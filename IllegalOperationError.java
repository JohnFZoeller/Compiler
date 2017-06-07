import java.io.*;
import java.util.*;

public class IllegalOperationError extends Error {

	public IllegalOperationError(String reference, String other) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}