package compiler.lexicalanalyzer;

/**
 * MainLex
 * Entry program for Lexical Analyzer
 * Modify the global variables SOURCE_FILE_PATH to your own source file path
 * Modify the global variables OUTPUT_FILE_PATH to your own output file path
 * @author Moxin Chen
 * @version 1.0
 */

public class MainLex {
	// Source and output file path
	public static String SOURCE_FILE_PATH = "source.txt";
	public static String OUTPUT_FILE_PATH = "output_token_sequence.txt";
	
	public static void main(String[] args) {
		new LexAnalyzer(SOURCE_FILE_PATH, OUTPUT_FILE_PATH);
		System.out.println("Lexical Analyzation Finished!");
		System.out.println("Output to output_token_sequence.txt");
	}
}
