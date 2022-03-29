package toit.du.tee.grsc.markup;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import toit.du.tee.grsc.rest.Node;

public class NodeBuilder {

	private List<Node> nodes = new LinkedList<>();
	private Stack<List<Node>> hierarchy = new Stack<>();
	
	public NodeBuilder add(String type, String group, String value) {
		nodes.add(new Node(type, group, "", value));
		
		return this;
	}
	
	public NodeBuilder add(String type, String group, String properties, String value) {
		nodes.add(new Node(type, group, properties, value));
		
		return this;
	}
	
	public NodeBuilder toChildren() {
		List<Node> children = new LinkedList<>();
		nodes.get(nodes.size() - 1).setChildren(children);
		hierarchy.push(nodes);
		nodes = children;
		
		return this;
	}
	
	public NodeBuilder toParent() {
		nodes = hierarchy.pop();
		
		return this;
	}

	public List<Node> build() {
		while (!hierarchy.isEmpty()) {
			nodes = hierarchy.pop();
		}
		return nodes;
	}
}
