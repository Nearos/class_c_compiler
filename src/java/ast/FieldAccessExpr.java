package ast;

public class FieldAccessExpr extends Expr{
    public final Expr object;
    public String name;

    public FieldAccessExpr(Expr object, String name){
        this.object = object;
        this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v){
        return v.visitFieldAccessExpr(this);
    }
}