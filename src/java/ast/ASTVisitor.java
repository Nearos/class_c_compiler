package ast;

public interface ASTVisitor<T> {
    public T visitBaseType(BaseType bt);
    public T visitStructTypeDecl(StructTypeDecl st);
    public T visitBlock(Block b);
    public T visitFunDecl(FunDecl p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    public T visitVarExpr(VarExpr v);
    public T visitStructType(StructType st);
    public T visitArrayType(ArrayType at);
    public T visitPointerType(PointerType pt);
    public T visitAssign(Assign e);
    public T visitAddressOfExpr(AddressOfExpr e);
    public T visitArrayAccessExpr(ArrayAccessExpr e);
    public T visitBinOp(BinOp bop);
    public T visitChrLiteral(ChrLiteral cl);
    public T visitIntLiteral(IntLiteral il);
    public T visitStrLiteral(StrLiteral sl);
    public T visitExprStmt(ExprStmt es);
    public T visitFieldAccessExpr(FieldAccessExpr e);
    public T visitFunCallExpr(FunCallExpr e);
    public T visitIf(If ifs);
    public T visitReturn(Return rets);
    public T visitSizeOfExpr(SizeOfExpr e);
    public T visitTypecastExpr(TypecastExpr e);
    public T visitValueAtExpr(ValueAtExpr e);
    public T visitWhile(While ws);

    // to complete ... (should have one visit method for each concrete AST node class)
}
