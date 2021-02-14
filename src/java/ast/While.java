package ast;

public class While extends Stmt{

    public final Expr condition;
    public final Stmt stmt;


    public While(Expr condition, Stmt stmt){
        this.condition = condition;
        this.stmt = stmt;
    }

    public <T> T accept(ASTVisitor<T> v){
        //TODO
        return v.visitWhile(this);
    }
}