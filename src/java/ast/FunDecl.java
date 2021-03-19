package ast;

import java.util.List;
import gen.asm.AssemblyItem;

public class FunDecl implements ASTNode {
    public final Type type;
    public final String name;
    public final List<VarDecl> params;
    public Block block;
    public AssemblyItem.Label label;
    public FunDecl map = null;
    public boolean modified = false;

    public FunDecl(Type type, String name, List<VarDecl> params, Block block) {
	    this.type = type;
	    this.name = name;
	    this.params = params;
	    this.block = block;
        this.map = this;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitFunDecl(this);
    }
}
