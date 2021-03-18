package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.*;

/**
 * This visitor should produce a program. Its job is simply to handle the global variable declaration by allocating
 * these in the data section. Then it should call the FunGen function generator to process each function declaration.
 * The label corresponding to each global variable can either be stored in the VarDecl AST node (simplest solution)
 * or store in an ad-hoc data structure (i.e. a Map) that can be passed to the other visitors.
 */
public class ProgramGen extends BaseGen<Void> {

    private final AssemblyProgram asmProg;

    private final AssemblyProgram.Section dataSection ;

    public ProgramGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
        this.dataSection = asmProg.newSection(AssemblyProgram.Section.Type.DATA);
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
        AssemblyProgram.Section mainData = asmProg.newSection(AssemblyProgram.Section.Type.DATA);


        // AssemblyItem.Label messageLabel = new AssemblyItem.Label("message");
        // mainData.emit(messageLabel);
        // mainData.emit(new AssemblyItem.Directive.Ascii("Hello, world!"));

        AssemblyProgram.Section main = asmProg.newSection(AssemblyProgram.Section.Type.TEXT);
        main.emit(new AssemblyItem.Directive.Globl("main")); //.globl main
        main.emit(AssemblyItem.Label.main);
        
        if(AssemblyItem.Label.getMainLabel() == null){
            System.out.println("No main function found");
            return null;
        }

        main.emitJump("jal", AssemblyItem.Label.getMainLabel());

        // int, char, and pointer returns are in v0
        main.emit("add", Register.Arch.a0, Register.Arch.zero, Register.Arch.zero); //copy value to a0, where it will be returned
        main.emit("addi", Register.Arch.v0, Register.Arch.zero, 17); //syscall for exit with value
        main.emit(AssemblyItem.Instruction.syscall);


        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {
        AssemblyItem.Label theLabel = new AssemblyItem.Label(vd.varName);

        int bytes = vd.type.bytes();
        int aligned = ((bytes - 1 )/4 +1)*4;

        dataSection.emit(theLabel);
        dataSection.emit(new AssemblyItem.Directive.Space(aligned));

        vd.memory = new VarDecl.Memory(theLabel);
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        throw new ShouldNotReach();
    }

    // TODO: to complete (all the other visit methods should throw SholdNotReach)


}
