package pass;

import ast.*;
import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

public class ChangeReturnValuesToPointerArguments extends BaseASTPass {

    List<Stmt> extras = null;
    List<VarDecl> extraLocals;
    VarDecl returnArgumentDecl = null;

    @Override
    public ASTNode visitFunDecl(FunDecl p){
        extraLocals = new LinkedList<>();
        Type returnType = (Type) p.type.accept(this);

        if(returnType.equals(BaseType.VOID)){
            FunDecl ret = (FunDecl) super.visitFunDecl(p);
            extraLocals.addAll(ret.block.vars);
            ret.block = new Block(extraLocals, ret.block.statements);
            return ret;
        }
        p.modified = true;

        Type returnArgumentType = new PointerType(returnType);
        returnArgumentDecl = new VarDecl(returnArgumentType, "return"); 
        //return is a reserved word in c, can't name variable that (I can now though)

        List<VarDecl> args = new LinkedList<>();
        args.add(returnArgumentDecl);
        args.addAll(p.params.stream().map(q -> {
                VarDecl retp =(VarDecl) q.accept(this);
                return retp;
            }).collect(Collectors.toList()));

        FunDecl ret = new FunDecl(BaseType.VOID, 
            p.name, 
            args, 
            null);
        p.map = ret;
        
        Block temp = (Block) p.block.accept(this);
        extraLocals.addAll(temp.vars);
        ret.block = new Block(extraLocals, temp.statements);
        return ret;
    }

    @Override
    public ASTNode visitReturn(Return ret){
        if(ret.value == null){
            return super.visitReturn(ret);
        }

        Expr expr = (Expr) ret.value.accept(this);
        VarExpr returnVarExpr = new VarExpr("return");
        returnVarExpr.vd = returnArgumentDecl;
        extras.add(new Assign(new ValueAtExpr(returnVarExpr), expr));
        extras.add(new Return(null));

        return null;
    }

    @Override
    public ASTNode visitBlock(Block b){
        List<VarDecl> vars = new LinkedList<>();
        List<Stmt> statements = new LinkedList<>();

        for(VarDecl i: b.vars){
            vars.add((VarDecl)i.accept(this));
        }
        List<Stmt> oldExtras = extras;
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
        extras = oldExtras;
        return new Block(vars, statements);
    }

    @Override
    public ASTNode visitFunCallExpr(FunCallExpr e){
        //TODO! Fix!
        if(e.type.equals(BaseType.VOID)){
            //function was not modified
            return super.visitFunCallExpr(e);
        }

        List<Expr> args = new LinkedList<>();

        //create a variable to hold return value named ""
        VarDecl returnValueVar = new VarDecl(e.fd.map.params.get(0).type, "");
        extraLocals.add(returnValueVar);

        //create an expression which takes the address of the variable and add it to the arguments of the function
        VarExpr returnArgumentVarExpr = new VarExpr("");
        returnArgumentVarExpr.vd = returnValueVar;
        AddressOfExpr returnArgument = new AddressOfExpr(returnArgumentVarExpr);
        args.add(returnArgument);

        //recurse with existing arguments (may include more function calls)
        args.addAll(e.arguments.stream().map(a -> (Expr) a.accept(this)).collect(Collectors.toList())); //existing arguments

        //rebuild the expression
        FunCallExpr newFunCallExpr = new FunCallExpr(
            e.function, 
            args);
        newFunCallExpr.fd = e.fd.map;

        extras.add(new ExprStmt(newFunCallExpr));

        //Replace this expression with the value returned by the function, aka the created variable
        VarExpr valueExpr = new VarExpr("");
        valueExpr.vd = returnValueVar;

        valueExpr.type = e.type;

        return valueExpr;
    }
}