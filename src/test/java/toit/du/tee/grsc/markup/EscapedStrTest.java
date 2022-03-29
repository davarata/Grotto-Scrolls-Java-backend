package toit.du.tee.grsc.markup;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EscapedStrTest {

   @Test
   public void toString_returns_original_string_when_not_containing_escape_character() {
      String expected = "abcdef";
      String actual = new EscapedStr(expected, new char[]{'-'}).toString();
      
      assertEquals(expected, actual);
   }

   @Test
   public void toString_returns_original_string_when_containing_escape_character() {
      String expected = "abc-def";
      String actual = new EscapedStr(expected, new char[]{'-'}).toString();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void toString_returns_original_string_when_containing_escaped_escape_character() {
      String expected = new StringBuilder("abc").append('\\').append("-def").toString(); // abc\-def
      String actual = new EscapedStr(expected, new char[]{'-'}).toString();
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void _1_param_substring_mimics_String_method_when_not_containing_escape_character() {
      int index = 3;
      String expected = "abcdef";
      String actual = new EscapedStr(expected, new char[]{'-'}).substring(index).toString();
      expected = expected.substring(index);
      
      assertEquals(expected, actual);
   }

   @Test
   public void _1_param_substring_mimics_String_method_when_containing_escape_character() {
      int index = 3;
      String expected = "abc-def";
      String actual = new EscapedStr(expected, new char[]{'-'}).substring(index).toString();
      expected = expected.substring(index);
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void _1_param_substring_mimics_String_method_when_containing_escaped_escape_character() {
      int index = 3;
      String expected = new StringBuilder("abc").append('\\').append("-def").toString(); // abc\-def
      String actual = new EscapedStr(expected, new char[]{'-'}).substring(index).toString();
      expected = expected.substring(index);
      
      assertEquals(expected, actual);
   }
   
   @Test
   public void _1_param_substring_mimics_String_method_when_splitting_escaped_string_on_backslash() {
      int index = 4;
      String expected = new StringBuilder("abc").append('\\').append("-def").toString(); // abc\-def
      String actual = new EscapedStr(expected, new char[]{'-'}).substring(index).toString();
      expected = expected.substring(index);
      
      assertEquals(expected, actual);
   }

   @Test
   public void strip_mimics_String_method_when_not_containing_escape_character() {
      String expected = " \t abcdef\n  ";
      String actual = new EscapedStr(expected, new char[]{'-'}).strip().toString();
      expected = expected.strip();
      
      assertEquals(expected, actual);
   }

   @Test
   public void strip_mimics_String_method_when_containing_escape_character() {
      String expected = " \t abc-def\n  ";
      String actual = new EscapedStr(expected, new char[]{'-'}).strip().toString();
      expected = expected.strip();
      
      assertEquals(expected, actual);
   }

   @Test
   public void strip_mimics_String_method_when_containing_escaped_escape_character() {
      String expected = new StringBuilder(" \t abc").append('\\').append("-def\n  ").toString(); // ' \t abc\-def\n  '
      String actual = new EscapedStr(expected, new char[]{'-'}).strip().toString();
      expected = expected.strip();
      
      assertEquals(expected, actual);
   }
   
	@Test
	public void indexOf_recognizes_that_the_backslash_is_escaped_and_not_the_dollar_sign() {
		char[] characters = {'{', '\\', '\\', '$', 'a', '$'}; // {\\$a$
		EscapedStr string = new EscapedStr(String.copyValueOf(characters), "$aAiI1");
		
		assertEquals(3, string.indexOf("$"));
	}
	
	@Test
	public void indexOf_ignores_the_escaped_dollar_sign() {
		char[] characters = {'{', '\\', '$', 'a', '$'}; // {\$a$
		EscapedStr string = new EscapedStr(String.copyValueOf(characters), "$aAiI1");
		
		assertEquals(4, string.indexOf("$"));
	}

}
