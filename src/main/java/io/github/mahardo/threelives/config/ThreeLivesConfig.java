package io.github.mahardo.threelives.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.mahardo.threelives.ThreeLives;

import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Settings in config/three-lives.json. The file is read the first time this class is used (see the static block),
// so a changed file needs a game restart.
//
// defaultLives: start value of the gamerule threelives:lives for worlds created from now on.
//               Existing worlds keep their own value, because every world saves its gamerules.
// showHearts / showDeathScreenLine: client only, how the lives are displayed. Ignored on a dedicated server.
public final class ThreeLivesConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreeLives.MOD_ID);
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("three-lives.json");

	public static final int DEFAULT_LIVES = 3;

	// Not final: Gson fills them when reading the file, and the config screen changes them.
	public static int defaultLives = DEFAULT_LIVES;
	public static boolean showHearts = true;
	public static boolean showDeathScreenLine = true;

	// Runs once, the first time any code touches this class. It has to stay below the fields above:
	// Java initializes static fields and blocks from top to bottom, so the defaults are set before the file is read.
	// Because of this, code like ModGameRules always sees the values from the file, no matter who loads first.
	static {
		load();
	}

	private ThreeLivesConfig() {
	}

	// Reads the file. Only if it is missing or broken are the defaults used and the file (re)written.
	private static void load() {
		boolean readOk = false;
		if (Files.exists(FILE)) {
			try (Reader reader = Files.newBufferedReader(FILE)) {
				Values values = GSON.fromJson(reader, Values.class);
				if (values != null) {
					defaultLives = Math.max(1, values.defaultLives);
					showHearts = values.showHearts;
					showDeathScreenLine = values.showDeathScreenLine;
					readOk = true;
				}
			} catch (Exception e) {
				LOGGER.warn("Could not read {}, using the defaults", FILE, e);
			}
		}
		if (!readOk) {
			save();
		}
	}

	public static void save() {
		Values values = new Values();
		values.defaultLives = defaultLives;
		values.showHearts = showHearts;
		values.showDeathScreenLine = showDeathScreenLine;

		try (Writer writer = Files.newBufferedWriter(FILE)) {
			GSON.toJson(values, writer);
		} catch (IOException e) {
			LOGGER.warn("Could not write {}", FILE, e);
		}
	}

	// What the file looks like. The default values here are used for entries missing in the file.
	private static final class Values {
		int defaultLives = DEFAULT_LIVES;
		boolean showHearts = true;
		boolean showDeathScreenLine = true;
	}
}
