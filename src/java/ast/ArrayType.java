package ast;

public class ArrayType implements Type {
    

    public final Type element;
    public final int size;

    public ArrayType(int size, Type element){
        this.element = element;
        this.size = size;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }

    public boolean equals(Type other){
        return other instanceof ArrayType && ((ArrayType)other).element.equals(element) && ((ArrayType)other).size == size;
    }
}