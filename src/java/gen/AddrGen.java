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
            asmSection.emitLoad("addi", ret, Register.Arch.fp, -v.vd.memory.stackOffset);
        }else{ //global variable
            asmSection.emitLA(ret, v.vd.memory.label);
        }

        return ret;
    }

    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)

}
