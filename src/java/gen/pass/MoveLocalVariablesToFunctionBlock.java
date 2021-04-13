package pass;

import java.util.*;
import java.util.stream.Collectors;
import ast.*;

public class MoveLocalVariablesToFunctionBlock extends BaseASTPass {
    private List<VarDecl> currentLocals = null;

    @Override
    public ASTNode visitFunDecl(FunDecl fd){
        currentLocals = new LinkedList<>();

        List<VarDecl> newParams = fd.params.stream().map(q -> (VarDecl) q.accept(this)).collect(Collectors.toList());

        FunDecl ret = new FunDecl((Type) fd.type.accept(this), 
            fd.name, 
            newParams, 
            null);
        fd.map = ret;

        Block modified = (Block) fd.block.accept(this);
        modified = new Block(currentLocals, modified.statements);

        ret.block = modified;

        return ret;
    }

    @Override
    public ASTNode visitBlock(Block b){
        for(VarDecl vd: b.vars){
            currentLocals.add((VarDecl) vd.accept(this));
        }

        List<Stmt> statements = new LinkedList<>();
        for(Stmt stmt: b.statements){
            System.out.println(stmt);
            statements.add((Stmt) stmt.accept(this));
            
        }

        return new Block(new LinkedList<>(), statements);
    }
}