package ast;

import java.util.List;

public class StructTypeDecl implements ASTNode {
    public final StructType type;
    public final List<VarDecl> fields;

    public StructTypeDecl(StructType type, List<VarDecl> fields){
        this.fields = fields;
        this.type = type;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructTypeDecl(this);
    }

}
