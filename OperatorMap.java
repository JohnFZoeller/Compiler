/**
 * 
 * OperatorMap is a utility class that contains all of the Operator
 * tokens specific to a given language. Operator tokens are stored
 * in a HashMap. The key for each key-value pair is the operator
 * itself, and the value is a description of the operator that can
 * be printed for additional information. Note that the HashMap
 * does not allow duplicate keys. Class is final.
 *
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 1.0
 *
 */

package tokens;

import java.util.HashMap;
import java.util.Map;

public final class OperatorMap {
	public static Map<String, String> operators = 
	new HashMap<String, String>(){{

        	put("(", "OPEN_PARENTHESIS");
                put(")", "CLOSE_PARENTHESIS");
                put("[", "OPEN_BRACKET");
                put("]", "CLOSE_BRACKET");
                put("{", "OPEN_BRACE");
                put("}", "CLOSE_BRACE");
                put(",", "COMMA");
                put(";", "SEMICOLON");
                put("+", "PLUS");
                put("-", "MINUS");
                put("*", "ASTERISK");
                put("/", "BACKSLASH");
                put("~", "TILDE");
                put("=", "ASSIGNMENT_OPERATOR");
                put(">", "RELATIONAL_GREATER_THAN");
                put(">=", "RELATIONAL_GREATER_EQUALTO");
                put("<", "RELATIONAL_LESS_THAN");
                put("<=", "RELATIONAL_LESS_EQUALTO");
                put("&", "BITWISE_AND");
                put("&&", "LOGICAL_AND");
                put("|", "BITWISE_OR");
                put("||", "LOGICAL_OR");
                put("^", "BITWISE_XOR");
        
	}};
}