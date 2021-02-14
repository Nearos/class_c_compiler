package ast;

public class StructType implements Type {

    public final String name;

    public StructType(String name){
        this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

    public boolean equals(Type other){
        return other instanceof StructType && ((StructType)other).name.equals(name);
    }
}