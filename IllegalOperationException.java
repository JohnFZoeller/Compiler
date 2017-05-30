import java.io.*;
import java.util.*;

public class IllegalOperationException extends Error {

	public IllegalOperationException() {
		super();
	}

	public IllegalOperationException(String operation, String tokenType) {
		super(operation + " Operation not allowed on Tokens of type: " + tokenType);
	}
}