package compiler.lexicalanalyzer;

/**
 * LexAnalyzer
 * Main program for Lexical Analyzer
 * @param srcPath	Source File Path
 * @param optPath	Output File Path
 * @author Moxin Chen
 * @version 2.0
 * Update: Concatenates the separate words into one identifier (1.0)
 */

import java.util.ArrayList;

import compiler.util.FileIOUtil;
import compiler.util.TokenTypeUtil;

public class LexAnalyzer {
	
	private StringBuffer sBuffer = new StringBuffer();
	private int bufferIdx = 0;
	private char ch;
	private char nextCh;
	private String str = "";
	
	private ArrayList<String> typeList = new ArrayList<String>();
	private ArrayList<String> valueList = new ArrayList<String>();
	private String strOutput = "";
	
	public LexAnalyzer(String srcPath, String optPath) {
		
		// Read source file content into a string buffer
		FileIOUtil.readFromFile(sBuffer, srcPath);
		
		// Begin to analyze
		analyze();
		
		// Output for testing
		for (int i = 0; i < typeList.size(); i++) {
			strOutput = strOutput + "< " + typeList.get(i) + ", " + valueList.get(i) + " >";
			strOutput += '\n';
		}
		
		FileIOUtil.write2File(strOutput, optPath);
	}
	
	public void analyze() {
		while (bufferIdx < sBuffer.length()) {
			// Read a character
			ch = sBuffer.charAt(bufferIdx++);

			// Check the type of the current character
			if (TokenTypeUtil.isWhitespace(ch)) {
				// Skip the whitespace
				continue;
			} else if (TokenTypeUtil.isLetter(ch)
					|| TokenTypeUtil.isConnector(ch)) {
				// The current character is a letter or a connector
				// This implies that it may be a keyword or a identifier
				str = "";
				str += ch;
				nextCh = sBuffer.charAt(bufferIdx++);
				
				// Append the successor letter or connector, if exists
				while(TokenTypeUtil.isLetter(nextCh)
						|| TokenTypeUtil.isDigit(nextCh)
						|| TokenTypeUtil.isConnector(nextCh)) {
					str += nextCh;
					nextCh = sBuffer.charAt(bufferIdx++);
				}
				bufferIdx--;
				
				// Check whether the letter or word is a keyword or a identifier
				if (TokenTypeUtil.isKeyWord(str)) {
					typeList.add(TokenTypeUtil.TOKEN_TYPE_KEYWORD);
					valueList.add(str);
				} else {
					typeList.add(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER);
					valueList.add(str);
				}
			} else if (TokenTypeUtil.isDigit(ch)) {
				// The current character is a digit
				boolean loopFlag = false;
				boolean errorFlag = false;
				str = "";
				str += ch;
				nextCh = sBuffer.charAt(bufferIdx++);
				
				// Append the successor digit, if exists
				// Append the successor letter or connector, if exists, and set error
				while(TokenTypeUtil.isLetter(nextCh)
						|| TokenTypeUtil.isDigit(nextCh)
						|| TokenTypeUtil.isConnector(nextCh)) {
					loopFlag = true;
					if (!TokenTypeUtil.isDigit(nextCh)) {
						errorFlag = true;
					}
					str += nextCh;
					nextCh = sBuffer.charAt(bufferIdx++);
				}
				bufferIdx--;
				
				if (loopFlag && errorFlag) {
					typeList.add(TokenTypeUtil.TOKEN_TYPE_ERROR);
					valueList.add(str);
				} else {
					typeList.add(TokenTypeUtil.TOKEN_TYPE_CONSTANT);
					valueList.add(str);
				}
			} else if (TokenTypeUtil.isUnaryOperator(ch)) {
				// The current character is an unary operator
				
				if (ch == '/') {
					// Check whether the character is a comment operator or not
					nextCh = sBuffer.charAt(bufferIdx++);
					
					if (nextCh == '*') {
						// The successor content is multi-line comments
						while (true) {
							nextCh = sBuffer.charAt(bufferIdx++);
							if ((nextCh == '*')
									&& (sBuffer.charAt(bufferIdx) == '/')) {
								// End of comments
								bufferIdx++;
								break;
							}
						}
					} else if (nextCh == '/') {
						// The successor content is a single-line comment
						while ((nextCh = sBuffer.charAt(bufferIdx++)) != '\n') {}
					} else if (nextCh == '=') {
						// Binary operator
						str = "" + ch + nextCh;
						typeList.add(TokenTypeUtil.TOKEN_TYPE_OPERATOR);
						valueList.add(str);
					} else {
						// Unary operator
						bufferIdx--;
						typeList.add(TokenTypeUtil.TOKEN_TYPE_OPERATOR);
						valueList.add(Character.toString(ch));
					}
				} else {
					nextCh = sBuffer.charAt(bufferIdx++);
					// Binary operator
					if (TokenTypeUtil.isUnaryOperator(nextCh)) {
						str = "" + ch + nextCh;
						if (TokenTypeUtil.isBinaryOperator(str)) {
							typeList.add(TokenTypeUtil.TOKEN_TYPE_OPERATOR);
							valueList.add(str);
						} else {
							typeList.add(TokenTypeUtil.TOKEN_TYPE_ERROR);
							valueList.add(str);
						}
					} else {
						// Unary operator
						bufferIdx--;
						typeList.add(TokenTypeUtil.TOKEN_TYPE_OPERATOR);
						valueList.add(Character.toString(ch));
					}
				}
			} else if (TokenTypeUtil.isDelimiter(ch)) {
				// The current character is a delimiter
				if (ch == '\'' || ch == '"') {
					str = "";
					while((nextCh = sBuffer.charAt(bufferIdx++)) != ch) {
						str += nextCh;
					}
					typeList.add(TokenTypeUtil.TOKEN_TYPE_DELIMITER);
					valueList.add(Character.toString(ch));
					typeList.add(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER);
					valueList.add(str);
					typeList.add(TokenTypeUtil.TOKEN_TYPE_DELIMITER);
					valueList.add(Character.toString(nextCh));
				} else {
					typeList.add(TokenTypeUtil.TOKEN_TYPE_DELIMITER);
					valueList.add(Character.toString(ch));
				}
			} else {
				// Undefined character
				typeList.add(TokenTypeUtil.TOKEN_TYPE_ERROR);
				valueList.add(Character.toString(ch));
			}
		}
	}
}
