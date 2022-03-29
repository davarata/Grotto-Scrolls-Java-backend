package toit.du.tee.grsc.manager;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import toit.du.tee.grsc.rest.Node;

public class NavigationCreator {

   private class Counter {
      int value = 0;
   }
   
   private Stack<List<Node>> hierarchy = new Stack<>();
   private List<Node> navigationContent = new LinkedList<>();
   private Stack<Counter> numbering = new Stack<>();
   
   public List<Node> get(List<Node> nodes) {
      if (nodes == null || nodes.isEmpty()) {
         return navigationContent;
      }
      navigationContent.add(new Node("p", "navigation-title;selected-title", nodes.get(0).getValue()));
      nodes = nodes.stream().filter(n -> n.getGroups().startsWith("heading") && !n.getGroups().equals("heading-ruler")).toList();

      increaseDepth();
      for (Node node : nodes) {
         // hierarchy.size() will be the same as the current heading depth, so use it to determine if a new ol or li should be added.
         int headingLevel = Integer.valueOf(node.getGroups().substring(7));
         
         if (headingLevel == hierarchy.size()) {
            addHeading(node.getValue());
         } else if (headingLevel > hierarchy.size()) {
            increaseDepth();
            addHeading(node.getValue());
         } else {
            while (headingLevel != hierarchy.size()) {
               numbering.pop();
               navigationContent = hierarchy.pop();
            }
            addHeading(node.getValue());
         }
      }
      
      while (!hierarchy.isEmpty()) {
         navigationContent = hierarchy.pop();
      }
      
      return navigationContent;
   }
   
   private void addHeading(String value) {
      numbering.peek().value++;
      
      Node heading = new Node("li", "navigation-heading", "", "", new LinkedList<>());
      heading.getChildren().add(new Node("span", "", numbering.stream().map(v -> String.valueOf(v.value)).collect(Collectors.joining("."))));
//      heading.getChildren().add(new Node("a", "navigation-heading-text", " " + value));
      heading.getChildren().add(new Node("span", "navigation-heading-text", " " + value));
      
      navigationContent.add(heading);
   }
   
   private void increaseDepth() {
      numbering.add(new Counter());
      Node headingContainer = new Node("ol", "ordered-list", "");
      headingContainer.setChildren(new LinkedList<>());
      navigationContent.add(headingContainer);
      hierarchy.push(navigationContent);
      navigationContent = headingContainer.getChildren();
   }
   
}
