package compiler.util;

/**
 * FileIoUtil
 * Implements methods for input/output operation on file
 * @author Moxin Chen
 * @version 1.0
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileIOUtil {
	
	@SuppressWarnings("resource")
	public static boolean readFromFile(StringBuffer buffer, String srcPath) {
		
		String line;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(srcPath));
			while ((line = br.readLine()) != null) {
				line += '\n';
				buffer.append(line);
			}
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean write2File(String content, String optPath) {
		
		try {
			FileWriter fw = new FileWriter(optPath);
			fw.write(content);
			fw.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean parseTokenSeq(String srcPath, 
			List<String> type, 
			List<String> value) {
		
		String line;
		String str;
		char ch;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(srcPath));
			while ((line = br.readLine()) != null) {
				line = line.substring(2, line.length() - 2);
				int lineIdx = 0;
				str = "";
				while ((ch = line.charAt(lineIdx++)) != ',') {
					str += ch;
				}
				type.add(str);
				value.add(line.substring(++lineIdx, line.length()));
			}
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
