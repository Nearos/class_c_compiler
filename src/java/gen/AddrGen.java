package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Register;

/**
 * Generates code to calculate the address of an expression and return the result in a register.
 */
public class AddrGen implements ASTVisitor<Register> {

    private AssemblyProgram asmProg;

    public AddrGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    @Override
    public Register visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitBlock(Block b) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitFunDecl(FunDecl p) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitProgram(Program p) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitVarDecl(VarDecl vd) {
        throw new ShouldNotReach();
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        // TODO: to complete
        return null;
    }

    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)

}
