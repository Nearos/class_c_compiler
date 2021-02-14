package ast;


public class Return extends Stmt{

    public final Expr value;

    public Return(Expr value){
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v){
        //TODO
        return v.visitReturn(this);
    }
}