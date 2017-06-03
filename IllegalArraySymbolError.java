/*======================= IllegalArraySymbolError =============================

/**	Specialized error class for invalid array dimensions
 */

import java.io.*;
import java.util.*;

public class IllegalArraySymbolSize extends Error {
	public IllegalArraySymbolSize(int dimension) {
		super("Dimension of: " + dimension + " not allowed for ArraySymbol types!");
	}
}
