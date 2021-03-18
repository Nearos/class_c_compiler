package gen;

import ast.*;
import gen.asm.Register;


public abstract class BaseGen<T> implements ASTVisitor<T>{

    @Override
    public T visitBaseType(BaseType bt){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitStructTypeDecl(StructTypeDecl st){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitBlock(Block b){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitFunDecl(FunDecl p){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitProgram(Program p){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitVarDecl(VarDecl vd){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitVarExpr(VarExpr v){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitStructType(StructType st){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitArrayType(ArrayType at){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitPointerType(PointerType pt){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitAssign(Assign e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitAddressOfExpr(AddressOfExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitArrayAccessExpr(ArrayAccessExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitBinOp(BinOp bop){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitChrLiteral(ChrLiteral cl){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitIntLiteral(IntLiteral il){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitStrLiteral(StrLiteral sl){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitExprStmt(ExprStmt es){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitFieldAccessExpr(FieldAccessExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitFunCallExpr(FunCallExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitIf(If ifs){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitReturn(Return rets){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitSizeOfExpr(SizeOfExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitTypecastExpr(TypecastExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitValueAtExpr(ValueAtExpr e){
        throw new ShouldNotReach();
    }

    @Override    
    public T visitWhile(While ws){
        throw new ShouldNotReach();
    }

}