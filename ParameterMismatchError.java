import java.io.*;
import java.util.*;

public class ParameterMismatchError extends Error {

	public ParameterMismatchError() {
		super();
	}

	public ParameterMismatchError(String function) {
		super("Parameter Mismatch Error! Function: " + function);
	}
}