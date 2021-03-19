package pass;

import ast.*;
import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

public class HandleStructArgs extends BaseASTPass {
    @Override
    public ASTNode visitFunDecl(FunDecl p){
        List<Stmt> convertStatements = new LinkedList<>();
        List<VarDecl> newLocals = new LinkedList<>();

        List<VarDecl> newParams = p.params.stream().map(q -> {
                if(q.type instanceof StructType){
                    VarDecl qp = new VarDecl(q.type, q.varName);
                    q.map = qp;

                    newLocals.add(qp);
                    Type oldType = q.type;

                    q = new VarDecl(new PointerType(q.type), q.varName);

                    VarExpr qpExpr = new VarExpr(q.varName);
                    qpExpr.vd = qp;
                    qpExpr.type = oldType;

                    VarExpr qExpr = new VarExpr(q.varName);
                    qExpr.vd = q;
                    qExpr.type = q.type;

                    ValueAtExpr vExpr = new ValueAtExpr(qExpr);
                    vExpr.type = oldType;

                    convertStatements.add(new Assign(qpExpr, vExpr));

                }

                return q;
            }).collect(Collectors.toList());

        FunDecl ret = new FunDecl((Type) p.type.accept(this), 
            p.name, 
            newParams, 
            null);
        p.map = ret;
        Block block = (Block) p.block.accept(this);

        convertStatements.addAll(block.statements);
        newLocals.addAll(block.vars);

        ret.block = new Block(newLocals, convertStatements);
        return ret;
    }

    @Override 
    public ASTNode visitFunCallExpr(FunCallExpr e){
        FunCallExpr ret = new FunCallExpr(
            e.function, 
            e.arguments.stream().map(a -> {
                if(a.type instanceof StructType){
                    a = new AddressOfExpr(a);
                }
                return (Expr) a.accept(this);
            }).collect(Collectors.toList()));
        ret.fd = e.fd.map;
        ret.type = e.type;
        return ret;
    }
}