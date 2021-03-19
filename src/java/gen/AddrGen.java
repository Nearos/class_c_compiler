package gen;

import ast.*;
import gen.asm.AssemblyProgram;
import gen.asm.Register;

/**
 * Generates code to calculate the address of an expression and return the result in a register.
 */
public class AddrGen extends BaseGen<Register> {


    private AssemblyProgram asmProg;
    private AssemblyProgram.Section asmSection;

    public AddrGen(AssemblyProgram asmProg, AssemblyProgram.Section asmSection) {
        this.asmProg = asmProg;
        this.asmSection = asmSection;
    }

    @Override
    public Register visitVarExpr(VarExpr v) {
        // TODO: to complete
        Register ret = new Register.Virtual();

        if(v.vd.memory.label == null){ //stack variable
            asmSection.emit("addi", ret, Register.Arch.fp, -v.vd.memory.stackOffset);
        }else{ //global variable
            asmSection.emitLA(ret, v.vd.memory.label);
        }

        return ret;
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr e){
        Register ret = new Register.Virtual();
        Register array = e.array.accept(new ExprGen(asmProg, asmSection));
        Register index = e.index.accept(new ExprGen(asmProg, asmSection));

        Type elemType = null;

        if(e.array.type instanceof ArrayType){
            elemType = ((ArrayType)e.array.type).element;
        }else{
            elemType = ((PointerType)e.array.type).type;
        }

        if(!elemType.equals(BaseType.CHAR)){
            Register four = new Register.Virtual();
            asmSection.emit("addi", four, Register.Arch.zero, elemType.bytes());
            asmSection.emit("mult", index, four);
            asmSection.emit("mflo", index);
        }

        asmSection.emit("add", ret, array, index);
        return ret;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr e){
        Register structAddr = e.object.accept(this);
        int fieldOffset = ((StructType)((Expr) e.object).type).getFieldOffset(e.name);
        Register ret = new Register.Virtual();

        asmSection.emit("addi", ret, structAddr, fieldOffset);
        return ret;
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr e){
        return e.expr.accept(new ExprGen(asmProg, asmSection));
    }
    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)

}
