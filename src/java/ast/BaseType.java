package ast;

public enum BaseType implements Type {
    INT, CHAR, VOID;

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBaseType(this);
    }

    public boolean equals(Type other){
        return other instanceof BaseType && (BaseType)other == this;
    }

    public int bytes(){
        switch(this){
            case INT:
                return 4;
            case CHAR:
                return 1;
            case VOID:
                return 0;
        }
        return 0;
    }
}
