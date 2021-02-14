package ast;

public class If extends Stmt{

    public final Expr condition;
    public final Stmt consequent;
    public final Stmt alternative;


    public If(Expr condition, Stmt consequent, Stmt alternative){
        this.condition = condition;
        this.consequent = consequent;
        this.alternative = alternative;
    }

    public <T> T accept(ASTVisitor<T> v){
        //TODO
        return v.visitIf(this);
    }
}