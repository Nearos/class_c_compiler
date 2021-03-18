package ast;

public class StructType implements Type {

    public final String name;
    public StructTypeDecl declaration;

    public StructType(String name){
        this.name = name;
    }

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitStructType(this);
    }

    public boolean equals(Type other){
        return other instanceof StructType && ((StructType)other).name.equals(name);
    }

    public int bytes(){
        int ret = 0;
        for(VarDecl vd: declaration.fields){
            ret+= ((vd.type.bytes()-1)/4+1)*4;//aligned size
        }
        return ret;
    }

    public int getFieldOffset(String field){
        int offset = 0;
        for(VarDecl vd: declaration.fields){
            if(vd.varName.equals(field)){
                break;
            }
            offset += ((vd.type.bytes()-1)/4+1)*4;
        }
        return offset;
    }
}