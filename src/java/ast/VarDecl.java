package ast;

import gen.asm.AssemblyItem;

public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;
    public Memory memory; //set by ProgramGen

    public static class Memory {

        public AssemblyItem.Label label;
        public int stackOffset;

        public Memory(AssemblyItem.Label label){
            this.stackOffset = -1;
            this.label = label;
        }

        public Memory(int stackOffset){
            this.stackOffset = stackOffset;
            this.label = null;
        }
    }

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
        this.memory = null;
    }

    public <T> T accept(ASTVisitor<T> v) {
	   return v.visitVarDecl(this);
    }
}
