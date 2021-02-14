package ast;

public class PointerType implements Type {

    Type type;

    public PointerType(Type type){
        this.type = type;
    }


    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }
}