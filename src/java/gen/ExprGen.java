package gen;

import ast.*;
import gen.asm.*;


/**
 * Generates code to evaluate an expression and return the result in a register.
 */
public class ExprGen extends BaseGen<Register> {

    private AssemblyProgram.Section asmSection;
    private AssemblyProgram asmProg;

    public ExprGen(AssemblyProgram asmProg, AssemblyProgram.Section asmSection) {
        this.asmSection = asmSection;
        this.asmProg = asmProg;
    }

    @Override
    public Register visitVarExpr(VarExpr v) { //all var exprs should be basic types at this point
        Register ret = new Register.Virtual();
        String loadOp;

        switch(v.vd.type.bytes()){
        case 1:
            loadOp="lb"; 
            break;//already false
        case 4:
            loadOp="lw";
            break;
        default:
            throw new ShouldNotReach();
        }

        if(v.vd.memory.label == null){ //stack variable
            asmSection.emitLoad(loadOp, ret, Register.Arch.fp, -v.vd.memory.stackOffset);
        }else{ //global variable
            Register addr = new Register.Virtual();
            asmSection.emitLA(addr, v.vd.memory.label);
            asmSection.emitLoad(loadOp, ret, addr, 0);
        }
        return ret;
    }

    @Override 
    public Register visitStrLiteral(StrLiteral sl){
        AssemblyProgram.Section literalData = asmProg.newSection(AssemblyProgram.Section.Type.DATA);
        AssemblyItem.Label label = new AssemblyItem.Label();

        literalData.emit(label);
        literalData.emit(new AssemblyItem.Directive.Ascii(sl.value));

        Register ret = new Register.Virtual();
        asmSection.emitLA(ret, label);

        return ret;
    }

    //No longer acts as an expression; any return value has been made into a pointer
    @Override
    public Register visitFunCallExpr(FunCallExpr f){
        Register[] argumentRegisters = new Register[f.arguments.size()];
        int i =0;
        for(Expr expr: f.arguments){
            argumentRegisters[i] = expr.accept(this);
            i++;
        }

        int maxOffset = FunGen.findArgumentOffsets(f.fd);

        i=0;
        for(VarDecl vd: f.fd.params){
            //offsets computed in findArgumentOffsets. they are relative to the frame pointer of the called function
            asmSection.emitStore("sw", argumentRegisters[i], Register.Arch.sp, - 4 - maxOffset - vd.memory.stackOffset);
            i++;
        }
        asmSection.emit("subi", Register.Arch.sp,  Register.Arch.sp, maxOffset);
        asmSection.emitJump("jal", f.fd.label);
        asmSection.emit("addi", Register.Arch.sp,  Register.Arch.sp, maxOffset);
        return null;
    }

    @Override
    public Register visitTypecastExpr(TypecastExpr expr){
        return expr.expr.accept(this);
    }

    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)
}
