package pass;

import ast.*;
import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

public class ChangeStructAssignmentsToFieldAssignments extends BaseASTPass {

    List<Stmt> extras = null;
    List<VarDecl> extraLocals;
    VarDecl returnArgumentDecl = null;

    private static Expr addType(Type type, Expr ipt){
        ipt.type = type;
        return ipt;
    }

    @Override
    public ASTNode visitAssign(Assign a){
        //structs are always rvalues
        //^ must be run after converting returns to ensure this
        if(!(a.rvalue.type instanceof StructType)){
            return super.visitAssign(a);
        }

        StructType structType = (StructType) a.rvalue.type;

        Expr lvalue = (Expr)a.lvalue.accept(this);
        Expr rvalue = (Expr)a.rvalue.accept(this);

        Type structPointerType = new PointerType(structType);

        VarDecl laddr = new VarDecl(structPointerType, "laddr");
        extraLocals.add(laddr);

        VarDecl raddr = new VarDecl(structPointerType, "raddr");
        extraLocals.add(raddr);

        VarExpr laddrAssign = new VarExpr("laddr");
        laddrAssign.vd = laddr;
        Expr laddrExpr = new AddressOfExpr(lvalue);
        laddrExpr.type = structPointerType;
        extras.add(new Assign(laddrAssign, laddrExpr));

        VarExpr raddrAssign = new VarExpr("raddr");
        raddrAssign.vd = raddr;
        Expr raddrExpr = new AddressOfExpr(rvalue);
        raddrExpr.type = structPointerType;
        extras.add(new Assign(raddrAssign, raddrExpr));

        for(VarDecl vd: structType.declaration.fields){
            VarExpr lAddrUse = new VarExpr("laddr");
            lAddrUse.vd = laddr;

            VarExpr rAddrUse = new VarExpr("raddr");
            rAddrUse.vd = raddr;

            Expr fieldExpr = new FieldAccessExpr(
                addType(structType, new ValueAtExpr(rAddrUse)), 
                vd.varName);
            fieldExpr.type = vd.type;
            Stmt replacement = (Stmt) new Assign(
                new FieldAccessExpr(
                    addType(structType, new ValueAtExpr(lAddrUse)), 
                    vd.varName), 
                fieldExpr).accept(this);

            if(replacement != null){
                extras.add(replacement);
            }
        }

        return null;
    }

    @Override
    public ASTNode visitBlock(Block b){
        List<VarDecl> vars = new LinkedList<>();
        List<Stmt> statements = new LinkedList<>();

        extraLocals = new LinkedList<>();

        for(VarDecl i: b.vars){
            vars.add((VarDecl)i.accept(this));
        }
        extras = new LinkedList<>();
        for(Stmt i: b.statements){
             //empty list of extra statements needed to replace the current one
            Stmt newStmt = (Stmt) i.accept(this); //this will fill it up if needed
            statements.addAll(extras);
            extras = new LinkedList<>();
            if(newStmt != null){
                statements.add(newStmt);
            }
        }
        vars.addAll(extraLocals);
        return new Block(vars, statements);
    }
}