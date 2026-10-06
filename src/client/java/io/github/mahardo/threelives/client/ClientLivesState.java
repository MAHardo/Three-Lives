package io.github.mahardo.threelives.client;

// The lives values last sent by the server. The HUD and death screen read them from here.
public final class ClientLivesState {
	private static int remaining;
	private static int max;

	private ClientLivesState() {
	}

	public static int remaining() {
		return remaining;
	}

	public static int max() {
		return max;
	}

	// The server has sent us values (a server without the mod never does).
	public static boolean isActive() {
		return max > 0;
	}

	public static boolean hasLivesLeft() {
		return isActive() && remaining > 0;
	}

	public static void update(int remaining, int max) {
		ClientLivesState.remaining = remaining;
		ClientLivesState.max = max;
	}
}
