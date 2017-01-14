package compiler.util;

/**
 * TokenTypeUtil
 * For lexical analyzer
 * Define several token types and their concrete values
 * Implements methods for checking the type of character/string
 * @author Moxin Chen
 * @version 1.0
 */

import java.util.Arrays;

public class TokenTypeUtil {
	// Types of token
	public static String TOKEN_TYPE_KEYWORD = "Keyword";
	public static String TOKEN_TYPE_IDENTIFIER = "Identifier";
	public static String TOKEN_TYPE_OPERATOR = "Operator";
	public static String TOKEN_TYPE_CONSTANT = "Constant";
	public static String TOKEN_TYPE_DELIMITER = "Delimiter";
	public static String TOKEN_TYPE_ERROR = "Error";
	
	// Array of keywords
	public static String[] ARRAY_KEYWORDS = {
			"package", "import",
			"class", "abstract", "enum", "interface", "extends", "implements",
			"public", "protected", "private", "static", "const", "synchronized", "void",
			"int", "short", "long", "float", "double", "boolean", "char", "byte",
			"if", "else", "switch", "case", "default", "while", "do", "for",
			"throw", "throws", "try", "catch", "finally", "return", "break", "continue",
			"super", "final", "instanceof", "this", "new", "goto", "assert"
	};
	
	// Array of unary operators
	public static char[] ARRAY_UNARY_OPERATORS = {
			'+', '-', '*', '/', '%', '>', '<', '=', '&', '|', '^', '!'
	};
	
	// Array of unary operators
	public static String[] ARRAY_BINARY_OPERATORS = {
			"+=", "-=", "*=", "/=", "%=", "^=", "&=", "|=",
			"++", "--", "&&", "||", "<<", ">>", 
			"==", "<=", ">=", "!="
	};
	
	// Array of delimiters
	public static char[] ARRAY_DELIMITERS = {
			'.', ',', ';', ':', '"', '\'', '\\',
			'(', ')', '[', ']', '{', '}'
	};
	
	// Array of word connectors
	public static char[] ARRAY_CONNECTORS = {
			'_' 
	};
	
	// Check if the source string is a keyword
	public static boolean isKeyWord(String src) {
		return Arrays.asList(ARRAY_KEYWORDS).contains(src);
	}
	
	// Check if the source character is an unary operator  
	public static boolean isUnaryOperator(char src) {
		for (int i = 0; i < ARRAY_UNARY_OPERATORS.length; i++) {
			if (src == ARRAY_UNARY_OPERATORS[i]) {
				return true;
			}
		}
		return false;
	}
	
	// Check if the source character is a binary operator  
	public static boolean isBinaryOperator(String src) {
		return Arrays.asList(ARRAY_BINARY_OPERATORS).contains(src);
	}
	
	// Check if the source character is a delimiter
	public static boolean isDelimiter(char src) {
		for (int i = 0; i < ARRAY_DELIMITERS.length; i++) {
			if (src == ARRAY_DELIMITERS[i]) {
				return true;
			}
		}
		return false;
	}
	
	// Check if the source character is a word connector
		public static boolean isConnector(char src) {
			for (int i = 0; i < ARRAY_CONNECTORS.length; i++) {
				if (src == ARRAY_CONNECTORS[i]) {
					return true;
				}
			}
			return false;
		}
	
	// Check if the source character is a letter
	public static boolean isLetter(char src) {
		return Character.isLetter(src);
	}
	
	// Check if the source character is a letter
	public static boolean isDigit(char src) {
		return Character.isDigit(src);
	}
	
	// Check if the source character is a whitespace
	public static boolean isWhitespace(char src) {
		return (Character.isWhitespace(src) || ((int)src == 9));
	}
}
