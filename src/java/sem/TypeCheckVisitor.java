package sem;

import ast.*;
import java.util.Map;
import java.util.HashMap;
import ast.BinOp.OP;
import java.util.Objects;
import java.util.Iterator;

public class TypeCheckVisitor extends BaseSemanticVisitor<Type> {

    Map<String, StructTypeDecl> structs;
    Type currentReturnType;

    public TypeCheckVisitor(){
        structs = new HashMap<>();
        currentReturnType = null;
    }

	@Override
	public Type visitBaseType(BaseType bt) {
		// To be completed...
		return null;
	}

	@Override
	public Type visitStructTypeDecl(StructTypeDecl st) {
        if(structs.get(st.type.name)!=null){
            error("Struct "+st.type.name+ " declared twice");
        }else{
            structs.put(st.type.name, st);
        }
		
		return null;
	}

	@Override
	public Type visitBlock(Block b) {
        for(VarDecl vd: b.vars){
            vd.accept(this);
        }
		for(Stmt stmt: b.statements){
            stmt.accept(this);
        }
		return null;
	}

	@Override
	public Type visitFunDecl(FunDecl p) {
        currentReturnType = p.type;
		p.block.accept(this);
        currentReturnType = null;
		return null;
	}


	@Override
	public Type visitProgram(Program p) {
		for(StructTypeDecl sdecl: p.structTypeDecls){
            sdecl.accept(this);
        }

        for(VarDecl vd: p.varDecls){
            vd.accept(this);
        }

        for(FunDecl fd: p.funDecls) {
            fd.accept(this);
        }
		return null;
	}

	@Override
	public Type visitVarDecl(VarDecl vd) {
        if(vd.type.equals(BaseType.VOID)){
            error("Can't declare variable "+vd.varName+" of type void");
        }
		vd.type.accept(this);
		return vd.type;
	}

	@Override
	public Type visitVarExpr(VarExpr v) {
        if(v.vd == null){
            error("Variable declaration not found");
            return null; //name analyser didn't associate with declaration
        }
		return v.vd.type;
	}

	@Override
	public Type visitArrayType(ArrayType at){
        at.element.accept(this);
		return at;
	}

	@Override
	public Type visitStructType(StructType st){
        if(structs.get(st.name)==null){
            error("Struct "+st.name+" undefined");
        }
		return st;
	}

	@Override
	public Type visitPointerType(PointerType pt){
        pt.type.accept(this);
		return pt;
	}

	@Override
	public Type visitAssign(Assign e){
		Type lhs = e.lvalue.accept(this);
        Type rhs = e.rvalue.accept(this);
        if(!lhs.equals(rhs)){
            error("Cannot assign type "+ASTPrinter.printNode(rhs)
                +" to "+ASTPrinter.printNode(lhs));
        }
		return null;
	}

    @Override
    public Type visitAddressOfExpr(AddressOfExpr e){
    	return new PointerType(e.expr.accept(this));
    }

    @Override
    public Type visitArrayAccessExpr(ArrayAccessExpr e){
        Type type = e.array.accept(this);
        Type indexType = e.index.accept(this);
        if(!indexType.equals(BaseType.INT)){
            error("Array index must be int. got "
                +ASTPrinter.printNode(type));
            return null;
        } 
        if(type instanceof ArrayType){
            ArrayType arrayType = (ArrayType)type;
            return arrayType.element;
        }else if(type instanceof PointerType){
            PointerType pointerType = (PointerType)type;
            return pointerType.type;
        }
        error("Cannot index type "+ASTPrinter.printNode(type)+"; must be array or pointer");
    	return null; //cannot resolve type
    }

    @Override
    public Type visitBinOp(BinOp bop){
    	Type lhs = bop.lhs.accept(this);
        Type rhs = bop.rhs.accept(this);
        if(lhs.equals(BaseType.INT)
            && rhs.equals(BaseType.INT)){
            return BaseType.INT;
        }else if(
            lhs.equals(rhs)
            && !(lhs instanceof StructType) && !(lhs instanceof ArrayType) 
            && !lhs.equals(BaseType.VOID)
            && !(rhs instanceof StructType) && !(rhs instanceof ArrayType) 
            && !rhs.equals(BaseType.VOID)
            && (bop.op == OP.EQ || bop.op == OP.NE)){
            return BaseType.INT;
        }
        error("Binary operation other than ==, != must take 2 ints. Got types "
            +ASTPrinter.printNode(lhs)
            +" and "
            +ASTPrinter.printNode(rhs));
    	return BaseType.INT;//avoid further errors
    }

