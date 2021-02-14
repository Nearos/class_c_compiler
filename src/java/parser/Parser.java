package parser;

import ast.*;
import ast.BinOp.OP;
import lexer.Token;
import lexer.Token.TokenClass;
import lexer.Tokeniser;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author cdubach
 */
public class Parser {

    private Token token;

    // use for backtracking (useful for distinguishing decls from procs when parsing a program for instance)
    private Queue<Token> buffer = new LinkedList<>();

    private final Tokeniser tokeniser;



    public Parser(Tokeniser tokeniser) {
        this.tokeniser = tokeniser;
    }

    public Program parse() {
        // get the first token
        nextToken();

        return parseProgram();
    }

    public int getErrorCount() {
        return error;
    }

    private int error = 0;
    private Token lastErrorToken;

    private void error(TokenClass... expected) {

        if (lastErrorToken == token) {
            // skip this error, same token causing trouble
            return;
        }

        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (TokenClass e : expected) {
            sb.append(sep);
            sb.append(e);
            sep = "|";
        }
        System.out.println("Parsing error: expected ("+sb+") found ("+token+") at "+token.position+"\n\n");
        //Thread.dumpStack();

        error++;
        lastErrorToken = token;
    }

    /*
     * Look ahead the i^th element from the stream of token.
     * i should be >= 1
     */
    private Token lookAhead(int i) {
        // ensures the buffer has the element we want to look ahead
        while (buffer.size() < i)
            buffer.add(tokeniser.nextToken());
        assert buffer.size() >= i;

        int cnt=1;
        for (Token t : buffer) {
            if (cnt == i)
                return t;
            cnt++;
        }

        assert false; // should never reach this
        return null;
    }


    /*
     * Consumes the next token from the tokeniser or the buffer if not empty.
     */
    private void nextToken() {
        if (!buffer.isEmpty())
            token = buffer.remove();
        else
            token = tokeniser.nextToken();
    }

    /*
     * If the current token is equals to the expected one, then skip it, otherwise report an error.
     * Returns the expected token or null if an error occurred.
     */
    private Token expect(TokenClass... expected) {
        for (TokenClass e : expected) {
            if (e == token.tokenClass) {
                Token cur = token;
                nextToken();
                return cur;
            }
        }

        error(expected);
        return null;
    }

    /*
    * Returns true if the current token is equals to any of the expected ones.
    */
    private boolean accept(TokenClass... expected) {
        boolean result = false;
        for (TokenClass e : expected)
            result |= (e == token.tokenClass);
        return result;
    }


