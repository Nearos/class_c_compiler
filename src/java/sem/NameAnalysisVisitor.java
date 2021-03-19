package sem;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

    Scope scope;

    public NameAnalysisVisitor(Scope scope){
        this.scope = scope;
    }

	@Override
	public Void visitBaseType(BaseType bt) {
		return null;
	}

	@Override
	public Void visitStructTypeDecl(StructTypeDecl sts) {
		Scope oldScope = scope;
        scope = new Scope(null);

        for(VarDecl i: sts.fields){
            i.accept(this);
        }
        
        sts.scope = scope; //associate struct with scope
        scope = oldScope;

		return null;
	}

	@Override
	public Void visitBlock(Block b) {
		Scope oldScope = scope;
        scope = new Scope(scope);

        for(VarDecl vd: b.vars){
            vd.accept(this);
        }

        for(Stmt stmt: b.statements){
            stmt.accept(this);
        }

        scope = oldScope;
		return null;
	}

	@Override
	public Void visitFunDecl(FunDecl p) {
		if(scope.lookupCurrent(p.name)==null){
          scope.put(new FunSymbol(p));
        }else{
            error("Symbol "+p.name+" already declared in this scope");
        }
        Scope oldScope = scope;
        scope = new Scope(scope);
        for(VarDecl vd: p.params){
            vd.accept(this);
        }

        //handle block here because it is one scope
        Block b = p.block;
        for(VarDecl vd: b.vars){
            vd.accept(this);
        }

        for(Stmt stmt: b.statements){
            stmt.accept(this);
        }
        scope = oldScope;
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
        if(scope.lookupCurrent(vd.varName)==null){
		  scope.put(new VarSymbol(vd));
        }else{
             error("Symbol "+vd.varName+" already declared in this scope");
        }
		return null;
	}

	@Override
	public Void visitVarExpr(VarExpr v) {
		Symbol symbol = scope.lookup(v.name);
        if(symbol == null){
             error("Symbol "+v.name+" not declared in this scope");
        }else{
            VarDecl decl = symbol.getVar();
            if(decl == null){
                 error("Symbol "+v.name+" not a variable");
            }else{
                v.vd = decl;
            }
        }
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

    	Symbol symbol = scope.lookup(e.function);
        if(symbol == null){
            error("Symbol "+e.function+" not declared in this scope\n");
        }else{
            FunDecl decl = symbol.getFun();
            if(decl == null){
                error("Symbol "+e.function+" is not a function\n");
            }else{
                e.fd = decl;
            }
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


	// To be completed...


}
