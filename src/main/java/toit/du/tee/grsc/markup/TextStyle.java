package toit.du.tee.grsc.markup;

public enum TextStyle {

	PLAIN(' ', ' '),
	IGNORED('|', '|'),
	BOLD('<', '>'),
	ITALIC('/', '/'),
	STRIKETHROUGH('-', '-'),
	UNDERLINE('_', '_'),
	SUPERSCRIPT('^', '^');
	
	private char opening;
	private char closing;
	
	private TextStyle(char opening, char closing) {
		this.opening = opening;
		this.closing = closing;
	}
	
	public char opening() {
		return opening;
	}
	
	public char closing() {
		return closing;
	}
	
}
