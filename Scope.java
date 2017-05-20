import java.util.*;
import java.io.*;

public interface Scope {
	public String getScopeName();
	public Scope getEnclosingScope();
	public void define(Symbol sym);
	public Symbol resolve(String name);
}