import java.io.*;
import java.util.*;

public class MissingReferenceField extends Error {

	public MissingReferenceField(String reference) {
		super("Reference of " + reference + " requires initialization statement!");
	}
}