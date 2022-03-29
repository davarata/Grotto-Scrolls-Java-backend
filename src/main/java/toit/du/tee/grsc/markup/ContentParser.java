package toit.du.tee.grsc.markup;

import static toit.du.tee.grsc.markup.TextStyle.BOLD;
import static toit.du.tee.grsc.markup.TextStyle.IGNORED;
import static toit.du.tee.grsc.markup.TextStyle.ITALIC;
import static toit.du.tee.grsc.markup.TextStyle.PLAIN;
import static toit.du.tee.grsc.markup.TextStyle.STRIKETHROUGH;
import static toit.du.tee.grsc.markup.TextStyle.SUPERSCRIPT;
import static toit.du.tee.grsc.markup.TextStyle.UNDERLINE;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import toit.du.tee.grsc.rest.Content;
import toit.du.tee.grsc.rest.Node;

public class ContentParser {
	
	private record Section(int begin, int end) { }
	
	private class SectionComparator implements Comparator<Section> {

		@Override
		public int compare(Section section1, Section section2) {
			return section1.begin() - section2.begin();
		}

	}
	
	private Content content;
	private int beginIndex = -1;
	private int endIndex = -1;
	
	private Node previousNode;
	private Node currentNode  = new Node("", "", "");
	private Queue<Node> nextNodes = new LinkedList<>();
	
	private Stack<List<Node>> hierarchy = new Stack<>();
	private List<Node> nodes = new LinkedList<>();
	
	private Numbering numbering = new Numbering();
	private boolean useParagraphInItem = false;
	private List<String> bulletType = new LinkedList<>();
	
	public ContentParser(Content content) {
		this.content = content;
		
	}

	public List<Node> parse() {
		nodes.add(new Node("h1", "title", content.title()));
		nodes.add(new Node("hr", "heading-ruler", ""));

		while (hasNext()) {
			Node node = next();
			
			if (node.getType().equals("p")) {
				if (!parentIs("p")) {
					if (previousNode.getType().startsWith("h")) {
						node.addGroup(";text-after-heading");
					} else if (previousNode.getType().startsWith("sl")) {
						node.addGroup(";text-after-list");
					}
					node.setChildren(new LinkedList<>());
					nodes.add(node);
					hierarchy.push(nodes);
					nodes = node.getChildren();					
				} else {
					nodes.add(new Node("br", "", ""));
				}
				
				String text = node.getValue();
				node.setValue("");
				// Combine all non-empty text lines ending with a backslash.
				while (!text.endsWith("\\\\") && text.endsWith("\\") && hasNext() && peek().getType().equals("p")) {
					text = text.substring(0, text.length() - 1) + " " + next().getValue();
				}

				if (!text.endsWith("\\\\") && text.endsWith("\\")) {
					if (hasNext() && peek().getType().equals("br")) {
						next(); // The effect is that the backslash removes the blank line.
					}
					// If the next node is not a blank line (br), remove the backslash since backslashes only affects paragraphs and blank lines. It is
					// ignored when used in combination with anything else.
					text = text.substring(0, text.length() - 1);
				}
				
				nodes.addAll(getTextFormatting(text));
				continue;
			}
			
			if (node.getType().equals("ol") || node.getType().equals("ul")) {
				if (parentIs("p")) {
					nodes = hierarchy.pop();
					Node theNode = nodes.get(nodes.size() - 1);
					theNode.addGroup(";text-before-list");
				}
				node.setChildren(new LinkedList<>());
				nodes.add(node);
				hierarchy.push(nodes);
				nodes = node.getChildren();
				continue;
			}
			
			if (node.getType().equals("li")) {
            node.setChildren(getTextFormatting(node.getValue()));
			   node.setValue("");
			}
			
			if (node.equals(Node.STOP_LIST)) {
				nodes = hierarchy.pop();
				continue;
			}
			
			if (node.getType().equals("br")) {
				while (hasNext() && peek().getType().equals("br")) {
					next();
				}

				if (!hierarchy.isEmpty() ) {
					nodes = hierarchy.pop();
					continue;
				}
				
				if (previousNode.getType().equals("sl")) {
					continue;
				}
			}
			
			if (node.getType().startsWith("h")) {
				while (!hierarchy.isEmpty()) {
					nodes = hierarchy.pop();
				}
			}
			nodes.add(node);
			if (node.getType().equals("h1") || node.getType().equals("h2")) {
				nodes.add(new Node("hr", "heading-ruler", ""));
			}
		}
		
		while (!hierarchy.isEmpty()) {
			nodes = hierarchy.pop();
		}
		
		return nodes;
	}
	
