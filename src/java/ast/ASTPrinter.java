package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;
    private int indent = 1;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    private void doIndent(){
        for(int i=0; i!=indent; i++){
            writer.print("  ");
        }
    }

    @Override
    public Void visitBlock(Block b) {
        doIndent();
        indent++;
        writer.print("Block(\n");
        String delim = "";
        for(VarDecl vd: b.vars){
            writer.print(delim);
            delim=", \n";
            doIndent();
            vd.accept(this);
        }
        for(Stmt stmt: b.statements){
            writer.print(delim);
            delim=", \n";
            doIndent();
            stmt.accept(this);
        }
        indent--;
        writer.print("\n");
        doIndent();
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        writer.print("FunDecl(");
        fd.type.accept(this);
        writer.print(","+fd.name+",");
        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print(",");
        }
        fd.block.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        writer.print("Program(");
        String delimiter = "\n";
        for (StructTypeDecl std : p.structTypeDecls) {
            writer.print(delimiter);
            delimiter = ", \n";
            doIndent();
            std.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = ", \n";
            doIndent();
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = ", \n";
            doIndent();
            fd.accept(this);
        }
        writer.print("\n)\n");
	    writer.flush();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");
        vd.type.accept(this);
        writer.print(","+vd.varName);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        writer.print("VarExpr(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
        switch(bt){
            case INT:
                writer.print("INT");
                break;
            case VOID:
                writer.print("VOID");
                break;
            case CHAR:
                writer.print("CHAR");

        }
        return null;
    }

    @Override
    public Void visitStructTypeDecl(StructTypeDecl st) {
        indent++;
        writer.print("StructTypeDecl(");
        st.type.accept(this);
        for(VarDecl vd: st.fields){
            writer.print(", \n");
            doIndent();
            vd.accept(this);
        }
        indent--;
        writer.print("\n");
        doIndent();
        writer.print(")");
        return null;
    }

    @Override 
    public Void visitPointerType(PointerType pt){
        writer.print("PointerType(");
        pt.type.accept(this);
        writer.print(")");
        return null;
    }

    @Override 
    public Void visitStructType(StructType st){
        writer.print("StructType(");
        writer.print(st.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitArrayType(ArrayType at){
        writer.print("ArrayType(");
        writer.print(at.size);
        writer.print(", ");
        at.element.accept(this);
        writer.print(")");
        return null;
    }
    
    @Override
    public Void visitAssign(Assign ass){
        writer.print("Assign(");
        ass.lvalue.accept(this);
        writer.print(", ");
        ass.rvalue.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitAddressOfExpr(AddressOfExpr e){
        writer.print("AddressOfExpr(");
        e.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitArrayAccessExpr(ArrayAccessExpr e){
        writer.print("ArrayAccessExpr(");
        e.array.accept(this);
        writer.print(", ");
        e.index.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBinOp(BinOp bop){
        writer.print("BinOp(");
        bop.lhs.accept(this);
        writer.print(", "+bop.op.name()+", ");
        bop.rhs.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitChrLiteral(ChrLiteral clit){
        writer.print("ChrLiteral("+Character.toString(clit.value)+")");
        return null;
    }

    @Override 
    public Void visitIntLiteral(IntLiteral ilit){
        writer.print("IntLiteral("+Integer.toString(ilit.value)+")");
        return null;
    }

    @Override
    public Void visitStrLiteral(StrLiteral slit){
        writer.print("StrLiteral("+slit.value+")");
        return null;
    }

    @Override 
    public Void visitExprStmt(ExprStmt es){
        writer.print("ExprStmt(");
        es.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFieldAccessExpr(FieldAccessExpr fae){
        writer.print("FieldAccessExpr(");
        fae.object.accept(this);
        writer.print(", "+fae.name+")");
        return null;
    }

    @Override
    public Void visitFunCallExpr(FunCallExpr fe){
        writer.print("FunCallExpr("+fe.function);

        for(Expr i: fe.arguments){
            writer.print(", ");
            i.accept(this);
        }
        writer.print(")");
        return null;

    }

    @Override
    public Void visitIf(If ife){
        writer.print("If(");
        ife.condition.accept(this);
        writer.print(", ");
        ife.consequent.accept(this);
        if(ife.alternative!=null){
            writer.print(", ");
            ife.alternative.accept(this);
        }
        writer.print(")");
        return null;
    }

    @Override
    public Void visitReturn(Return rs){
        writer.print("Return(");
        if(rs.value!=null)
            rs.value.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitSizeOfExpr(SizeOfExpr se){
        writer.print("SizeOfExpr(");
        se.type.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitTypecastExpr(TypecastExpr ce){
        writer.print("TypecastExpr(");
        ce.type.accept(this);
        writer.print(", ");
        ce.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitValueAtExpr(ValueAtExpr de){
        writer.print("ValueAtExpr(");
        de.expr.accept(this);
        writer.print(")");
        return null;
    }

    @Override 
    public Void visitWhile(While we){
        writer.print("While(");
        we.condition.accept(this);
        writer.print(", ");
        we.stmt.accept(this);
        writer.print(")");
        return null;
    }
}
