package sem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import ast.BaseType;
import ast.FunDecl;
import ast.VarDecl;
import ast.Block;
import ast.PointerType;
import ast.Stmt;

public class SemanticAnalyzer {
	
	public int analyze(ast.Program prog) {
		// List of visitors
		ArrayList<SemanticVisitor> visitors = new ArrayList<SemanticVisitor>() {{
			add(new NameAnalysisVisitor(new Scope(null)));
			// To be completed...
		}};
		// Error accumulator
		int errors = 0;

		Collections.reverse(prog.funDecls);

		//add builtins
		VarDecl[] print_sArgs = {new VarDecl(new PointerType(BaseType.CHAR), "s")};
		prog.funDecls.add(new ast.FunDecl(BaseType.VOID, "print_s", Arrays.asList(print_sArgs), 
			new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())));

		VarDecl[] print_iArgs = {new VarDecl(BaseType.INT, "i")};
		prog.funDecls.add(new ast.FunDecl(BaseType.VOID, "print_i", Arrays.asList(print_iArgs), 
			new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())));

		VarDecl[] print_cArgs = {new VarDecl(BaseType.CHAR, "c")};
		prog.funDecls.add(new FunDecl(BaseType.VOID, "print_c", Arrays.asList(print_cArgs), 
			new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())));

		prog.funDecls.add(new FunDecl(BaseType.CHAR, "read_c", new ArrayList<VarDecl>(), 
			new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())));
		prog.funDecls.add(new FunDecl(BaseType.INT, "read_i", new ArrayList<VarDecl>(), 
			new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())));

		VarDecl[] mcmallocArgs = {new VarDecl(BaseType.INT, "size")};
		prog.funDecls.add(new FunDecl(new PointerType(BaseType.VOID), "mcmalloc", Arrays.asList(mcmallocArgs), 
			new Block(new ArrayList<VarDecl>(), new ArrayList<Stmt>())));

		Collections.reverse(prog.funDecls);
		
		// Apply each visitor to the AST
		for (SemanticVisitor v : visitors) {
			prog.accept(v);
			errors += v.getErrorCount();
		}
		
		// Return the number of errors.
		return errors;
	}
}
