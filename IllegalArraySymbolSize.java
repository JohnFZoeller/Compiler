import java.io.*;
import java.util.*;

public class IllegalArraySymbolSize extends Error {

	public IllegalArraySymbolSize(int reference) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}