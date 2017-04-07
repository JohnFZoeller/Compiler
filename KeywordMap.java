/**
 * 
 * KeywordNMap is a utility class that contains all of the keywords
 * specific to a given language. Keywords are stored a HashMap. The
 * key for each key-value pair is the keyword itself, the value
 * is a description of the keyword that can be printed for additional
 * information. Note that the HashMap does not allow duplicate keys.
 * Class is final.
 *
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 1.0
 *
 */

//package tokens;

import java.util.HashMap;
import java.util.Map;

public final class KeywordMap {
	public static Map<String, String> keywords = 
	new HashMap<String, String>(){{

        	put("byte", "KEYWORD_BYTE");
                put("const", "KEYWORD_CONST");
                put("else", "KEYWORD_ELSE");
                put("end", "KEYWORD_END");
                put("exit", "KEYWORD_EXIT");
                put("float64", "KEYWORD_FLOAT64");
                put("for", "KEYWORD_FOR");
                put("function", "KEYWORD_FUNCTION");
                put("if", "KEYWORD_IF");
                put("int32", "KEYWORD_INT32");
                put("print", "KEYWORD_PRINT");
                put("record", "KEYWORD_RECORD");
                put("ref", "KEYWORD_REF");
                put("return", "KEYWORD_RETURN");
                put("static", "KEYWORD_STATIC");
                put("type", "KEYWORD_STATIC");
                put("var", "KEYWORD_VAR");
                put("while", "KEYWORD_WHILE");
        
	}};
}