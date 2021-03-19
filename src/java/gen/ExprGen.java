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
    public Register visitVarExpr(VarExpr v) { 
        Register ret = new Register.Virtual();
        String loadOp;

        if(v.vd.type instanceof ArrayType){
            //should return pointer as register
            if(v.vd.memory.label == null){ //stack variable
                int offset = v.vd.memory.stackOffset; 

                asmSection.emit("addi", ret, Register.Arch.fp, 0);
                asmSection.emit("subi", ret, ret, offset);
            }else{ //global variable
                asmSection.emitLA(ret, v.vd.memory.label);
            }
            return ret;
        }

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

        literalData.emit(new AssemblyItem.Directive.Space(4-((sl.length()+1) % 4)));

        Register ret = new Register.Virtual();
        asmSection.emitLA(ret, label);

        return ret;
    }

    @Override
    public Register visitIntLiteral(IntLiteral lit){
        Register ret = new Register.Virtual();
        asmSection.emit("addi", ret, Register.Arch.zero, lit.value);
        return ret;
    }

    @Override
    public Register visitChrLiteral(ChrLiteral lit){
        Register ret = new Register.Virtual();
        asmSection.emit("addi", ret, Register.Arch.zero, (int)lit.value);
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

    @Override
    public Register visitAddressOfExpr(AddressOfExpr e){
        return e.expr.accept(new AddrGen(asmProg, asmSection));
    }

    @Override 
    public Register visitBinOp(BinOp bop){
        Register ret = new Register.Virtual();
        Register lhs = bop.lhs.accept(this);

        switch(bop.op){
            case ADD:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("add", ret, lhs, rhs);
            }
            break;
            case SUB:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("sub", ret, lhs, rhs);
            }
            break;
            case MUL:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("mult", lhs, rhs);
                asmSection.emit("mflo", ret);
            }
            break;
            case DIV:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("div", lhs, rhs);
                asmSection.emit("mflo", ret);
            }
            break;
            case MOD:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("div", lhs, rhs);
                asmSection.emit("mfhi", ret);
            }
            break;
            case GT:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("slt", ret, rhs, lhs);
            }
            break;
            case LT:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("slt", ret, lhs, rhs);
            }
            break;
            case GE:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("slt", ret, lhs, rhs);
                asmSection.emit("xori", ret, ret, 1);
            }
            break;
            case LE:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("slt", ret, rhs, lhs);
                asmSection.emit("xori", ret, ret, 1);
            }
            break;
            case EQ:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("xor", ret, lhs, rhs);
                Register one = new Register.Virtual();
                asmSection.emit("addi", one, Register.Arch.zero, 1);
                asmSection.emit("sltu", ret, ret, one);
            }
            break;
            case NE:
            {
                Register rhs = bop.rhs.accept(this);
                asmSection.emit("xor", ret, lhs, rhs);
                asmSection.emit("sltu", ret, Register.Arch.zero, ret);
            }
            break;
            case AND:
            {
                AssemblyItem.Label no = new AssemblyItem.Label("no");

                asmSection.emit("addi", ret, Register.Arch.zero, 0); //ret = 0;
                asmSection.emit("beq", lhs, Register.Arch.zero, no); //if lhs != 0

                Register rhs = bop.rhs.accept(this);

                asmSection.emit("add", ret, Register.Arch.zero, rhs); //ret = rhs

                asmSection.emit(no);
            }
            break;
            case OR:
            {
                AssemblyItem.Label no = new AssemblyItem.Label("no");

                asmSection.emit("addi", ret, Register.Arch.zero, 1); //ret = 1;
                asmSection.emit("bne", lhs, Register.Arch.zero, no); //if lsh = 0

                Register rhs = bop.rhs.accept(this);   

                asmSection.emit("add", ret, Register.Arch.zero, rhs); //ret = rhs

                asmSection.emit(no);
            }
            break;

        }

        return ret;
    }

    @Override
    public Register visitArrayAccessExpr(ArrayAccessExpr e){
        Register ret = new Register.Virtual();
        Register array = e.array.accept(new ExprGen(asmProg, asmSection));
        Register index = e.index.accept(new ExprGen(asmProg, asmSection));

        String loadI = "lb";

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
            loadI = "lw";
        }

        asmSection.emit("add", ret, array, index);
        asmSection.emitLoad(loadI, ret, ret, 0);
        return ret;
    }

    @Override
    public Register visitValueAtExpr(ValueAtExpr e){
        Register addr = e.expr.accept(this);
        Register ret = new Register.Virtual();
        String loadI="lw";
        if(e.expr.type.equals(BaseType.CHAR)){
            loadI="lb";
        }
        asmSection.emitLoad(loadI, ret, addr, 0);
        return ret;
    }

    @Override
    public Register visitFieldAccessExpr(FieldAccessExpr e){
        Register structAddr = e.object.accept(new AddrGen(asmProg, asmSection));
        int fieldOffset = ((StructType)((Expr) e.object).type).getFieldOffset(e.name);
        Register ret = new Register.Virtual();
        
        String loadI = "lw";
        if(e.type.equals(BaseType.CHAR)){
            loadI="lb";
        }

        asmSection.emit("addi", ret, structAddr, fieldOffset);
        asmSection.emitLoad(loadI, ret, ret, 0);
        return ret;
    }

    @Override
    public Register visitSizeOfExpr(SizeOfExpr e){
        Register ret = new Register.Virtual();

        asmSection.emit("addi", ret, Register.Arch.zero, e.type.bytes());
        return ret;
    }

    // TODO: to complete (only deal with Expression nodes, anything else should throw ShouldNotReach)
}
