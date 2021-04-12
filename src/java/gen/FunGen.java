package gen;

import ast.*;
import gen.asm.*;

/**
 * A visitor that produces code for a function declaration
 *
 *
 * Calling convention -
 *
 *  before calling
 *  
 *  leave space for arguments + 4 for return address
 *  put onto stack
 *
 *  stack on call:
 *  -return value
 *  -arguments
 *  -return address (essentially first argument) //TODO: ast pass to make it into arguments
 *  
 *  actually return value can be anywhere
 *  
 *  after call:
 *  
 *  push fp
 *  fp = sp
 *  leave space for locals //TODO: collect locals
 *  pushregs
 *  
 *
 *  ...
 *
 *  popregs
 *  remove space for locals
 *  pop fp
 *  
 */
public class FunGen extends BaseGen<Void> {

    private AssemblyProgram asmProg;
    private AssemblyProgram.Section section;

    private int offsetCounter;

    public FunGen(AssemblyProgram asmProg) {
        this.asmProg = asmProg;
        this.offsetCounter = 0;
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

        if(b.vars.size()!=0){
            throw new ShouldNotReach();//should have been cleared out by ast rebuilder
        }
        
        for(Stmt s: b.statements) {
            String astCode = ASTPrinter.printNode(s);
            int newlineI = astCode.indexOf('\n');
            if(newlineI > 0){
                astCode = astCode.substring(0, newlineI);
            }

            section.emit("AST code: " + astCode);

            s.accept(this);
        }
        
        return null;
    }

    private void emitEpilogue(){
        section.emitStore("lw", Register.Arch.ra, Register.Arch.sp, 0);
        section.emit("addi", Register.Arch.sp, Register.Arch.sp, 4);
        section.emit(AssemblyItem.Instruction.popRegisters);
        section.emit("addi", Register.Arch.sp, Register.Arch.sp, offsetCounter); //delete locals
        section.emitLoad("lw", Register.Arch.fp, Register.Arch.sp, -4);//restore fp
        section.emitJr("jr", Register.Arch.ra);//return
    }

    public static int findArgumentOffsets(FunDecl decl){
        //return value should have been dealt with by an AST visitor
        int argOffsetCounter = -4; //0 is original fp
        for(VarDecl vd: decl.params){
            vd.memory = new VarDecl.Memory(argOffsetCounter);
            argOffsetCounter -= ((vd.type.bytes()-1)/4+1)*4; //go back enough to fit this argument
        }
        return -argOffsetCounter+4;
    }

    private void visitFunctionBlock(Block b){
        // TODO: to complete:
        // 1) emit the prolog

        //push fp
        section.emitStore("sw", Register.Arch.fp, Register.Arch.sp, -4);

        //fp = sp
        section.emit("subi", Register.Arch.fp, Register.Arch.sp, 4);

        offsetCounter = 4; //fp + 0 is original fp, so fp + 4 is first local

        for(VarDecl vd: b.vars){
            vd.accept(this); //increments offset counter by word-aligned sizeof(vd)
        }

        //space on the stack for variables:
        section.emit("subi", Register.Arch.sp, Register.Arch.sp, offsetCounter);

        //pushregs
        section.emit(AssemblyItem.Instruction.pushRegisters);
        section.emitStore("sw", Register.Arch.ra, Register.Arch.sp, -4);
        section.emit("subi", Register.Arch.sp, Register.Arch.sp, 4);

        // 2) emit the body of the function
        for(Stmt s: b.statements) {
            String astCode = ASTPrinter.printNode(s);
            int newlineI = astCode.indexOf('\n');
            if(newlineI > 0){
                astCode = astCode.substring(0, newlineI);
            }

            section.emit("AST code: " + astCode);
            s.accept(this);
        }

         // 3) emit the epilog
        emitEpilogue();
    }

