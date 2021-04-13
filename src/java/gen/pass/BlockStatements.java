package pass;

import ast.*;
import java.util.Arrays;
import java.util.*;
import java.util.stream.Collectors;

public class BlockStatements extends BaseASTPass {
    @Override
    public ASTNode visitIf(If ifs){
        Expr condition = (Expr) ifs.condition.accept(this);
        Stmt consequent = (Stmt) ifs.consequent.accept(this);
        Stmt alternative = null;

        if(ifs.alternative != null){
            alternative = (Stmt) ifs.alternative.accept(this);
        }

        if(!(consequent instanceof Block)){
            List<Stmt> bs = new LinkedList<Stmt>();
            bs.add(consequent);
            consequent = new Block(new LinkedList<>(), bs);
        }

        if(alternative != null && !(alternative instanceof Block)){
            List<Stmt> bs = new LinkedList<Stmt>();
            bs.add(alternative);
            alternative = new Block(new LinkedList<>(), bs);
        }

        return new If(condition, consequent, alternative);
    }

    @Override
    public ASTNode visitWhile(While we){
        Expr condition = (Expr) we.condition.accept(this);
        Stmt stmt = (Stmt) we.stmt.accept(this);

        if(!(stmt instanceof Block)){
            List<Stmt> bs = new LinkedList<Stmt>();
            bs.add(stmt);
            stmt = new Block(new LinkedList<>(), bs);
        }

        return new While(condition, stmt);
    }

}