    @Override
    public Type visitChrLiteral(ChrLiteral cl){
    	return BaseType.CHAR;
    }

    @Override
    public Type visitIntLiteral(IntLiteral il){
    	return BaseType.INT;
    }

    @Override
    public Type visitStrLiteral(StrLiteral sl){
    	return new ArrayType(sl.value.length()+1, BaseType.CHAR);
    }

    @Override
    public Type visitExprStmt(ExprStmt es){
    	es.expr.accept(this);
    	return null;
    }

    @Override
    public Type visitFieldAccessExpr(FieldAccessExpr e){
    	Type st = e.object.accept(this);
        if(!(st instanceof StructType)){
            error("Cannot access field of non-struct type "+ASTPrinter.printNode(st));
            return null;
        }
        StructType structType = (StructType)st;
        StructTypeDecl std = structs.get(structType.name);
        if(std == null){
            error("Cannot access field of struct "+structType.name+ "; it does not exist.");
            return null;
        }
        VarDecl decl = std.scope.lookupCurrent(e.name).getVar();
        if(decl == null){
            error("Struct "+structType.name+" has no field called "+e.name);
            return null;
        }

    	return decl.type;
    }

    @Override
    public Type visitFunCallExpr(FunCallExpr e){
    	FunDecl decl = e.fd;
        if(decl == null){
            return null;//name analyser didn't find this function
        }
        if(decl.params.size()!=e.arguments.size()){
            error("Wrong number of arguments passed to function "+decl.name
                +". expected "+decl.params.size()+" got "+e.arguments.size());
            return decl.type;
        }

        Iterator<VarDecl> decls = decl.params.iterator();
        Iterator<Expr> givens = e.arguments.iterator();
        for(int i=0; i!=decl.params.size(); i++){
            Type declType = decls.next().type;
            Type givenType = givens.next().accept(this);
            if(!declType.equals(givenType)){
                error("Wrong argument type passed to function "+decl.name
                    +". expected "
                    +ASTPrinter.printNode(declType)
                    +" got "
                    +ASTPrinter.printNode(givenType));
                return decl.type;
            }
        }
    	return decl.type;
    }

    @Override
    public Type visitIf(If ifs){
    	Type condType = ifs.condition.accept(this);
        ifs.consequent.accept(this);
        if(ifs.alternative != null)ifs.alternative.accept(this);
        if(!condType.equals(BaseType.INT)){
            error("If condition must have type int, got type "+ASTPrinter.printNode(condType));
        }
    	return null;
    }

    @Override
    public Type visitReturn(Return rets){
    	Type returnType = rets.value.accept(this);
        if(!currentReturnType.equals(returnType)){

            error("Type returned does not match return type of the function: expected "
                +ASTPrinter.printNode(currentReturnType)
                +" got "
                +ASTPrinter.printNode(returnType));
        }
    	return null;
    }

    @Override
    public Type visitSizeOfExpr(SizeOfExpr e){
    	return BaseType.INT;
    }

    @Override
    public Type visitTypecastExpr(TypecastExpr e){
        Type resType = e.expr.accept(this);
    	if(resType instanceof ArrayType){
            ArrayType at = (ArrayType)resType;
            if(e.type instanceof PointerType 
                && Objects.deepEquals(((PointerType)e.type).type, at.element)){
                return e.type;
            }
        }else if(resType instanceof PointerType && e.type instanceof PointerType){
            return e.type;
        }else if(resType.equals(BaseType.CHAR)&&e.type.equals(BaseType.INT)){
                return e.type;
        }
        error("Illegal typecast "+ASTPrinter.printNode(resType)+" to "+ASTPrinter.printNode(e.type));
    	return null;
    }

    @Override
    public Type visitValueAtExpr(ValueAtExpr e){
        Type type = e.expr.accept(this);
    	if(type instanceof PointerType){
            PointerType pt = (PointerType)type;
            return pt.type;
        }
        error("Cannot dereference non-pointer type " + ASTPrinter.printNode(type));
    	return null;
    }

    @Override
    public Type visitWhile(While ws){
    	Type condType = ws.condition.accept(this);
        ws.stmt.accept(this);
        if(!condType.equals(BaseType.INT)){
            error("While condition must have type int, got type "+ASTPrinter.printNode(condType));
        }
    	return null;
    }


}
