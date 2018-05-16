package GUI;

public class WordUtils {
	public static String capitalizeFully(String str, char... delimiters) {
		int delimLen = delimiters == null ? -1 : delimiters.length;
		if (str != null && str.length() != 0 && delimLen != 0) {
			str = str.toLowerCase();
			return capitalize(str, delimiters);
		} else {
			return str;
		}
	}
	
	private static String capitalize(String str, char... delimiters) {
		int delimLen = delimiters == null ? -1 : delimiters.length;
		if (str != null && str.length() != 0 && delimLen != 0) {
			char[] buffer = str.toCharArray();
			boolean capitalizeNext = true;
			
			for (int i = 0; i < buffer.length; ++i) {
				char ch = buffer[i];
				if (isDelimiter(ch, delimiters)) {
					capitalizeNext = true;
				} else if (capitalizeNext) {
					buffer[i] = Character.toTitleCase(ch);
					capitalizeNext = false;
				}
			}
			
			return new String(buffer);
		} else {
			return str;
		}
	}
	
	private static boolean isDelimiter(char ch, char[] delimiters) {
		if (delimiters == null) {
			return Character.isWhitespace(ch);
		} else {
			for (char delimiter : delimiters) {
				if (ch == delimiter) {
					return true;
				}
			}
			
			return false;
		}
	}
}
