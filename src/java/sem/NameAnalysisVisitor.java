package sem;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	@Override
	public Void visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl p) {
		// To be completed...
		return null;
	}


	@Override
	public Void visitProgram(Program p) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		// To be completed...
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		// To be completed...
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
		//TODO:
		return null;
	}

    @Override
    public Void visitAddressOfExpr(AddressOfExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitBinOp(BinOp bop){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral cl){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitIntLiteral(IntLiteral il){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral sl){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt es){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitIf(If ifs){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitReturn(Return rets){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitTypecastExpr(TypecastExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr e){
    	//TODO:
    	return null;
    }

    @Override
    public Void visitWhile(While ws){
    	//TODO:
    	return null;
    }


	// To be completed...


}
