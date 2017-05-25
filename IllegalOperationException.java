import java.io.*;
import java.util.*;

public class IllegalOperationException extends Exception {

	public AlreadyDefinedException(String operation, String tokenType) {
		super(operation + " Operation not allowed on Tokens of type: " + tokenType);
	}
}