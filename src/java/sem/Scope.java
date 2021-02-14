package sem;

import java.util.Map;
import java.util.HashMap;

public class Scope {
	private Scope outer;
	private Map<String, Symbol> symbolTable;
	
	public Scope(Scope outer) { 
		this.outer = outer; 
		symbolTable = new HashMap<String, Symbol>();
	}
	
	public Scope() { this(null); }
	
	public Symbol lookup(String name) {
		Symbol symbol = symbolTable.get(name);
		if(symbol == null&&outer!=null){
			return outer.lookup(name);
		}
		return symbol;
	}
	
	public Symbol lookupCurrent(String name) {
		return symbolTable.get(name);
	}
	
	public void put(Symbol sym) {
		symbolTable.put(sym.name, sym);
	}
}