    private Program parseProgram() {
        parseIncludes();
        List<StructTypeDecl> stds = parseStructDecls();
        List<VarDecl> vds = parseVarDecls();
        List<FunDecl> fds = parseFunDecls();
        expect(TokenClass.EOF);
        return new Program(stds, vds, fds);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private List<StructTypeDecl> parseStructDecls() {
        List<StructTypeDecl> ret = new LinkedList<StructTypeDecl>();
        while(accept(TokenClass.STRUCT)){
            if(lookAhead(2).tokenClass!=TokenClass.LBRA)break;
            ret.add(parseStructDecl());
        }
        return ret;
    }

    private List<VarDecl> parseVarDecls() {
        LinkedList<VarDecl> ret = new LinkedList<VarDecl>();

        while(accept(TokenClass.STRUCT, TokenClass.INT, TokenClass.VOID, TokenClass.CHAR)){
            int checkLoc = 2;//position to check for (
            if(accept(TokenClass.STRUCT)){
                checkLoc++; //type id ? -> struct id id ?
            }
            if(lookAhead(checkLoc).tokenClass == TokenClass.ASTERIX){
                checkLoc++; //decl ? to decl * ?
            }
            if(lookAhead(checkLoc).tokenClass == TokenClass.LPAR){
                break; //function
            }
            ret.add(parseVarDecl());

        }

        return ret;
    }

    private List<FunDecl> parseFunDecls() {
        LinkedList<FunDecl> ret = new LinkedList<FunDecl>();
        while(accept(TokenClass.INT, TokenClass.CHAR, TokenClass.VOID, TokenClass.STRUCT)){
            ret.add(parseFunDecl());
        }
        return ret;
    }

    private boolean isFirstOfType(TokenClass tk){
        switch(tk){
            case INT:
            case STRUCT:
            case CHAR:
            case VOID:
                return true;
            default:
                return false;
        }
        //unreachable:
    }

    private StructTypeDecl parseStructDecl(){
        List<VarDecl> fields = new LinkedList<VarDecl>();

        expect(TokenClass.STRUCT);
        Token tk = expect(TokenClass.IDENTIFIER);
        if(tk == null) return null;
        StructType type = new StructType(tk.data);
        expect(TokenClass.LBRA);
        fields.add(parseVarDecl());
        while(accept(TokenClass.STRUCT, TokenClass.VOID, TokenClass.INT, TokenClass.CHAR)){
            VarDecl vd = parseVarDecl();
            fields.add(vd);
        }
        expect(TokenClass.RBRA);
        expect(TokenClass.SC);

        return new StructTypeDecl(type, fields);
    }

    private FunDecl parseFunDecl(){
        Type type = parseType();
        Token tk = expect(TokenClass.IDENTIFIER);
        if(tk==null)return null;
        String name = tk.data;
        expect(TokenClass.LPAR);
        List<VarDecl> params = parseParams();
        expect(TokenClass.RPAR);
        Block bk = parseBlock();
        return new FunDecl(type, name, params, bk);
    }

    private Type parseType(){
        Type ret = null;
        if(accept(TokenClass.STRUCT)){
            nextToken();
            Token tk = expect(TokenClass.IDENTIFIER);
            if(tk!=null){
                ret = new StructType(tk.data);
            }
        }else{
            Token tk = expect(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR);
            if(tk==null)return null;
            switch(tk.tokenClass){
                case INT:
                    ret = BaseType.INT;
                    break;
                case VOID:
                    ret = BaseType.VOID;
                    break;
                case CHAR:
                    ret = BaseType.CHAR;
                    break;
            }
        }

        if(accept(TokenClass.ASTERIX)){
            nextToken();
            ret = new PointerType(ret);
        }

        return ret;
    }

    private List<VarDecl> parseParams(){
        if(!accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT))
            return new LinkedList<VarDecl>();//this is optional

        Type type = parseType();

        Token tk = expect(TokenClass.IDENTIFIER);
        if(tk==null)return null;
        VarDecl decl = new VarDecl(type, tk.data);
        LinkedList<VarDecl> ret = new LinkedList<VarDecl>();
        ret.add(decl);
        if(accept(TokenClass.COMMA)){
            nextToken();
            ret.addAll(parseParams());//So functional
        }
        return ret;
    }

    private VarDecl parseVarDecl(){
        Type type = parseType();
        Token tk = expect(TokenClass.IDENTIFIER);
        String name;
        if(tk!=null){
            name = tk.data; 
        }else{
            return null;//failure... return null string is fine since parser fails here
        }
        if(accept(TokenClass.LSBR)){
            nextToken();
            Token size_literal = expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
            if(size_literal!=null)
                type = new ArrayType(Integer.parseInt(size_literal.data), type);
        }
        expect(TokenClass.SC);
        return new VarDecl(type, name);
    }

    private Block parseBlock(){
        expect(TokenClass.LBRA);
        LinkedList<VarDecl> vars = new LinkedList<VarDecl>();
        while(accept(TokenClass.STRUCT, TokenClass.INT, TokenClass.VOID, TokenClass.CHAR)){
            vars.add(parseVarDecl());
        }
        LinkedList<Stmt> statements = new LinkedList<Stmt>();
        //while accept(first(stmt))
        while(accept(TokenClass.LBRA, TokenClass.WHILE, TokenClass.IF, TokenClass.RETURN,
            TokenClass.LPAR, TokenClass.IDENTIFIER, TokenClass.INT_LITERAL, TokenClass.PLUS, TokenClass.MINUS,
            TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL, TokenClass.ASTERIX, TokenClass.AND, TokenClass.SIZEOF)){

            if(accept(TokenClass.EOF)){
                error(TokenClass.RBRA);
                return null;
            }
            statements.add(parseStmt());
        }
        nextToken();//consume the '}'
        return new Block(vars, statements);
    }

