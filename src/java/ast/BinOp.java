package ast;

public class BinOp extends Expr{
    public final Expr lhs;
    public final Expr rhs;

    public enum OP{
        ADD, SUB, MUL, DIV, MOD, GT, LT, GE, LE, NE, EQ, OR, AND;

        public String symbol(){
            switch(this){
                case ADD:
                    return "+";
                case SUB:
                    return "-";
                case MUL:
                    return "*";
                case DIV: 
                    return "/";
                case MOD:
                    return "%";
                case GT:
                    return ">";
                case LT:
                    return "<";
                case GE:
                    return ">=";
                case LE:
                    return "<=";
                case NE:
                    return "!=";
                case EQ:
                    return "==";
                case OR:
                    return "||";
                case AND:
                    return "&&";
            }
            return "wtf";
        }

    }

    public final OP op;

    public BinOp(Expr lhs, OP op, Expr rhs){
        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    public <T> T accept(ASTVisitor<T> v){
        return v.visitBinOp(this);
    }
}