	private Node get() {
		return currentNode;
	}
	
	private Node next() {
		if (nextNodes.isEmpty()) {
			parseNext();
		}
		
		previousNode = currentNode;
		currentNode = nextNodes.remove();
		return currentNode;
	}
	
	private Node peek() {
		if (nextNodes.isEmpty()) {
			parseNext();
		}
		
		return nextNodes.peek();
	}
	
	private boolean hasNext() {
		return !nextNodes.isEmpty() || endIndex < content.content().length();
	}
	
	private void parseNext() {
		beginIndex = endIndex + 1;
		endIndex = content.content().indexOf('\n', beginIndex);
		if (endIndex == -1) {
			endIndex = content.content().length();
		}
		
		String text = content.content().substring(beginIndex, endIndex).strip();
		
		if (text.startsWith("=")) {
			int level = 0;
			while (text.charAt(++level) == '=');
			nextNodes.add(new Node("h" + level, "heading" + level, text.substring(level).strip()));
		} else if (text.startsWith("#")) {
			parseOrderedNode(text);
		} else if (text.startsWith("*")) {
			parseUnorderedNode(text);
		} else if (text.isBlank()) {
			if (parentIs("p")) {
				
			}
			if (parentIs("ol")) {
				if (!useParagraphInItem) {
					int total = numbering.removeAll();
					for (int count = 0; count < total; count++) {
						nextNodes.add(Node.STOP_LIST);
					}
				}
			} else if (parentIs("ul")) {
				if (!useParagraphInItem) {
					int total = bulletType.size();
					for (int count = 0; count < total; count++) {
						nextNodes.add(Node.STOP_LIST);
					}
					bulletType.clear();
				}
			}
			nextNodes.add(new Node("br", "", ""));
		} else {
			if (!useParagraphInItem) {
				if (parentIs("ol")) {
					int total = numbering.removeAll();
					for (int count = 0; count < total; count++) {
						nextNodes.add(Node.STOP_LIST);
					}
				} else if (parentIs("ul")) {
					int total = bulletType.size();
					for (int count = 0; count < total; count++) {
						nextNodes.add(Node.STOP_LIST);
					}
					bulletType.clear();
				}
			}
			nextNodes.add(new Node("p", "text", text.strip()));
		}
	}

	private void parseOrderedNode(String textStr) {
		useParagraphInItem = false;
		
		textStr = textStr.substring(1).strip();
		
		// Extract the parameters
		EscapedStr text = new EscapedStr(textStr, "{}$aAiI1,");
		if (text.startsWith("{") && text.indexOf("}") > 0) {
			int index = text.indexOf("}");
			EscapedStr parameter = text.substring(1, index);
			
			// The first parameter.
			if (index + 1 == text.length()) {
				text = EscapedStr.EMPTY;
			} else {
				text = text.substring(index + 1).strip();
			}
			
			index = parameter.indexOf(",");
			if (index > 0 && parameter.substring(0, index).strip().equals("p")) {
				useParagraphInItem = true;
			}
			
			if (index == -1 && parameter.equals("p")) {
				useParagraphInItem = true;
				parameter = EscapedStr.EMPTY;
			} else {
				if (index + 1 == parameter.length()) {
					parameter = EscapedStr.EMPTY;
				} else {
					parameter = parameter.substring(index + 1).strip();
				}
			}
			
			// The second parameter.
			if (parameter.startsWith("..")) {
				useParagraphInItem = false;
				nextNodes.add(Node.STOP_LIST);
				if (parameter.equals("..")) {
					numbering.remove();
				} else {
					// Change the code to add more than one nextNode (thus create a list for it), and add multiple Node.STOP_EL.
					// The remove method can return how many pops was performed. 
					int total = numbering.remove(parameter.substring(2).toString()) - 1;
					for (int count = 0; count < total; count++) {
						nextNodes.add(Node.STOP_LIST);
					}
				}
			} else {
				if (parameter.length() > 0 || !useParagraphInItem) {
					nextNodes.add(new Node("ol", "ordered-list", ""));
					if (parameter.equals("")) {
						numbering.add("1");
					} else {
						numbering.add(parameter.toString());
					}
				}
			}
		}
		
		// For when a top-level ordered list created without using any parameters.
		if (!parentIs("ol") && (nextNodes.isEmpty() || !nextNodes.peek().getType().equals("ol"))) {
			nextNodes.add(new Node("ol", "ordered-list", ""));
			numbering.add("1");
			
			if (text.isBlank()) {
				nextNodes.add(new Node("li", "olist-item", numbering.getCounterText()));
			}
		}
		
		if (!text.isBlank()) {
			nextNodes.add(new Node("li", "olist-item", numbering.getCounterText() + " " + text));
		}

		return;
	}
	