    private Stmt parseStmt(){
        if(accept(TokenClass.LBRA)){
            return parseBlock();
        }else if(accept(TokenClass.WHILE)){
            nextToken(); //while

            expect(TokenClass.LPAR);//(condition)
            Expr cond = parseExp();
            expect(TokenClass.RPAR);

            Stmt stmt = parseStmt(); //body;

            return new While(cond, stmt);

        }else if(accept(TokenClass.IF)){
            nextToken();//if

            expect(TokenClass.LPAR);//(condition)
            Expr cond = parseExp();
            expect(TokenClass.RPAR);

            Stmt conseq = parseStmt(); //body;
            Stmt alt = null;
            if(accept(TokenClass.ELSE)){
                nextToken();//else

                alt = parseStmt(); //body
            }

            return new If(cond, conseq, alt);
        }else if(accept(TokenClass.RETURN)){
            nextToken();//return
            Expr value = null;
            if(accept(TokenClass.SC)){
                nextToken();
            }else{
                value = parseExp();
                expect(TokenClass.SC);
            }
            return new Return(value);
        }else{
            //expression statements
            Expr expr = parseExp();
            if(accept(TokenClass.ASSIGN)){
                nextToken();

                Expr rvalue = parseExp();
                expect(TokenClass.SC);
                return new Assign(expr, rvalue);
            }else{
                expect(TokenClass.SC);
                return new ExprStmt(expr);
            }
        }
    }

    private Expr parseExp(){
        Expr ret = parseLogicalTerm();

        while(accept(TokenClass.LOGOR)){
            nextToken();
            ret = new BinOp(ret, OP.OR, parseLogicalTerm());
        }

        return ret;
    }

    private Expr parseLogicalTerm(){
        Expr ret = parsePredicate();

        while(accept(TokenClass.LOGAND)){
            nextToken();
            ret = new BinOp(ret, OP.AND, parsePredicate());
        }
        return ret;
    }

    private Expr parsePredicate(){
        Expr ret = parseComparison();

        while(accept(TokenClass.EQ, 
            TokenClass.NE)){

            Token op = expect(TokenClass.EQ, 
                TokenClass.NE);

            switch(op.tokenClass){
                case EQ:
                    ret = new BinOp(ret, OP.EQ, parseComparison());
                    break;
                case NE:
                    ret = new BinOp(ret, OP.NE, parseComparison());
                    break;
            }
        }
        return ret;
    }

    private Expr parseComparison(){
        Expr ret = parsePolynomial();

        while(accept(
            TokenClass.LT,
            TokenClass.GT,
            TokenClass.GE,
            TokenClass.LE)){

            Token op = expect(
                TokenClass.LT,
                TokenClass.GT,
                TokenClass.GE,
                TokenClass.LE);

            switch(op.tokenClass){
                case LT:
                    ret = new BinOp(ret, OP.LT, parsePolynomial());
                    break;
                case GT:
                    ret = new BinOp(ret, OP.GT, parsePolynomial());
                    break;
                case GE:
                    ret = new BinOp(ret, OP.GE, parsePolynomial());
                    break;
                case LE:
                    ret = new BinOp(ret, OP.LE, parsePolynomial());
                    break;
            }
        }
        return ret;
    }

    private Expr parsePolynomial(){
        Expr ret = parseTerm();

        while(accept(TokenClass.PLUS,
            TokenClass.MINUS)){
            Token op = expect(TokenClass.PLUS,
                TokenClass.MINUS);

            switch(op.tokenClass){
                case PLUS:
                    ret = new BinOp(ret, OP.ADD, parseTerm());
                    break;
                case MINUS:
                    ret = new BinOp(ret, OP.SUB, parseTerm());
                    break;
            }
        }

        return ret;
    }

    private Expr parseTerm(){
        Expr ret = parseFactor();
        while(accept(TokenClass.ASTERIX,
                TokenClass.DIV,
                TokenClass.REM)){
            Token op = expect(TokenClass.ASTERIX,
                TokenClass.DIV,
                TokenClass.REM);
            switch(op.tokenClass){
                case ASTERIX:
                    ret = new BinOp(ret, OP.MUL, parseFactor());
                    break;
                case DIV:
                    ret = new BinOp(ret, OP.DIV, parseFactor());
                    break;
                case REM:
                    ret = new BinOp(ret, OP.MOD, parseFactor());
                    break;
            }
        }
        return ret;
    }

