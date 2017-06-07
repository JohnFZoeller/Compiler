import java.io.*;
import java.util.*;

public class InvalidIndexError extends Error {

	public InvalidIndexError(int index, String reference) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}