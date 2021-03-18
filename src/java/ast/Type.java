package ast;

public interface Type extends ASTNode {

    public <T> T accept(ASTVisitor<T> v);
    public boolean equals(Type other);

    public int bytes(); //returns size in bytes
}
