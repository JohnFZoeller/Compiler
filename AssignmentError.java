import java.io.*;
import java.util.*;

public class AssignmentError extends Error {

	public AssignmentError(String reference) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}