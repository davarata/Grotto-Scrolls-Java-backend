package toit.du.tee.grsc.rest;

import java.util.List;
import java.util.Objects;

public class Node {
	
	public static Node STOP_LIST = new Node("sl", "", "");
	
	private String type;
	private String groups;
	private String properties;
	private String value;
	private List<Node> children;
	
	public Node(String type, String groups, String properties, String value, List<Node> children) {
		this.type = type;
		this.groups = groups;
		this.properties = properties;
		this.value = value;
		this.children = children;
	}

	public Node(String type, String groups, String properties, String value) {
		this(type, groups, properties, value, null);
	}

	public Node(String type, String groups, String value) {
		this(type, groups, "", value, null);
	}
	
	public void addGroup(String group) {
		if (groups == null) {
			groups = "";
		}
		
		groups += group;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	public String getProperties() {
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "Element [type=" + type + ", groups=" + groups + ", style=" + properties + ", value=" + value + ", children="
				+ children + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(children, groups, properties, type, value);
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Node)) {
			return false;
		}
		Node other = (Node) object;
		return Objects.equals(children, other.children) &&
			Objects.equals(groups, other.groups) &&
			Objects.equals(properties, other.properties) &&
			Objects.equals(type, other.type) &&
			Objects.equals(value, other.value);
	}

	
}
