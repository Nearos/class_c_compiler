package gen;

import ast.*;
import gen.asm.AssemblyProgram;

/**
 * A visitor that produces code for a function declaration
 */
public class FunGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;

    public FunGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitBlock(Block b) {
        // TODO: to complete
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {

        // Each function should be produced in its own section.
        // This is is necessary for the register allocator.
        asmProg.newSection(AssemblyProgram.Section.Type.TEXT);

        // TODO: to complete:
        // 1) emit the prolog
        // 2) emit the body of the function
        // 3) emit the epilog

        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        throw new ShouldNotReach();
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        // TODO: should allocate local variables on the stack and rember the offset from the frame pointer where they are stored (e.g. in the VarDecl AST node)
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        // expression should be visited with the ExprGen when they appear in a statement (e.g. If, While, Assign ...)
        throw new ShouldNotReach();
    }

    // TODO: to complete (should only deal with statements, expressions should be handled by the ExprGen or AddrGen)
}
