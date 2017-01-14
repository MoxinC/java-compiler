package compiler.syntaxanalyzer;

import java.util.ArrayList;
import java.util.List;

import compiler.util.FileIOUtil;
import compiler.util.TokenTypeUtil;

/**
 * SynAnalyzer
 * Main program for Syntax Analyzer
 * @param srcPath	Source Token Sequences File
 * @param optPath	Output File Path
 * @author Moxin Chen
 * @version 1.0
 */

public class SynAnalyzer {
	
	private ArrayList<String> tokenType = new ArrayList<String>();
	private ArrayList<String> tokenValue = new ArrayList<String>();
	
	private int blockStartIdx = 0;
	
	private int currentStmtIdx = 0;
	
	private int pkgNameCount = 1;
	private int importStmtCount = 0;
	private int typeStmtCount = 1;
	private int blockCount = 1;
	private int stmtsCount = 1;
	private int stmtCount = 1;
	private int boolsCount = 1;
	private int boolCount = 1;
	private int exprsCount = 1;
	private int exprCount = 1;
	private int termsCount = 1;
	private int termCount = 1;
	private int factorCount = 1;
	
	private ArrayList<String> resList = new ArrayList<String>();
	private String resStr = "";
	
	public SynAnalyzer(String srcPath, String optPath) {
		
		// Read the token sequences from file and get the type and value of token
		boolean parseFlag = FileIOUtil.parseTokenSeq(srcPath, tokenType, tokenValue);
		if (parseFlag) {
			analyze();
		} else {
			System.err.println("Error occurs while parsing token sequences.");
		}
		
		printResRecord();
		FileIOUtil.write2File(resStr, optPath);
	}
	
	public void analyze() {
		String tempStr;
		while (!(tempStr = tokenValue.get(blockStartIdx)).equals("{")){
			blockStartIdx++;
		}
		classDeclarationCheck();
		
	}
	
	public void classDeclarationCheck() {
		boolean declarationError = false;

		switch(tokenValue.get(0)) {
		case "package":
//			resList.add("program -> package_stmts");
			resList.add("program -> package_stmts type_stmts" + typeStmtCount
					+ " class identifier block" + blockCount);
			resList.add("package_stmts -> package_stmt import_stmt");
			if (packageStmtsCheck()) {
				
			} else {
				declarationError = true;
			}
			break;
		case "import":
//			resList.add("program -> package_stmts");
			resList.add("program -> package_stmts type_stmts" + typeStmtCount
					+ " class identifier block" + blockCount);
			resList.add("package_stmts -> package_stmt import_stmt");
			resList.add("package_stmt -> null");
			importStmtCheck(0);
			break;
		case "class":
//			resList.add("program -> package_stmts");
			resList.add("program -> package_stmts type_stmts" + typeStmtCount
					+ " class identifier block" + blockCount);
			resList.add("package_stmts -> package_stmt import_stmt");
			resList.add("package_stmt -> null");
			resList.add("import_stmt -> null");
			classNameCheck(1);
			break;
		default:
			classTypeStmtCheck(0);
			break;
		}
		
		if (!declarationError) {
			blockCheck();
		}
	}
	
	public boolean packageStmtsCheck() {
		System.out.println("### PackageStmtsCheck ###");
		if (packageStmtCheck()) {
			return true;
		}
		
		return false;
	}
	
