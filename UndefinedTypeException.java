import java.io.*;
import java.util.*;

public class UndefinedTypeException extends Exception {

	public UndefinedTypeException(String name) {
		super(name + " Not Defined in this Scope!");
	}
}