	private void parseUnorderedNode(String textStr) {
		useParagraphInItem = false;
		
		textStr = textStr.substring(1).strip();
		
		if (textStr.startsWith("{") && textStr.indexOf("}") > 0) {
			int index = textStr.indexOf("}");
			String parameter = textStr.substring(1, index).strip();
			
			// The first parameter.
			if (index + 1 == textStr.length()) {
				textStr ="";
			} else {
				textStr = textStr.substring(index + 1).strip();
			}
			
			index = parameter.indexOf(",");
			if (index > 0 && parameter.substring(0, index).strip().equals("p")) {
				useParagraphInItem = true;
			}
			
			if (index == -1 && parameter.equals("p")) {
				useParagraphInItem = true;
				parameter = "";
			} else {
				if (index + 1 == parameter.length()) {
					parameter = "";
				} else {
					parameter = parameter.substring(index + 1);
				}
			}
			
			// The second parameter.
			if (parameter.startsWith("..")) {
				useParagraphInItem = false;
				nextNodes.add(Node.STOP_LIST);
				if (parameter.equals("..")) {
					bulletType.remove(bulletType.size() - 1);
				} else {
					for (int count = 0; count < bulletType.size(); count++) {
						nextNodes.add(Node.STOP_LIST);
					}
					bulletType.clear();
				}
			} else {
				if (parameter.length() > 0 || !useParagraphInItem) {
					nextNodes.add(new Node("ul", "unordered-list", ""));
					bulletType.add(parameter);
				}
			}
		}
		
		if (bulletType.size() == 0) {
			nextNodes.add(new Node("ul", "unordered-list", ""));
			bulletType.add("*");			
			if (textStr.isBlank()) {
				String style = switch (bulletType.get(bulletType.size() - 1)) {
					case "o": yield "circle";
					case "+": yield "square";
					case "v": yield "disclosure-open";
					case ">": yield "disclosure-closed";
					default: yield "disc";
				};
				nextNodes.add(new Node("li", "ulist-item", "list-style: " + style + ";", ""));
			}
		}
		
		if (!textStr.isBlank()) {
			String style = switch (bulletType.get(bulletType.size() - 1)) {
				case "o": yield "circle";
				case "+": yield "square";
				case "v": yield "disclosure-open";
				case ">": yield "disclosure-closed";
				default: yield "disc";
			};
			nextNodes.add(new Node("li", "ulist-item", "list-style: " + style + ";", textStr));
		}
	
		return;
	}

	private boolean parentIs(String type) {
		if (!hierarchy.isEmpty() && !hierarchy.peek().isEmpty()) {
			return hierarchy.peek().get(hierarchy.peek().size() - 1).getType().equals(type);
		}
		
		return false;
	}
	
	private String removeEscaped(String text) {
		// Cannot get replaceAll(...) to work for \\
		while (text.indexOf("\\\\") >= 0) {
			text = text.substring(0, text.indexOf("\\\\")) + text.substring(text.indexOf("\\\\") + 1);
		}
		
		return text.replaceAll("\\\\=", "=").
			replaceAll("\\\\\\[", "[").
			replaceAll("\\\\]", "]").
			replaceAll("\\\\<", "<").
			replaceAll("\\\\>", ">").
			replaceAll("\\\\/", "/").
			replaceAll("\\\\-", "-").
			replaceAll("\\\\_", "_").
			replaceAll("\\\\\\^", "^").
			replaceAll("\\\\@", "@").
			replaceAll("\\\\#", "#").
			replaceAll("\\\\\\*", "*").
			replaceAll("\\\\\\{", "{").
			replaceAll("\\\\}", "}");
	}
	