    private Expr parseFactor(){
        if(accept(TokenClass.PLUS)){
            nextToken();
            return parseFactor();
        }else if(accept(TokenClass.MINUS)){
            nextToken();
            return new BinOp(new IntLiteral(0), OP.SUB, parseFactor());
        }else if(accept(TokenClass.AND)){
            nextToken();
            return new AddressOfExpr(parseFactor());
        }else if(accept(TokenClass.ASTERIX)){
            nextToken();
            return new ValueAtExpr(parseFactor());
        }else if(accept(TokenClass.LPAR)){
            if(isFirstOfType(lookAhead(1).tokenClass)){
                //typecast
                return parseTypeCast();
            }
        }
        return parseValue();
    }

    private Expr parseValue(){
        Expr parsed = null;

        if(accept(TokenClass.LPAR)){ //bracketed expression or typecast
            nextToken();
            parsed = parseExp();
            expect(TokenClass.RPAR);
        }else if(accept(TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)){
            Token literal = expect(TokenClass.INT_LITERAL, 
                TokenClass.CHAR_LITERAL, 
                TokenClass.STRING_LITERAL);//do nothing... these are expressions in themselves

            switch(literal.tokenClass){
                case INT_LITERAL:
                    parsed = new IntLiteral(Integer.parseInt(literal.data));
                    break;
                case CHAR_LITERAL:
                    parsed = new ChrLiteral(literal.data.charAt(0));
                    break;
                case STRING_LITERAL:
                    parsed =  new StrLiteral(literal.data);
                    break;
            }
        }else if(accept(TokenClass.IDENTIFIER)){
            if(lookAhead(1).tokenClass == TokenClass.LPAR){
                parsed = parseFunctionCall();
            }else{
                Token id = expect(TokenClass.IDENTIFIER); //consume identifier; it's an expr
                parsed = new VarExpr(id.data);
            }
        }else if(accept(TokenClass.SIZEOF)){
            parsed = parseSizeof();
        }else{
            error(
                TokenClass.LPAR, 
                TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL,
                TokenClass.PLUS, TokenClass.MINUS,
                TokenClass.IDENTIFIER,
                TokenClass.ASTERIX,
                TokenClass.AND,
                TokenClass.SIZEOF
                );
            nextToken();
        }

        while(accept(TokenClass.LSBR, TokenClass.DOT)){
            Token tok = expect(TokenClass.LSBR, TokenClass.DOT);
            switch(tok.tokenClass){
                case LSBR:
                    Expr inside = parseExp();
                    expect(TokenClass.RSBR);
                    parsed = new ArrayAccessExpr(parsed, inside);
                    break;
                case DOT:
                    Token id = expect(TokenClass.IDENTIFIER);
                    parsed = new FieldAccessExpr(parsed, id.data);
                    break;
            }
        }   
        return parsed; 
    }

    private FunCallExpr parseFunctionCall(){
        Token name = expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        LinkedList<Expr> args = new LinkedList<Expr>();
        if(accept(TokenClass.RPAR)){
            nextToken();
            return new FunCallExpr(name.data, args);
        }
        
        while(true){
            args.add(parseExp());
            if(accept(TokenClass.COMMA)){
                nextToken();
            }else{
                break;
            }
        }
        expect(TokenClass.RPAR);
        return new FunCallExpr(name.data, args);

    }

    private SizeOfExpr parseSizeof(){
        expect(TokenClass.SIZEOF);
        expect(TokenClass.LPAR);
        
        Type type = parseType();
        expect(TokenClass.RPAR);
        return new SizeOfExpr(type);
    }

    private TypecastExpr parseTypeCast(){
        expect(TokenClass.LPAR);
        Type type = parseType();
        expect(TokenClass.RPAR);
        Expr expr = parseFactor();
        return new TypecastExpr(type, expr);
    }
    // to be completed ...
}
