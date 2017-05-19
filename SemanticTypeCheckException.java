import java.io.*;
import java.util.*;

public class SemanticTypeCheckException extends Exception {
	Type incorrect = null;

	public SemanticTypeCheckException(Type passed, Type expected) {
		super("Incorrect type. " + passed.toString() + " passed, " +
			expected.toString() + " expected.");
	}
}