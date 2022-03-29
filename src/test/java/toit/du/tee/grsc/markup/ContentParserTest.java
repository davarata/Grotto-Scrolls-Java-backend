package toit.du.tee.grsc.markup;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import toit.du.tee.grsc.rest.Content;
import toit.du.tee.grsc.rest.Node;

public class ContentParserTest {

   @Test
   public void ordered_list_with_no_parameter_creates_numeric_counters() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "1 One").
            add("li", "olist-item", "2 Two").
         build();

      String content = """
            # One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();

      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_numeric_1_counter_parameter_creates_numeric_counters() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "1 One").
            add("li", "olist-item", "2 Two").
         build();

      String content = """
            # {1} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_lowercase_a_counter_parameter_creates_lowercase_alphabetic_counters() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "a One").
            add("li", "olist-item", "b Two").
         build();

      String content = """
            # {a} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_uppercase_a_counter_parameter_creates_uppercase_alphabetic_counters() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "A One").
            add("li", "olist-item", "B Two").
         build();

      String content = """
            # {A} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_lowercase_i_counter_parameter_creates_lowercase_roman_numerals() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "i One").
            add("li", "olist-item", "ii Two").
         build();

      String content = """
            # {i} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_uppercase_i_counter_parameter_creates_uppercase_roman_numerals() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "I One").
            add("li", "olist-item", "II Two").
         build();

      String content = """
            # {I} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }

   @Test
   public void ordered_list_with_text_before_counter_parameter_shows_the_text() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", ":1 One").
            add("li", "olist-item", ":2 Two").
         build();

      String content = """
            # {:1} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_text_after_counter_parameter_shows_the_text() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "i) One").
            add("li", "olist-item", "ii) Two").
         build();

      String content = """
            # {i)} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_with_whitespace_and_text_around_counter_parameter_ignores_whitespace() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "[a] One").
            add("li", "olist-item", "[b] Two").
         build();

      String content = """
            # {    [a]\t  } One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_ignores_counter_parameter_when_counter_character_is_escaped() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "a One").
            add("li", "olist-item", "a Two").
         build();

      String content = """
            # {\\a} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_uses_second_counter_character_since_first_is_escaped() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "aa One").
            add("li", "olist-item", "ab Two").
         build();

      String content = """
            # {\\aa} One
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_shows_nested_ordered_list() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "1 One").
            add("ol", "ordered-list", "").
            toChildren().
               add("li", "olist-item", "a Alpha").
               add("li", "olist-item", "b Bravo").
               toParent().
            add("li", "olist-item", "2 Two").
         build();

      String content = """
            # {1} One
              # {a} Alpha
              # Bravo
              # {..}
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void ordered_list_continues_with_first_level_list_after_third_level_list_exits_to_first() {
      List<Node> expected = nodeListWithTitle("Test").
         add("ol", "ordered-list", "").
         toChildren().
            add("li", "olist-item", "1 One").
            add("ol", "ordered-list", "").
            toChildren().
               add("li", "olist-item", "a Alpha").
               add("li", "olist-item", "b Bravo").
               add("ol", "ordered-list", "").
               toChildren().
                  add("li", "olist-item", "i First").
                  add("li", "olist-item", "ii Second").
               toParent().
            toParent().
         add("li", "olist-item", "2 Two").
         build();

      String content = """
            # {1} One
              # {a} Alpha
              # Bravo
                # {i} First
                # Second
              # {..a}
            # Two""";

      ContentParser contentParser = new ContentParser(contentWithTitle("Test", content));
      List<Node> actual = contentParser.parse();
      
      assertEquals(expected, actual);
   }
   
   
   private static NodeBuilder nodeListWithTitle(String title) {
      return new NodeBuilder().
         add("h1", "title", "Test").
         add("hr", "heading-ruler", "");
   }
   
   private static Content contentWithTitle(String title, String content) {
      return new Content(title, content, null, null, null);
   }
   
}
