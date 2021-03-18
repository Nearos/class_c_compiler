package ast;

public class PointerType implements Type {

    public final Type type;

    public PointerType(Type type){
        this.type = type;
    }


    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }

    public boolean equals(Type other){
        return other instanceof PointerType && ((PointerType)other).type != null && ((PointerType)other).type.equals(type);
    }

    public int bytes(){
        return 4;
    }
}