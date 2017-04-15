import java.util.*;

class ASTTree{
	private ASTNode root;

	public ASTTree(){
		root = null;
	}

	public ASTTree(ArrayList<Token> list){
		root = new ASTNode(list);
	}

	

}