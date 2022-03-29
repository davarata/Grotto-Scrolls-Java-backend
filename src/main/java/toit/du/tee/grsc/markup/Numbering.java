package toit.du.tee.grsc.markup;

import java.util.Stack;

public class Numbering {

	private class Counter {
		
		String name;
		String[] template = new String[2];
		String style;
		int value = 0;
		
	}

	public static final char[] ESCAPE_CHARS = {'$', 'a', 'A', 'i', 'I', '1', ','};
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String[] TYPES = new String[] {"1", "a", "A", "i", "I"};
	
	private Stack<Counter> counters = new Stack<>();
	
	public void add(String templateStr) {
		Counter counter = new Counter();
		counter.name = String.valueOf(LOWERCASE.charAt(counters.size()));
		createTemplate(counter, templateStr);
		counters.push(counter);
	}
	
	public String getCounterText() {
		Counter counter = counters.peek();
		return counter.template[0] + getCounterValue(counter) + counter.template[1];
	}

	public void remove() {
		counters.pop();
	}
	
	public int remove(String name) {
		int total = 0;
		while (counters.size() > 1 && !counters.peek().name.equals(name)) {
			counters.pop();
			total++;
		}
		
		return total;
	}

	public int removeAll() {
		int total = counters.size();
		counters.clear();
		
		return total;
	}
	
	public boolean inUse() {
		return !counters.isEmpty();
	}
	
	private void createTemplate(Counter counter, String templateStr) {
		EscapedStr[] templateParts = new EscapedStr[2];
		EscapedStr bubbles = new EscapedStr(templateStr, ESCAPE_CHARS);
		
		int splitIndex = Integer.MAX_VALUE;
		for (String c : TYPES) {
			int index = bubbles.indexOf(c);
			while (index > 0 && bubbles.isCharAt(index - 1, '$')) {
				index = bubbles.indexOf(c, index + 1);
			}
			
			if (index >= 0 && index < splitIndex) {
				splitIndex = index;
			}
		}
		
		if (splitIndex != Integer.MAX_VALUE) {
			templateParts[0] = bubbles.substring(0, splitIndex);
			if (splitIndex + 1 == bubbles.length()) {
				templateParts[1] = EscapedStr.EMPTY;
			} else {
				templateParts[1] = bubbles.substring(splitIndex + 1);
			}
			
			counter.style = bubbles.substring(splitIndex, splitIndex + 1).toString();
		} else {
			templateParts[0] = bubbles;
			templateParts[1] = EscapedStr.EMPTY;
			counter.style = "";
		}

		for (int tIndex = 0; tIndex < 2; tIndex++) {
			for (int index = 0; index < counters.size(); index++) {
				int varIndex = templateParts[tIndex].indexOf("$" + LOWERCASE.charAt(index));
				if (varIndex >= 0) {
					Counter aCounter = counters.get(index);
					aCounter.value--;
					templateParts[tIndex].setString(
						templateParts[tIndex].substring(0, varIndex).toString(),
						getCounterValue(aCounter),
//						EscapedStr.encode(getCounterValue(aCounter), ESCAPE_CHARS),
						templateParts[tIndex].substring(varIndex + 2).toString());
				}
			}
			
			if (counter.template[tIndex] == null) {
				counter.template[tIndex] = templateParts[tIndex].interpret();
			}
		}
		
		if (templateStr == null || templateStr.equals("")) {
			counter.style = "1";
		}
	}
	
	private String getCounterValue(Counter counter) {
		if (counter.style.equals("1")) {
			return String.valueOf(++counter.value);
		} else if (counter.style.equals("a")) {
			if (counter.value > 25) {
				counter.value = counter.value % 26;
			}
			return String.valueOf(LOWERCASE.charAt(counter.value++));
		} else if (counter.style.equals("A")) {
			if (counter.value > 25) {
				counter.value = counter.value % 26;
			}
			return String.valueOf(UPPERCASE.charAt(counter.value++));
		} else if (counter.style.equals("i")) {
			return toRoman(++counter.value).toLowerCase();
		} else if (counter.style.equals("I")) {
			return toRoman(++counter.value);
		}
		
		return "";
	}
	
	private String toRoman(int number) {
		return "I".repeat(number).
			replace("IIIII", "V").
			replace("IIII", "IV").
			replace("VV", "X").
			replace("VIV", "IX").
			replace("XXXXX", "L").
			replace("XXXX", "XL").
			replace("LL", "C").
			replace("LXL", "XC").
			replace("CCCCC", "D").
			replace("CCCC", "CD").
			replace("DD", "M").
			replace("DCD", "CD");
	}
	
}

