package toit.du.tee.grsc.markup;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class EscapedStr {

	public static final EscapedStr EMPTY = new EscapedStr("");
	
	private String string;
	private char[] mask;
	private char[] escaped;
	
	public EscapedStr(String string, String escapeChars) {
		this(string, escapeChars.toCharArray());
	}

	public EscapedStr(String string, char[] escapeChars) {
		this.string = string;
		escaped = Arrays.copyOf(escapeChars, escapeChars.length + 1);
		escaped[escaped.length - 1] = '\\';
		createMask();
	}
	
	private EscapedStr() { }
	
	private EscapedStr(String string) {
		this.string = string;
	}

	public static String encode(String string, char[] escapedChars) {
		String encoded = string;
		int index = encoded.lastIndexOf("\\");
		while (index >= 0) {
			encoded = encoded.substring(0, index) + "\\\\" + encoded.substring(index + 1);
			index = encoded.lastIndexOf("\\", index - 1);
		}

		if (escapedChars != null) {
			for (char c : escapedChars) {
				index = encoded.lastIndexOf(c);
				while (index >= 0) {
					encoded = encoded.substring(0, index) + "\\" + String.valueOf(c) + encoded.substring(index + 1);
					index = encoded.lastIndexOf(c, index - 1);
				}
			}
		}
		
		return encoded;
		
	}
	
	public char[] getEscaped() {
		return Arrays.copyOf(escaped, escaped.length);
	}
	
	public boolean isCharAt(int index, char c) {
		return string.charAt(index) == c && mask[index] != 'x'; 
	}

	public void setString(String... strings) {
		this.string = Arrays.stream(strings).collect(Collectors.joining());
		createMask();
	}
	
	public int indexOf(String c) {
		int index = string.indexOf(c);
		while (index != -1 && mask[index] == 'x') {
			index = string.indexOf(c, index + 1);
		}

		return index;
	}
	
	public int indexOf(String c, int index) {
		index = string.indexOf(c, index); // Not index + 1, since we want to start searching from index (as per parameter).
		while (index != -1 && mask[index] == 'x') {
			index = string.indexOf(c, index + 1);
		}

		return index;
	}

//	private boolean notMasked(int index, int length) {
//		for (int pos = index ; pos < index + length; pos++) {
//			if (mask[pos] == 'x') {
//				return false;
//			}
//		}
//		
//		return true;
//	}
	
	public boolean startsWith(String prefix) {
		return string.startsWith(prefix);
	}
	
	public EscapedStr substring(int beginIndex) {
		EscapedStr eStr = new EscapedStr();
		eStr.string = this.string.substring(beginIndex);
		eStr.mask = Arrays.copyOfRange(this.mask, beginIndex, this.mask.length);
		if (eStr.mask.length == 1) {
			eStr.mask[0] = ' ';
		} else if (eStr.mask.length > 1) {
			if (eStr.mask[0] == 'x' && eStr.mask[1] != 'x') {
				eStr.createMask();
			}
		}
		eStr.mask = mask;
		
		return eStr;
	}

	public EscapedStr substring(int beginIndex, int endIndex) {
		EscapedStr eStr = new EscapedStr();
		eStr.string = this.string.substring(beginIndex, endIndex);
		eStr.escaped = Arrays.copyOfRange(this.escaped, beginIndex, endIndex);
		if (eStr.escaped.length == 1) {
			eStr.escaped[0] = ' ';
		} else if (eStr.escaped.length > 1) {
			if (eStr.escaped[0] == 'x' && eStr.escaped[1] != 'x') {
				eStr.createMask();
			}
		}
		eStr.mask = mask;
		
		return eStr;
	}
	
	public EscapedStr strip() {
		EscapedStr eStr = new EscapedStr();
		eStr.string = string.stripLeading();
		int beginIndex = string.length() - eStr.string.length();
		eStr.string = eStr.string.stripTrailing();
		eStr.mask = Arrays.copyOfRange(mask, beginIndex, beginIndex + eStr.string.length());

		if (eStr.mask.length == 1) {
			eStr.mask[0] = ' ';
		} else if (eStr.mask.length > 1) {
			if (eStr.mask[0] == 'x' && eStr.mask[1] != 'x') {
				eStr.createMask();
			}
		}
		eStr.mask = mask;
		
		return eStr;
	}

	public char charAt(int index) {
		return string.charAt(index);
	}
	
	public boolean isBlank() {
		return string.isBlank();
	}
	
	public boolean equals(String other) {
		return string.equals(other);
	}

	@Override
	public String toString() {
		return string;
	}
	
	public String interpret() {
		String interpreted = string;
		while (interpreted.indexOf("\\\\") >= 0) {
			interpreted = interpreted.substring(0, interpreted.indexOf("\\\\")) + interpreted.substring(interpreted.indexOf("\\\\") + 1);
		}

		if (escaped != null) {
			for (char c : escaped) {
				String str = "\\" + String.valueOf(c);
				while (interpreted.indexOf(str) >= 0) {
					interpreted = interpreted.substring(0, interpreted.indexOf(str)) + interpreted.substring(interpreted.indexOf(str) + 1);
				}
			}
		}
		
		return interpreted;
	}
	
	public int length() {
		return string.length();
	}
	
	private void createMask() {
		mask = string.toCharArray();
		
		for (int mIndex = 0; mIndex < mask.length - 1; mIndex++) {
			if (mask[mIndex] == '\\') {
				for (int eIndex = 0; eIndex < escaped.length; eIndex++) {
					if (mask[mIndex + 1] == escaped[eIndex]) {
						mask[mIndex] = 'x';
						mask[mIndex + 1] = 'x';
						mIndex++;
						break;
					} else {
						mask[mIndex] = ' ';
					}
				}
			} else {
				mask[mIndex] = ' ';
			}
		}
	}
}