	public boolean packageStmtCheck() {
		System.out.println("### PackageStmtCheck ###");
		
		String tempType;
		String tempStr;
		for (int i = 1; i < blockStartIdx; i++) {
			tempType = tokenType.get(i);
			tempStr = tokenValue.get(i);
			if (tempType.equals(TokenTypeUtil.TOKEN_TYPE_KEYWORD)) {
				if (!tokenValue.get(i - 1).equals(";")) {
					System.err.println("Missing ';' in package statement.");
					return false;
				}
				else {
					resList.add("package_stmt -> package package_name" + pkgNameCount + ";");
					int pkgNameEndIdx = i - 1;
					if (packageNameCheck(1, pkgNameEndIdx)) {
						if (importStmtCheck(i)) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
			}
		}
		System.err.println("Unknown error in package statement.");
		return false;
	}
	
	public boolean packageNameCheck(int startIdx, int endIdx) {
		System.out.println("### PackageNameCheck ###");
		
		int count = 0;
		for (int i = startIdx; i < endIdx; i++) {
			if (
				((count % 2 == 0) 
					&& (!tokenType.get(i).equals(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER))) ||
				((count % 2 == 1)
					&& (!tokenValue.get(i).equals(".")))
			   ) {
				String pkgName = "";
				for (int j = startIdx; j < endIdx; j++) {
					pkgName += tokenValue.get(j);
				}
				System.err.println("Illegal package name: " + pkgName);
				return false;
			}
			resList.add("package_name" + (pkgNameCount)
					+ " -> identifier.package_name" + (++pkgNameCount));
			count++;
		}
		resList.add("package_name" + (pkgNameCount)
				+ " -> identifier");
		return true;
	}
	
	public boolean importStmtCheck(int index) {
		System.out.println("### ImportStmtCheck ###");
		
		if (!tokenValue.get(index).equals("import")) {
			resList.add("import_stmt -> null");
			if (classTypeStmtCheck(index)) {
				return true;
			} else {
				return false;
			}
		} else {
			int i = index + 1;
			while (!tokenType.get(i).equals(TokenTypeUtil.TOKEN_TYPE_KEYWORD)) {
				i++;
			}
			if (!tokenValue.get(i - 1).equals(";")) {
				System.err.println("Missing ';' in import statement.");
				return false;
			} else {
				resList.add("import_stmt"+ (++importStmtCount) 
						+ " -> import package_name" + (++pkgNameCount) + ";");
				if (packageNameCheck(index + 1, i - 1)) {
					if (tokenValue.get(i).equals("import")) {
						if (importStmtCheck(i)) {
							return true;
						} else {
							return false;
						}
					} else {
						if (classTypeStmtCheck(i)) {
							return true;
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		}
	}
	
	public boolean classTypeStmtCheck(int index) {
		System.out.println("### ClassTypeStmtCheck ###");
//		int cc = typeStmtCount;
//		resList.add("program -> package_stmts type_stmts" + typeStmtCount);
		
		boolean flag = true;
		
		int i = index;
		for (; i < blockStartIdx && !tokenValue.get(i).equals("class"); i++) {
			if (!tokenType.get(i).equals(TokenTypeUtil.TOKEN_TYPE_KEYWORD)) {
				System.err.println("Error in class statement: " + tokenValue.get(i));
				return false;
			}
			
			resList.add("type_stmts" + (typeStmtCount++) 
					+ " -> keyword " + "type_stmts" + typeStmtCount);
		}
		
		resList.add("type_stmts" + (typeStmtCount++) + " -> null");
		
		if (i == blockStartIdx) {
			System.err.println("Error in class statement.");
			return false;
		} else {
			if (tokenValue.get(i).equals("class")) {
				if (classNameCheck(i + 1)) {
//					resList.add("program -> package_stmts type_stmts" + cc
//							+ "class identifier block");
					return true;
				} else {
					return false;
				}
			} else {
				System.err.println("Missing keyword \"class\".");
				return false;
			}
		}
	}
	
	public boolean classNameCheck(int index) {
		System.out.println("### ClassNameCheck ###");
		if (tokenType.get(index).equals(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER)) {
			return true;
		}
		System.err.println("Illegal class name: " + tokenValue.get(index));
		return false;
	}
	
	public boolean blockCheck() {
		System.out.println("### BlockCheck ###");
		if (!tokenValue.get(tokenValue.size() - 1).equals("}")) {
			System.err.println("Missing '}'");
			return false;
		} else {
			resList.add("block" + (blockCount++)
					+ " -> { stmts" + (stmtsCount) + " }");
			if (globalStmtsCheck(blockStartIdx + 1, tokenValue.size() - 2)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean globalStmtsCheck(int startIdx, int endIdx) {
		System.out.println("### GolbalStmtsCheck ###");
		
		boolean flag = false;
		int ssc = stmtsCount;
		
		currentStmtIdx = startIdx;

		int end;
		while ((end = getNextStmt(currentStmtIdx, endIdx)) != -1) {
			flag = true;
			resList.add("stmts" + (stmtsCount++)
					+ " -> stmt" + stmtCount 
					+ " stmts" + stmtsCount);
			if (stmtCheck(currentStmtIdx, end)) {
//				currentStmtIdx = end + 1;
				if (currentStmtIdx < end) {
					currentStmtIdx = end + 1;
				}
			}
			else{
				return false;
			}
		}
		
		if (!flag) {
			resList.add("stmts" + ssc + " -> null");
		}
		
		return true;
	}
	
	public boolean stmtsCheck(int startIdx, int endIdx) {
		System.out.println("### StmtsCheck ###");
		boolean flag = false;
		int ssc = stmtsCount;
		
		int start = startIdx;
		int end;
		while ((end = getNextStmt(start, endIdx)) != -1) {
			flag = true;
			resList.add("stmts" + (stmtsCount++)
					+ " -> stmt" + stmtCount
					+ " stmts" + (stmtsCount));
			if (stmtCheck(start, end)) {
				start = end + 1;
			}
			else{
				return false;
			}
		}

		resList.add("stmts" + (stmtsCount++) + " -> null");
//		if (!flag) {
//			resList.add("stmts" + ssc + " -> null");
//		}
		
		return true;
	}
	
	// Return the end index of a statement
	public int getNextStmt(int startIdx, int endIdx) {
		int index = startIdx;
		while (index <= endIdx) {
			String cs = tokenValue.get(index);
			if (cs.equals(";") || cs.equals("{") || cs.equals("}")) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	// Check single statement
	public boolean stmtCheck(int startIdx, int endIdx) {
		System.out.println("### StmtCheck ###");
		
		String cs = tokenValue.get(startIdx);
		if (cs.equals("if")) {
			return ifStmtCheck(startIdx, endIdx);
		} else if (cs.equals("else")) {
			return elseStmtCheck(startIdx, endIdx);
		} else if (cs.equals("while")) {
			return whileStmtCheck(startIdx, endIdx);
		} else if (cs.equals("do")) {
			return doStmtCheck(startIdx, endIdx);
		} else if (cs.equals("{")) {
			return blockStmtCheck(startIdx);
		} else if (cs.equals("break") || cs.equals("continue")) {
			resList.add("stmt" + (stmtCount++) + cs + ";");
			return true;
		} else {
			return initStmtCheck(startIdx, endIdx);
		}
	}
	
	// Check if statement
	public boolean ifStmtCheck(int startIdx, int endIdx) {
		System.out.println("### IFStmtCheck ###");
		resList.add("stmt" + (stmtCount++) 
				+ " -> if ( bools" + (boolsCount)
				+ " ) stmt" + (stmtCount++));
		
		if ((tokenValue.get(startIdx + 1).equals("("))
				&& tokenValue.get(endIdx - 1).equals(")")) {
//			return boolsCheck(startIdx + 2, endIdx - 2);
			if (boolsCheck(startIdx + 2, endIdx - 2)) {
				if (blockStmtCheck(endIdx)) {
					return true;
				}
			}
		}
		System.err.println("Error in statement: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo += "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
		}
		System.err.println(errInfo);
		return false;
	}
	
	// Check else statement
	public boolean elseStmtCheck(int startIdx, int endIdx) {
		System.out.println("### ELSEStmtCheck ###");
		resList.add("stmt" + (stmtCount++) 
				+ " -> else stmt" + (stmtCount++));
		
		if (startIdx == (endIdx - 1)) {
			if (blockStmtCheck(endIdx)) {
				return true;
			}
		} else if ((tokenValue.get(startIdx + 1).equals("if"))
				&& (tokenValue.get(startIdx + 2).equals("("))
				&& (tokenValue.get(endIdx - 1).equals(")"))){
			if (boolsCheck(startIdx + 3, endIdx - 2)) {
				if (blockStmtCheck(endIdx)) {
					return true;
				}
			}
		}
		System.err.println("Error in statement: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}

	// Check while statement
	public boolean whileStmtCheck(int startIdx, int endIdx) {
		System.out.println("### WHILEStmtCheck ###");
		resList.add("stmt" + (stmtCount++) 
				+ " -> while ( bools" + (boolsCount)
				+ " ) stmt" + (stmtCount++));
		
		if ((tokenValue.get(startIdx + 1).equals("("))
				&& (tokenValue.get(endIdx - 1).equals(")"))) {
			return boolsCheck(startIdx + 2, endIdx - 2);
		}
		System.err.println("Error in statement: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo += "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
		}
		System.err.println(errInfo);
		return false;
	}
	
	// Check do statement
	public boolean doStmtCheck(int startIdx, int endIdx) {
		System.out.println("### DOStmtCheck ###");
		resList.add("stmt" + (stmtCount++) 
				+ " -> do stmt" + (stmtCount++));
		
		if (startIdx == (endIdx - 1)) {
			return true;
		}
		System.err.println("Error in statement: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}
	
	// Check block statement '{...}'
	public boolean blockStmtCheck(int startIdx) {
		System.out.println("### BlockStmtCheck ###");
		resList.add("stmt" + (stmtCount++) 
				+ " -> block" + (blockCount));
		
		int index = startIdx + 1;
		int endIdx = -1;
		while (index < tokenValue.size() - 1) {
			if (tokenValue.get(index).equals("}")) {
				endIdx = index;
				break;
			} else if (tokenValue.get(index).equals("{")) {
				resList.add("block" + blockCount
						+ "-> { stmts" + (stmtsCount) + " }");
				resList.add("stmts" + (stmtsCount++)
						+ " -> stmt" + (stmtCount++)
						+ " stmts" + (stmtsCount++));
				blockStmtCheck(index);
			}
			index++;
		}
		if (endIdx == -1) {
			System.err.println("Error: Missing '}'");
			return false;
		} else {
			resList.add("block" + blockCount
					+ "-> { stmts" + (stmtsCount++) + " }");
			currentStmtIdx = endIdx + 1;
			return stmtsCheck(startIdx + 1, endIdx - 1);
		}
	}
	
	public boolean initStmtCheck(int startIdx, int endIdx) {
		System.out.println("### InitStmtCheck ###");
		if ((tokenType.get(startIdx).equals(TokenTypeUtil.TOKEN_TYPE_KEYWORD))
				&& (tokenType.get(startIdx + 1).equals(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER))) {
			if (endIdx == (startIdx + 2)) {
				resList.add("stmt" + (stmtCount++)
						+ " -> type_stmts" + (typeStmtCount)
						+ " identifier;");
				resList.add("type_stmts" + (typeStmtCount++)
						+ " -> keyword type_stmts" + (typeStmtCount));
				resList.add("type_stmts" + (typeStmtCount++) + " -> null");
				return true;
//			} else if ((tokenValue.get(startIdx + 2).equals("="))
//				&& exprsCheck(startIdx + 3, endIdx - 1)) {
			} else if (tokenValue.get(startIdx + 2).equals("=")) {
				resList.add("stmt" + (stmtCount++)
						+ " -> type_stmts" + (typeStmtCount)
						+ " identifier = exprs" + (exprsCount) + ";");
				resList.add("type_stmts" + (typeStmtCount++)
						+ " -> keyword type_stmts" + (typeStmtCount));
				resList.add("type_stmts" + (typeStmtCount++) + " -> null");
				if (exprsCheck(startIdx + 3, endIdx - 1)) {
					return true;
				}
			}
		}
		if ((tokenType.get(startIdx).equals(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER))
				&& (tokenValue.get(startIdx + 1).equals("="))) {
			resList.add("stmt" + (stmtCount++)
					+ " -> type_stmts" + (typeStmtCount++)
					+ " identifier = exprs" + (exprsCount) + ";");
			resList.add("type_stmts" + (typeStmtCount++) + " -> null");
			if (exprsCheck(startIdx + 2, endIdx - 1)) {
				return true;
			}
		}
		
		System.err.println("Error in statement: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}
	
	public boolean boolsCheck(int startIdx, int endIdx) {
		System.out.println("### BoolsCheck ###");
		int index = startIdx;
		resList.add("bools" + (boolsCount++)
				+ " -> exprs" + exprsCount 
				+ " bool" + boolCount);
		while (index <= endIdx) {
			if (exprsCheck(startIdx, index)) {
				if (index == endIdx) {
					resList.add("bool" + (boolCount++)
							+ " -> null");
					return true;
				} else if (boolCheck(index + 1, endIdx)) {
					return true;
				}
			}
			index++;
		}
		System.err.println("Error in bools: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}
	
	public boolean boolCheck(int startIdx, int endIdx) {
		System.out.println("### BoolCheck ###");
		String cs = tokenValue.get(startIdx);
		if ((cs.equals("<")) 
				|| (cs.equals("<="))
				|| (cs.equals(">")) 
				|| (cs.equals(">="))) {
			resList.add("bool" + (boolCount++)
					+ " " + cs + " exprs" + exprsCount);
			int index = startIdx + 1;
			while (index <= endIdx) {
				if (exprsCheck(startIdx + 1, index)) {
					return true;
				}
				index++;
			}
		}
		return false;
	}

	public boolean exprsCheck(int startIdx, int endIdx) {
		System.out.println("### ExprsCheck ###");
		resList.add("exprs" + (exprsCount++)
				+ " -> terms" + termsCount
				+ " expr" + exprCount);
		
		int index = startIdx;
		while (index <= endIdx) {
			if (termsCheck(startIdx, index)) {
				if (index == endIdx) {
					resList.add("expr" + (exprCount++)
							+ " -> null");
					return true;
				} else if (exprCheck(index + 1, endIdx)) {
					return true;
				}
			}
			index++;
		}
		System.err.println("Error in exprs: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}
	
	public boolean exprCheck(int startIdx, int endIdx) {
		System.out.println("### ExprCheck ###");
		String cs = tokenValue.get(startIdx);
		if ((cs.equals("+")) || (cs.equals("-"))) {
			resList.add("expr" + (exprCount++)
					+ " -> " + cs + " terms" + termsCount
					+ " expr" + exprCount);
			
			int index = startIdx + 1;
			while (index <= endIdx) {
				if (termsCheck(startIdx + 1, index)) {
					if (index == endIdx) {
						resList.add("expr" + (exprCount++)
								+ " -> null");
						return true;
					} else if (exprCheck(index + 1, endIdx)) {
						return true;
					}
				}
				index++;
			}
		}
		return false;
	}

	public boolean termsCheck(int startIdx, int endIdx) {
		System.out.println("### TermsCheck ###");
		resList.add("terms" + (termsCount++)
				+ " -> factor" + factorCount
				+ " term" + termCount);
		
		int index = startIdx;
		while (index <= endIdx) {
			if (factorCheck(startIdx, index)) {
				if (index == endIdx) {
					resList.add("term" + (termCount++)
							+ " -> null");
					return true;
				} else if (termCheck(index + 1, endIdx)) {
					return true;
				}
			}
			index++;
		}
		System.err.println("Error in terms: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}
	
	public boolean termCheck(int startIdx, int endIdx) {
		System.out.println("### TermCheck ###");
				
		String cs = tokenValue.get(startIdx);
		if ((cs.equals("*")) || (cs.equals("/"))) {
			resList.add("term" + (termCount++)
					+ " -> " + cs + " factor" + factorCount
					+ " term" + termCount);
			int index = startIdx + 1;
			while (index <= endIdx) {
				if (factorCheck(startIdx + 1, index)) {
					if (index == endIdx) {
						resList.add("term" + (termCount++)
								+ " -> null");
						return true;
					} else if (termCheck(index + 1, endIdx)) {
						return true;
					}
				}
				index++;
			}
		}
		return false;
	}

	public boolean factorCheck(int startIdx, int endIdx) {
		System.out.println("### FactorCheck ###");
		if (startIdx == endIdx) {
			if (tokenType.get(startIdx).equals(TokenTypeUtil.TOKEN_TYPE_CONSTANT)) {
				resList.add("factor" + (factorCount++) + " -> constant");
				return true;
			} else if (tokenType.get(startIdx).equals(TokenTypeUtil.TOKEN_TYPE_IDENTIFIER)) {
				resList.add("factor" + (factorCount++) + " -> identifier");
				return true;
			}
		} else {
			resList.add("factor" + (factorCount++)
					+ " -> ( expr" + exprCount + " )");
			if ((tokenValue.get(startIdx) .equals("("))
					&& (tokenValue.get(endIdx).equals(")"))
					&& exprCheck(startIdx + 1, endIdx - 1)) {
				return true;
			}
		}

		System.err.println("Error in factor: ");
		String errInfo = "";
		for (int i = startIdx; i <= endIdx; i++) {
			errInfo = "<" + tokenType.get(i) + "> "
					+ "\"" + tokenValue.get(i) + "\"";
			System.err.println(errInfo);
		}
		return false;
	}
	
	public void printResRecord() {
		for (String str : resList) {
//			System.out.println(str);
			resStr += str + "\n";
		}
	}
}
