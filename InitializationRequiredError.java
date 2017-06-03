import java.io.*;
import java.util.*;

public class InitializationRequiredError extends Error {

	public InitializationRequiredError(String reference) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}