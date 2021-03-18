package ast;

import java.util.List;
import gen.asm.AssemblyItem.Label;

public class FunCallExpr extends Expr{
    public final String function;
    public final List<Expr> arguments;
    public FunDecl fd;
    

    public FunCallExpr(String function, List<Expr> arguments){
        this.function = function;
        this.arguments = arguments;
    }

    public <T> T accept(ASTVisitor<T> v){
        return v.visitFunCallExpr(this);
    }
}