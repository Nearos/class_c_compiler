package ast;

import java.util.List;
import sem.Scope;

public class StructTypeDecl implements ASTNode {
    public final StructType type;
    public final List<VarDecl> fields;
    public Scope scope;

    public StructTypeDecl(StructType type, List<VarDecl> fields){
        this.fields = fields;
        this.type = type;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