	private List<Node> getTextFormatting(String text) {
		List<Node> nodes = new LinkedList<>();
		
		Map<Section, List<TextStyle>> sections = findSections(text);
		for (Section section : sections.keySet().stream().sorted(new SectionComparator()).toList()) {
			String type = "span";
			String groups = "";
			String properties = "";
			
			List<TextStyle> styles = sections.get(section);
			
			if (!styles.contains(IGNORED)) {
				if (styles.contains(BOLD)) {
					properties += "font-weight: bold;";
				}
				if (styles.contains(ITALIC)) {
					properties += "font-style: italic;";
				}
				if (styles.contains(STRIKETHROUGH)) {
					properties += "text-decoration: line-through";
					if (styles.contains(UNDERLINE)) {
						properties += " underline;";
					} else {
						properties += ";";
					}
				} else {
					if (styles.contains(UNDERLINE)) {
						properties += "text-decoration: underline;";
					}
				}
				if (styles.contains(SUPERSCRIPT)) {
					type = "sup";
					properties += "font-size: 0.7em";
				}
			}
			
			nodes.add(new Node(type, groups, properties, removeEscaped(text.substring(section.begin(), section.end())))); 
		}
		
		return nodes;
	}
	
	private Map<Section, List<TextStyle>> findSections(String text) {
		Map<Section, List<TextStyle>> sections = new HashMap<>();
		sections.put(new Section(0, text.length()), Arrays.asList(PLAIN));
		
		for (TextStyle style : TextStyle.values()) {
			if (style == PLAIN) {
				continue;
			}
			
			int closingIndex = -1;
			int openingIndex = -1;
			while (true) {
				openingIndex = text.indexOf(style.opening(), closingIndex + 1);
				if (openingIndex == -1) {
					break;
				}
				if (openingIndex > 0 && text.charAt(openingIndex - 1) == '\\') {
					closingIndex = openingIndex;
					continue;
				}
				
				closingIndex = text.indexOf(style.closing(), openingIndex + 1);
				while (true) {
					if (closingIndex == -1) {
						break;
					}
					if (text.charAt(closingIndex - 1) != '\\') {
						break;
					}
					
					closingIndex = text.indexOf(style.closing(), closingIndex + 1);
				}
				
				if (closingIndex == -1) {
					break;
				}
				
				insertSection(sections, openingIndex, closingIndex, style);
			}
		}
		
		return sections;
	}

	private void insertSection(Map<Section, List<TextStyle>> sections, int openingIndex, int closingIndex, TextStyle style) {
		Section firstSection = null;
		Section lastSection = null;
		for (Section section: sections.keySet().stream().sorted(new SectionComparator()).toList()) {
			if (section.begin() <= openingIndex && openingIndex <= section.end()) {
				firstSection = section;
			}
			
			if (section.begin() <= closingIndex && closingIndex <= section.end()) {
				lastSection = section;
				break;
			}
			
			if (firstSection != null && firstSection != section) {
				sections.get(section).add(style);
			}
		}
		
		List<TextStyle> styles = new LinkedList<>(sections.get(firstSection));
		styles.add(style);
		
		if (firstSection.begin() < openingIndex) {
			sections.put(new Section(firstSection.begin(), openingIndex), new LinkedList<>(sections.get(firstSection)));
		}
		
		if (firstSection == lastSection) {
			if (openingIndex + 1 < closingIndex) {
				sections.put(new Section(openingIndex + 1, closingIndex), styles);
			}
		} else {
			if (openingIndex + 1 < firstSection.end()) {
				sections.put(new Section(openingIndex + 1, firstSection.end()), styles);
			}
			
			if (lastSection.begin() + 1 < closingIndex) {
				styles = new LinkedList<>(sections.get(lastSection));
				styles.add(style);
				sections.put(new Section(lastSection.begin(), closingIndex), styles);
			}
		}
		
		if (closingIndex < lastSection.end()) {
			sections.put(new Section(closingIndex + 1, lastSection.end()), sections.get(lastSection));
		}
		
		sections.remove(firstSection);
		sections.remove(lastSection);
	}
}