    private boolean emitBuiltins(String name){
        if(name.equals("mcmalloc")){
            section.emitStore("sw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitStore("sw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitStore("sw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitStore("sw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, 0);//return value location (void**)
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, 4);//amount of memory

            section.emit("subi", Register.Arch.sp, Register.Arch.sp, 16);

            section.emit("addi", Register.Arch.v0, Register.Arch.zero, 9);

            section.emit(AssemblyItem.Instruction.syscall);

            section.emitStore("sw", Register.Arch.v0, Register.Arch.a1, 0); //a1 is return location

            section.emit("addi", Register.Arch.sp, Register.Arch.sp, 16);
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitLoad("lw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitLoad("lw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitJr("jr", Register.Arch.ra);
        }else if(name.equals("read_i")){
            section.emitStore("sw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitStore("sw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitStore("sw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitStore("sw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, 0);

            section.emit("subi", Register.Arch.sp, Register.Arch.sp, 16);

            section.emit("addi", Register.Arch.v0, Register.Arch.zero, 5);

            section.emit(AssemblyItem.Instruction.syscall);

            section.emitStore("sw", Register.Arch.v0, Register.Arch.a0, 0); //a0 is return location

            section.emit("addi", Register.Arch.sp, Register.Arch.sp, 16);
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitLoad("lw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitLoad("lw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitJr("jr", Register.Arch.ra);
        }else if(name.equals("read_c")){
            section.emitStore("sw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitStore("sw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitStore("sw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitStore("sw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, 0);

            section.emit("subi", Register.Arch.sp, Register.Arch.sp, 16);

            section.emit("addi", Register.Arch.v0, Register.Arch.zero, 12);

            section.emit(AssemblyItem.Instruction.syscall);

            section.emitStore("sw", Register.Arch.v0, Register.Arch.a0, 0); //a0 is return location

            section.emit("addi", Register.Arch.sp, Register.Arch.sp, 16);
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitLoad("lw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitLoad("lw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitJr("jr", Register.Arch.ra);
        }else if(name.equals("print_i")){
            section.emitStore("sw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitStore("sw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitStore("sw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitStore("sw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, 0);

            section.emit("subi", Register.Arch.sp, Register.Arch.sp, 16);

            section.emit("addi", Register.Arch.v0, Register.Arch.zero, 1);

            section.emit(AssemblyItem.Instruction.syscall);

            section.emit("addi", Register.Arch.sp, Register.Arch.sp, 16);
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitLoad("lw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitLoad("lw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitJr("jr", Register.Arch.ra);
        }else if(name.equals("print_c")){
            section.emitStore("sw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitStore("sw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitStore("sw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitStore("sw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, 0);

            section.emit("subi", Register.Arch.sp, Register.Arch.sp, 16);

            section.emit("addi", Register.Arch.v0, Register.Arch.zero, 11);

            section.emit(AssemblyItem.Instruction.syscall);

            section.emit("addi", Register.Arch.sp, Register.Arch.sp, 16);
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitLoad("lw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitLoad("lw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitJr("jr", Register.Arch.ra);
        }else if(name.equals("print_s")){
            section.emitStore("sw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitStore("sw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitStore("sw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitStore("sw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, 0);

            section.emit("subi", Register.Arch.sp, Register.Arch.sp, 16);

            section.emit("addi", Register.Arch.v0, Register.Arch.zero, 4);

            section.emit(AssemblyItem.Instruction.syscall);

            section.emit("addi", Register.Arch.sp, Register.Arch.sp, 16);
            section.emitLoad("lw", Register.Arch.a0, Register.Arch.sp, -4);
            section.emitLoad("lw", Register.Arch.a1, Register.Arch.sp, -8);
            section.emitLoad("lw", Register.Arch.a2, Register.Arch.sp, -12);
            section.emitLoad("lw", Register.Arch.v0, Register.Arch.sp, -16);

            section.emitJr("jr", Register.Arch.ra);
        }else{
            return false;
        }
        return true;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {
        section = asmProg.newSection(AssemblyProgram.Section.Type.TEXT);

        String astCode = ASTPrinter.printNode(p);
        int newlineI = astCode.indexOf('\n');
        if(newlineI > 0){
            astCode = astCode.substring(0, newlineI);
        }

        section.emit("AST code: " + astCode);

        // Each function should be produced in its own section.
        // This is is necessary for the register allocator.
       
        p.label = new AssemblyItem.Label(p.name);
        section.emit(p.label);

        if(!emitBuiltins(p.name)){
            findArgumentOffsets(p);
            visitFunctionBlock(p.block);
        }

        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {

        offsetCounter += ((vd.type.bytes()-1)/4+1)*4;

        if(vd.memory == null)
            vd.memory = new VarDecl.Memory(offsetCounter);
        
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es) {
        es.expr.accept(new ExprGen(asmProg, section));
        return null;
    }

    @Override
    public Void visitReturn(Return ret){
        if(ret.value != null){
            throw new ShouldNotReach(); //should be turned into assignment by an ast pass
        }

        emitEpilogue();
        section.emitJr("jr", Register.Arch.ra);
        return null;
    }

    @Override
    public Void visitAssign(Assign a){

        Register rvalue = a.rvalue.accept(new ExprGen(asmProg, section));

        if(a.lvalue instanceof VarExpr){
            VarExpr lVarExpr = (VarExpr) a.lvalue;
            if(lVarExpr.vd.memory.register != null){
                section.emit("addi", lVarExpr.vd.memory.register, rvalue, 0);
                return null;
            }
        }

        Register laddr = a.lvalue.accept(new AddrGen(asmProg, section));

        String storeInstruction = "sw";

        if(a.rvalue.type.equals(BaseType.CHAR)){
            storeInstruction = "sb";
        }

        section.emitStore(storeInstruction, rvalue, laddr, 0);
        return null;
    }

    @Override
    public Void visitIf(If ifs){
        Register cond = ifs.condition.accept(new ExprGen(asmProg, section));
        AssemblyItem.Label ifend = new AssemblyItem.Label("ifend");
        AssemblyItem.Label end = null;

        section.emit("beq", cond, Register.Arch.zero, ifend);

        ifs.consequent.accept(this);
        if(ifs.alternative!=null){
            end = new AssemblyItem.Label("end");
            section.emitJump("j", end);
        }
        section.emit(ifend);
        if(ifs.alternative!=null){
            ifs.alternative.accept(this);
            section.emit(end);
        }
        return null;
    }

    @Override
    public Void visitWhile(While we){
        AssemblyItem.Label top = new AssemblyItem.Label("top");
        AssemblyItem.Label end = new AssemblyItem.Label("end");

        section.emit(top);
        Register condRes = we.condition.accept(new ExprGen(asmProg, section));
        section.emit("beq", condRes, Register.Arch.zero, end);

        we.stmt.accept(this);

        section.emitJump("j", top);
        section.emit(end);

        return null;
    }

    // TODO: to complete (should only deal with statements, expressions should be handled by the ExprGen or AddrGen)
}
