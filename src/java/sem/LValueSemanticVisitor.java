package sem;

import ast.*;

class LValueSemanticVisitor extends BaseSemanticVisitor<Boolean> {
  public Boolean visitBaseType(BaseType bt) {
    return null;
  }
  
  public Boolean visitStructTypeDecl(StructTypeDecl st) {
    return null;
  }
  
  public Boolean visitBlock(Block b) {
    for(Stmt stmt: b.statements){
      stmt.accept(this);
    }
    return null;
  }
  
  public Boolean visitFunDecl(FunDecl p) {
    p.block.accept(this);
    return null;
  }
  
  public Boolean visitProgram(Program p) {
    for(FunDecl fd: p.funDecls){
      fd.accept(this);
    }
    return null;
  }
  
  public Boolean visitVarDecl(VarDecl vd) {
    return null;
  }
  
  public Boolean visitVarExpr(VarExpr v) {
    return Boolean.TRUE;
  }
  
  public Boolean visitStructType(StructType st) {
    return null;
  }
  
  public Boolean visitArrayType(ArrayType at) {
    return null;
  }
  
  public Boolean visitPointerType(PointerType pt) {
    return null;
  }
  
  public Boolean visitAssign(Assign e) {
    Boolean works = e.lvalue.accept(this);
    e.rvalue.accept(this);
    if(works == null || works == Boolean.FALSE){
      error("Cannot assign to "+ASTPrinter.printNode(e.lvalue)+"; Not an lvalue");
    }
    return null;
  }
  
  public Boolean visitAddressOfExpr(AddressOfExpr e) {
    Boolean works = e.expr.accept(this);
    if(works == null||works == Boolean.FALSE){
      error("Cannot take the adress of "+ASTPrinter.printNode(e.expr)+"; Not an lvalue");
    }
    return Boolean.FALSE;
  }
  
  public Boolean visitArrayAccessExpr(ArrayAccessExpr e) {
    e.array.accept(this);
    e.index.accept(this);
    return Boolean.TRUE;
  }
  
  public Boolean visitBinOp(BinOp bop) {
    bop.lhs.accept(this);
    bop.rhs.accept(this);
    return Boolean.FALSE;
  }
  
  public Boolean visitChrLiteral(ChrLiteral cl) {
    return Boolean.FALSE;
  }
  
  public Boolean visitIntLiteral(IntLiteral il) {
    return Boolean.FALSE;
  }
  
  public Boolean visitStrLiteral(StrLiteral sl) {
    return Boolean.FALSE;
  }
  
  public Boolean visitExprStmt(ExprStmt es) {
    es.expr.accept(this);
    return null;
  }
  
  public Boolean visitFieldAccessExpr(FieldAccessExpr e) {
    e.object.accept(this);
    return Boolean.TRUE;
  }
  
  public Boolean visitFunCallExpr(FunCallExpr e) {
    for(Expr arg: e.arguments){
      arg.accept(this);
    }
    return Boolean.FALSE;
  }
  
  public Boolean visitIf(If ifs) {
    ifs.condition.accept(this);
    ifs.consequent.accept(this);
    if(ifs.alternative!=null)ifs.alternative.accept(this);
    return null;
  }
  
  public Boolean visitReturn(Return rets) {
    if(rets.value != null)
      rets.value.accept(this);
    return null;
  }
  
  public Boolean visitSizeOfExpr(SizeOfExpr e) {
    return Boolean.FALSE;
  }
  
  public Boolean visitTypecastExpr(TypecastExpr e) {
    e.expr.accept(this);
    return Boolean.FALSE;
  }
  
  public Boolean visitValueAtExpr(ValueAtExpr e) {
    e.expr.accept(this);
    return Boolean.TRUE;
  }
  
  public Boolean visitWhile(While ws) {
    ws.condition.accept(this);
    ws.stmt.accept(this);
    return null;
  }
}