package sem;

import ast.VarDecl;
import ast.FunDecl;

public abstract class Symbol {
	public String name;
	
	public Symbol(String name) {
		this.name = name;
	}

    public VarDecl getVar(){
        return null;
    }

    public FunDecl getFun(){
        return null;
    }

}
