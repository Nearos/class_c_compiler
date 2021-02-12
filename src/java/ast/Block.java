package ast;

import java.util.List;

public class Block extends Stmt {
    public final List<VarDecl> vars;
    public final List<Stmt> statements;

    public Block(List<VarDecl> vars, List<Stmt> statements){
        this.vars = vars;
        this.statements = statements;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitBlock(this);
    }
}
