import java.io.*;
import java.util.*;

public class UndefinedReferenceError extends Error {

	public UndefinedReferenceError(String reference, String other) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}