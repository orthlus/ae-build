package art.aelaort.utils;

public class ColoredConsoleTextUtils {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET_REGEXP = "\\u001B\\[0m";
	public static final String ANSI_RED_REGEXP = "\\u001B\\[31m";
	public static final String ANSI_GREEN_REGEXP = "\\u001B\\[32m";

	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";

	public static String wrapColor(String color, String text) {
		return color + text + ANSI_RESET;
	}

	public static String wrapBlue(String text) {
		return ANSI_BLUE + text + ANSI_RESET;
	}

	public static String wrapGreen(String text) {
		return ANSI_GREEN + text + ANSI_RESET;
	}

	public static String wrapRed(String text) {
		return ANSI_RED + text + ANSI_RESET;
	}
}
