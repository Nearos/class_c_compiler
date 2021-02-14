package ast;

public class AddressOfExpr extends Expr{
    public final Expr expr;

    public AddressOfExpr(Expr expr){
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> v){
        return v.visitAddressOfExpr(this);
    }
}