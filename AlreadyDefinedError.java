import java.io.*;
import java.util.*;

public class AlreadyDefinedError extends Error {

	public AlreadyDefinedError(String reference) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}