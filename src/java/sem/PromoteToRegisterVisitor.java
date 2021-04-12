package sem;

import ast.*;
import gen.asm.Register;

public class PromoteToRegisterVisitor extends BaseSemanticVisitor<Void> {

    @Override
    public Void visitBaseType(BaseType bt) {
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl sts) {

        //struct fields can't be registers

        /*for(VarDecl i: sts.fields){
            i.accept(this);
        }*/
        
        return null;
    }

    @Override
    public Void visitBlock(Block b) {
        for(VarDecl vd: b.vars){
            vd.accept(this);
        }

        for(Stmt stmt: b.statements){
            stmt.accept(this);
        }

        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl p) {

        // Function parameters can't be registers
        // for(VarDecl vd: p.params){
        //     vd.accept(this);
        // }

        Block b = p.block;
        for(VarDecl vd: b.vars){
            vd.accept(this);
        }

        for(Stmt stmt: b.statements){
            stmt.accept(this);
        }
        return null;
    }


    @Override
    public Void visitProgram(Program p) {
        for(StructTypeDecl std: p.structTypeDecls){
            std.accept(this);
        }

        for(VarDecl vd: p.varDecls){
            vd.accept(this);
        }

        for(FunDecl fd: p.funDecls){
            fd.accept(this);
        }
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd) {

        //TODO:
        if(!(vd.type instanceof StructType || vd.type instanceof ArrayType)){
            vd.memory = new VarDecl.Memory(new Register.Virtual());
        }

        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType at){
        return null;
    }

    @Override
    public Void visitStructType(StructType st){
        return null;
    }

    @Override
    public Void visitPointerType(PointerType pt){
        return null;
    }

    @Override
    public Void visitAssign(Assign e){
        e.lvalue.accept(this);
        e.rvalue.accept(this);
        return null;
    }

    @Override
    public Void visitAddressOfExpr(AddressOfExpr e){
        if(e.expr instanceof VarExpr){
            ((VarExpr) e.expr).vd = null; //can't be in register; used in AddressOf
        }
        e.expr.accept(this);
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr e){
        e.array.accept(this);
        e.index.accept(this);
        return null;
    }

    @Override
    public Void visitBinOp(BinOp bop){
        bop.lhs.accept(this);
        bop.rhs.accept(this);
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral cl){
        return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral il){
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral sl){
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es){
        es.expr.accept(this);
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr e){
        e.object.accept(this);
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr e){
        for(Expr arg: e.arguments){
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visitIf(If ifs){
        ifs.condition.accept(this);
        ifs.consequent.accept(this);
        if(ifs.alternative!=null)ifs.alternative.accept(this);        
        return null;
    }

    @Override
    public Void visitReturn(Return rets){
        if(rets.value!=null)rets.value.accept(this);
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr e){
        return null;
    }

    @Override
    public Void visitTypecastExpr(TypecastExpr e){
        e.expr.accept(this);
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr e){
        e.expr.accept(this);
        return null;
    }

    @Override
    public Void visitWhile(While ws){
        ws.condition.accept(this);
        ws.stmt.accept(this);
        return null;
    }
}