import java.io.*;
import java.util.*;

public class AlreadyDefinedException extends Exception {

	public AlreadyDefinedException(String name) {
		super(name + " Already Defined in this Scope");
	}
}