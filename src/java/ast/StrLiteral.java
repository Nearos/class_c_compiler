package ast;

public class StrLiteral extends Expr{
    public final String value;

    public int length(){
        int len = 0;
        for(char c: value.toCharArray()){
            if(c!='\\'){
                len++;
            }
        }
        return len;
    }

    public StrLiteral(String value){
        this.value = value;
    }

    public <T> T accept(ASTVisitor<T> v){
        return v.visitStrLiteral(this);
    }
}