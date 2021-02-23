package gen;

import ast.*;
import gen.asm.AssemblyProgram;

/**
 * This visitor should produce a program. Its job is simply to handle the global variable declaration by allocating
 * these in the data section. Then it should call the FunGen function generator to process each function declaration.
 * The label corresponding to each global variable can either be stored in the VarDecl AST node (simplest solution)
 * or store in an ad-hoc data structure (i.e. a Map) that can be passed to the other visitors.
 */
public class ProgramGen implements ASTVisitor<Void> {

    private AssemblyProgram asmProg;

    private final AssemblyProgram.Section dataSection = asmProg.newSection(AssemblyProgram.Section.Type.DATA);

    public ProgramGen(AssemblyProgram asmProg) {
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
    public Void visitBlock(Block b)  {
        throw new ShouldNotReach();
    }


    @Override
    public Void visitFunDecl(FunDecl fd) {
        // call the visitor specialized for handling function declaration
        return new FunGen(asmProg).visitFunDecl(fd);
    }

    @Override
    public Void visitProgram(Program p) {
        p.varDecls.forEach(vd -> vd.accept(this));
        p.funDecls.forEach(fd -> fd.accept(this));
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        // TODO: to complete: declare the variable globally in the data section and remember its label somewhere (e.g. in the VarDecl AST node directly).
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        throw new ShouldNotReach();
    }

    // TODO: to complete (all the other visit methods should throw SholdNotReach)


}
