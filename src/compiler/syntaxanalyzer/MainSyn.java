package compiler.syntaxanalyzer;

/**
 * MainSyn
 * Entry program for Syntax Analyzer
 * Modify the global variables SOURCE_FILE_PATH to your own source file path
 * Modify the global variables OUTPUT_FILE_PATH to your own output file path
 * @author Moxin Chen
 * @version 1.0
 */


public class MainSyn {
	public static String SOURCE_FILE_PATH = "output_token_sequence.txt";
	public static String OUTPUT_FILE_PATH = "output_syntax_tree.txt";
	
	public static void main(String[] args) {
		new SynAnalyzer(SOURCE_FILE_PATH, OUTPUT_FILE_PATH);
		System.out.println("Syntax Analyzation Finished!");
		System.out.println("Output to output_tree.txt");
	}
}
