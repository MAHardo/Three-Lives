package io.github.mahardo.threelives.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.mahardo.threelives.LivesManager;

import me.lucko.fabric.api.permissions.v0.Permissions;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

// /threelives get|set|reset <player>.
// Each subcommand has a permission node that server admins can grant with a permissions mod such as LuckPerms.
// Without such a mod the nodes fall back to the vanilla operator level, the same as /gamemode.
public final class ThreeLivesCommand {
	public static final String GET_PERMISSION = "threelives.command.get";
	public static final String SET_PERMISSION = "threelives.command.set";
	public static final String RESET_PERMISSION = "threelives.command.reset";

	private static final PermissionLevel DEFAULT_LEVEL = PermissionLevel.GAMEMASTERS;

	private ThreeLivesCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("threelives")
				// The command is visible to everyone who may use at least one of the subcommands.
				.requires(source -> Permissions.check(source, GET_PERMISSION, DEFAULT_LEVEL)
						|| Permissions.check(source, SET_PERMISSION, DEFAULT_LEVEL)
						|| Permissions.check(source, RESET_PERMISSION, DEFAULT_LEVEL))
				.then(Commands.literal("get")
						.requires(Permissions.require(GET_PERMISSION, DEFAULT_LEVEL))
						.then(Commands.argument("player", EntityArgument.player())
								.executes(ThreeLivesCommand::get)))
				.then(Commands.literal("set")
						.requires(Permissions.require(SET_PERMISSION, DEFAULT_LEVEL))
						.then(Commands.argument("player", EntityArgument.player())
								.then(Commands.argument("lives", IntegerArgumentType.integer(1))
										.executes(ThreeLivesCommand::set))))
				.then(Commands.literal("reset")
						.requires(Permissions.require(RESET_PERMISSION, DEFAULT_LEVEL))
						.then(Commands.argument("player", EntityArgument.player())
								.executes(ThreeLivesCommand::reset))));
	}

	private static int get(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = EntityArgument.getPlayer(context, "player");
		int remaining = LivesManager.getRemainingLives(player);
		int max = LivesManager.getMaxLives(player.level().getServer());

		context.getSource().sendSuccess(() -> Component.translatableWithFallback(
				"commands.threelives.get", "%s has %s of %s lives left",
				player.getDisplayName(), remaining, max), false);
		return remaining;
	}

	private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = EntityArgument.getPlayer(context, "player");
		int lives = IntegerArgumentType.getInteger(context, "lives");
		int max = LivesManager.getMaxLives(player.level().getServer());

		// The maximum is the gamerule, which can change at any time, so it is checked here instead of in the argument.
		if (lives > max) {
			context.getSource().sendFailure(Component.translatableWithFallback(
					"commands.threelives.set.too_many", "Lives can be at most %s (gamerule threelives:lives)", max));
			return 0;
		}

		LivesManager.setRemainingLives(player, lives);
		context.getSource().sendSuccess(() -> Component.translatableWithFallback(
				"commands.threelives.set", "%s now has %s of %s lives",
				player.getDisplayName(), lives, max), true);
		return lives;
	}

	private static int reset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = EntityArgument.getPlayer(context, "player");
		int max = LivesManager.getMaxLives(player.level().getServer());

		LivesManager.resetDeaths(player);
		context.getSource().sendSuccess(() -> Component.translatableWithFallback(
				"commands.threelives.reset", "%s was reset to %s lives",
				player.getDisplayName(), max), true);
		return max;
	}
}
