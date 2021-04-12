package ast;

import gen.asm.AssemblyItem;
import gen.asm.Register;

public class VarDecl implements ASTNode {
    public final Type type;
    public final String varName;
    public Memory memory; //set by ProgramGen
    public VarDecl map =null;

    public static class Memory {

        public AssemblyItem.Label label;
        public int stackOffset;
        public Register register;

        public Memory(AssemblyItem.Label label){
            this.stackOffset = -1;
            this.label = label;
            this.register = null;
        }

        public Memory(int stackOffset){
            this.stackOffset = stackOffset;
            this.label = null;
            this.register = null;
        }

        public Memory(Register register){
            this.stackOffset = -1;
            this.label = null;
            this.register = register;
        }
    }

    public VarDecl(Type type, String varName) {
	    this.type = type;
	    this.varName = varName;
        this.memory = null;
        this.map = this;
    }

    public <T> T accept(ASTVisitor<T> v) {
	   return v.visitVarDecl(this);
    }
}
