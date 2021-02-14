package ast;

public class Assign extends Stmt{

    public final Expr lvalue;
    public final Expr rvalue;


    public Assign(Expr lvalue, Expr rvalue){
        this.lvalue = lvalue;
        this.rvalue = rvalue;
    }

    public <T> T accept(ASTVisitor<T> v){
        //TODO
        return v.visitAssign(this);
    }
}