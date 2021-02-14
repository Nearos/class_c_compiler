package sem;

import ast.VarDecl;
import ast.FunDecl;

public class VarSymbol extends Symbol{
    public final VarDecl declaration;

    public VarSymbol(VarDecl declaration){
        super(declaration.varName);
        this.declaration = declaration;
    }

    @Override
    public VarDecl getVar(){
        return declaration;
    }
}