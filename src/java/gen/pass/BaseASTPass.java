package pass;

import ast.*;
import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

public class BaseASTPass implements ASTVisitor<ASTNode>{

    public ASTNode visitBaseType(BaseType bt){
        return bt;
    }

    public ASTNode visitStructTypeDecl(StructTypeDecl st){
        List<VarDecl> fields = new LinkedList<VarDecl>();
        for(VarDecl vd : st.fields){
            fields.add((VarDecl)vd.accept(this));
        }
        StructType type = (StructType)st.type.accept(this);
        StructTypeDecl ret = new StructTypeDecl(type, fields);
        type.declaration = ret;
        st.map = ret;
        return ret;
    }

    public ASTNode visitBlock(Block b){
        List<VarDecl> vars = new LinkedList<>();
        List<Stmt> statements = new LinkedList<>();

        for(VarDecl i: b.vars){
            vars.add((VarDecl)i.accept(this));
        }

        for(Stmt i: b.statements){
            statements.add((Stmt)i.accept(this));
        }
        return new Block(vars, statements);
    }

    public ASTNode visitFunDecl(FunDecl p){
        FunDecl ret = new FunDecl((Type) p.type.accept(this), 
            p.name, 
            p.params.stream().map(q -> {
                VarDecl retp =(VarDecl) q.accept(this);
                return retp;
            }).collect(Collectors.toList()), 
            null);
        p.map = ret;
        Block block = (Block) p.block.accept(this);
        ret.block = block;
        return ret;
    }

    public ASTNode visitProgram(Program p){
        return new Program(
            p.structTypeDecls.stream().map(sd -> (StructTypeDecl) sd.accept(this)).collect(Collectors.toList()),
                p.varDecls.stream().map(vd -> (VarDecl) vd.accept(this)).collect(Collectors.toList()),
                p.funDecls.stream().map(fd -> (FunDecl) fd.accept(this)).collect(Collectors.toList())
            );
    }

    public ASTNode visitVarDecl(VarDecl vd){
        VarDecl ret = new VarDecl(
            (Type) vd.type.accept(this),
            vd.varName
            );
        ret.memory = vd.memory;
        vd.map = ret;
        return ret;
    }

    public ASTNode visitVarExpr(VarExpr v){
        v.vd = v.vd.map;
        return v;
    }

    public ASTNode visitStructType(StructType st){
        st.declaration = st.declaration.map;
        return st;
    }

    public ASTNode visitArrayType(ArrayType at){
        return new ArrayType(at.size, (Type)at.element.accept(this));
    }

    public ASTNode visitPointerType(PointerType pt){
        return new PointerType((Type)pt.type.accept(this));
    }

    public ASTNode visitAssign(Assign e){
        return new Assign((Expr)e.lvalue.accept(this), (Expr)e.rvalue.accept(this));
    }

    public ASTNode visitAddressOfExpr(AddressOfExpr e){
        Expr ret = new AddressOfExpr((Expr)e.expr.accept(this));
        ret.type = e.type;
        return ret;
    }

    public ASTNode visitArrayAccessExpr(ArrayAccessExpr e){
        Expr ret = new ArrayAccessExpr((Expr)e.array.accept(this), (Expr)e.index.accept(this));
        ret.type =e.type;
        return ret;
    }

    public ASTNode visitBinOp(BinOp bop){
        Expr ret = new BinOp((Expr)bop.lhs.accept(this), bop.op, (Expr)bop.rhs.accept(this));
        ret.type =bop.type;
        return ret;
    }

    public ASTNode visitChrLiteral(ChrLiteral cl){
        return cl;
    }

    public ASTNode visitIntLiteral(IntLiteral il){
        return il;
    }

    public ASTNode visitStrLiteral(StrLiteral sl){
        return sl;
    }

    public ASTNode visitExprStmt(ExprStmt es){
        ExprStmt ret = new ExprStmt((Expr)es.expr.accept(this));
        return ret;
    }

    public ASTNode visitFieldAccessExpr(FieldAccessExpr e){
        Expr ret = new FieldAccessExpr((Expr)e.object.accept(this), e.name);
        ret.type = e.type;
        return ret;
    }

    public ASTNode visitFunCallExpr(FunCallExpr e){
        FunCallExpr ret = new FunCallExpr(
            e.function, 
            e.arguments.stream().map(a -> (Expr) a.accept(this)).collect(Collectors.toList()));
        ret.fd = e.fd.map;
        ret.type = e.type;
        return ret;
    }

    public ASTNode visitIf(If ifs){
        Stmt alternative = null;
        if(ifs.alternative != null){
            alternative = (Stmt) ifs.alternative.accept(this);
        }
        return new If(
            (Expr) ifs.condition.accept(this),
            (Stmt) ifs.consequent.accept(this),
            alternative
            );
    }

    public ASTNode visitReturn(Return rets){
        Expr expr = null;
        if(rets.value != null){
            expr = (Expr) rets.value.accept(this);
        }
        return new Return(expr);
    }

    public ASTNode visitSizeOfExpr(SizeOfExpr e){
        Expr ret = new SizeOfExpr((Type) e.type.accept(this));
        ret.type = e.type;
        return ret;
    }

    public ASTNode visitTypecastExpr(TypecastExpr e){
        Expr ret = new TypecastExpr((Type) e.type.accept(this), (Expr) e.expr.accept(this));
        ret.type = e.type;
        return ret;
    }

    public ASTNode visitValueAtExpr(ValueAtExpr e){
        Expr ret = new ValueAtExpr((Expr) e.expr.accept(this));
        ret.type = e.type;
        return ret;
    }

    public ASTNode visitWhile(While ws){
        return new While((Expr) ws.condition.accept(this), (Stmt) ws.stmt.accept(this));
    }

}