import java.io.*;
import java.util.*;

public class Subtree {
	protected int row = 0;
	protected int col = 0;
	protected int level = 0;
	Token token;
	protected List<Subtree> children;

	public Subtree(Token current) {
		this.token = current;
		this.row = current.getRow();
		this.col = current.getCol();
	}

	public void addChild(AST subtree) {
		if(children == null) {
			children = new ArrayList<Subtree>();
		}
		children.add(subtree);
	}

	public void print() {
		String toPrint = "+---";
		int count = 0;
		while(count < level) {
			toPrint += toPrint;
			count++;
		}
		toPrint += "(" + row + ", " + col + ") ";
		System.out.println(toPrint);
	}
}