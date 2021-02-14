package ast;

public class ArrayType implements Type {
    
    public final int size;
    public final Type element;

    public ArrayType(int size, Type element){
        this.size = size;
        this.element = element;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }

    public boolean equals(Type other){
        return other instanceof ArrayType && ((ArrayType)other).element.equals(element) && ((ArrayType)other).size == size;
    }
}