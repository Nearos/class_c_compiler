package sem;

import ast.*;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

	@Override
	public Type visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {
		// To be completed...
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitArrayType(ArrayType at){
		return null;
	}

	@Override
	public Type visitStructType(StructType st){
		return null;
	}

	@Override
	public Type visitPointerType(PointerType pt){
		return null;
	}

	@Override
	public Type visitAssign(Assign e){
		//TODO:
		return null;
	}

    @Override
    public Type visitAddressOfExpr(AddressOfExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitArrayAccessExpr(ArrayAccessExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitBinOp(BinOp bop){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitChrLiteral(ChrLiteral cl){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitIntLiteral(IntLiteral il){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitStrLiteral(StrLiteral sl){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitExprStmt(ExprStmt es){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitFieldAccessExpr(FieldAccessExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitFunCallExpr(FunCallExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitIf(If ifs){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitReturn(Return rets){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitSizeOfExpr(SizeOfExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitTypecastExpr(TypecastExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitValueAtExpr(ValueAtExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Type visitWhile(While ws){
    	//TODO:
    	return null;
    }


}
