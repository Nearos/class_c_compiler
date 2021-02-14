package sem;

import ast.VarDecl;
import ast.FunDecl;

public class FunSymbol extends Symbol{
    public final FunDecl declaration;

    public FunSymbol(FunDecl declaration){
        super(declaration.name);
        this.declaration = declaration;
    }

    @Override
    public FunDecl getFun(){
        return declaration;
    }
}