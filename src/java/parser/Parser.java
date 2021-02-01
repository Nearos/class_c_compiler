package parser;

import lexer.Token;
import lexer.Tokeniser;
import lexer.Token.TokenClass;

import java.util.LinkedList;
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

    public void parse() {
        // get the first token
        nextToken();

        parseProgram();
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


    private void parseProgram() {
        parseIncludes();
        parseStructDecls();
        parseVarDecls();
        parseFunDecls();
        expect(TokenClass.EOF);
    }

    // includes are ignored, so does not need to return an AST node
    private void parseIncludes() {
        if (accept(TokenClass.INCLUDE)) {
            nextToken();
            expect(TokenClass.STRING_LITERAL);
            parseIncludes();
        }
    }

    private void parseStructDecls() {
        while(accept(TokenClass.STRUCT)){
            if(lookAhead(2).tokenClass!=TokenClass.LBRA)break;
            parseStructDecl();
        }
    }

    private void parseVarDecls() {
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
            parseVarDecl();
        }
    }

    private void parseFunDecls() {
        while(!accept(TokenClass.EOF)){
            parseFunDecl();
        }
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

    private void parseStructDecl(){
        expect(TokenClass.STRUCT);
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LBRA);
        while(!accept(TokenClass.RBRA)){
            parseVarDecl();
        }
        nextToken();
        expect(TokenClass.SC);
    }

    private void parseFunDecl(){
        parseType();
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        parseParams();
        expect(TokenClass.RPAR);
        parseBlock();
    }

    private void parseType(){
        if(accept(TokenClass.STRUCT)){
            nextToken();
            expect(TokenClass.IDENTIFIER);
        }else{
            expect(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR);
        }

        if(accept(TokenClass.ASTERIX)){
            nextToken();
        }
    }

    private void parseParams(){
        if(!accept(TokenClass.INT, TokenClass.VOID, TokenClass.CHAR, TokenClass.STRUCT))return;//this is optional
        parseType();

        expect(TokenClass.IDENTIFIER);
        if(accept(TokenClass.COMMA)){
            nextToken();
            parseParams();
        }
    }

    private void parseVarDecl(){
        parseType();
        expect(TokenClass.IDENTIFIER);
        if(accept(TokenClass.LSBR)){
            nextToken();
            expect(TokenClass.INT_LITERAL);
            expect(TokenClass.RSBR);
        }
        expect(TokenClass.SC);
    }

    private void parseBlock(){
        expect(TokenClass.LBRA);
        while(accept(TokenClass.STRUCT, TokenClass.INT, TokenClass.VOID, TokenClass.CHAR)){
            parseVarDecl();
        }
        while(!accept(TokenClass.RBRA)){
            if(accept(TokenClass.EOF)){
                error(TokenClass.RBRA);
                return;
            }
            parseStmt();
        }
        nextToken();//consume the '}'
    }

    private void parseStmt(){
        if(accept(TokenClass.LBRA)){
            parseBlock();
        }else if(accept(TokenClass.WHILE)){
            nextToken(); //while

            expect(TokenClass.LPAR);//(condition)
            parseExp();
            expect(TokenClass.RPAR);

            parseStmt(); //body;

        }else if(accept(TokenClass.IF)){
            nextToken();//if

            expect(TokenClass.LPAR);//(condition)
            parseExp();
            expect(TokenClass.RPAR);

            parseStmt(); //body;

            if(accept(TokenClass.ELSE)){
                nextToken();//else

                parseStmt(); //body
            }
        }else if(accept(TokenClass.RETURN)){
            nextToken();//return

            if(accept(TokenClass.SC)){
                nextToken();
            }else{
                parseExp();
                expect(TokenClass.SC);
            }
        }else{
            //expression statements
            parseExp();
            if(accept(TokenClass.ASSIGN)){
                nextToken();

                parseExp();
                expect(TokenClass.SC);
            }else{
                expect(TokenClass.SC);
            }
        }
    }

    private void parseExp(){
        if(accept(TokenClass.LPAR)){ //bracketed expression or typecast
            if(isFirstOfType(lookAhead(1).tokenClass)){
                //typecast
                parseTypeCast();
            }else{
                nextToken();
                parseExp();
                expect(TokenClass.RPAR);
            }   
        }else if(accept(TokenClass.INT_LITERAL, TokenClass.CHAR_LITERAL, TokenClass.STRING_LITERAL)){
            nextToken();//do nothing... these are expressions in themselves
        }else if(accept(TokenClass.PLUS, TokenClass.MINUS)){
            nextToken();
            parseExp();
        }else if(accept(TokenClass.IDENTIFIER)){
            if(lookAhead(1).tokenClass == TokenClass.LPAR){
                parseFunctionCall();
            }else{
                nextToken(); //consume identifier; it's an expr
            }
        }else if(accept(TokenClass.ASTERIX)){
            nextToken();
            parseExp();
        }else if(accept(TokenClass.AND)){
            nextToken();
            parseExp();
        }else if(accept(TokenClass.SIZEOF)){
            parseSizeof();
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
        parseExpPrime();
    }

    private void parseExpPrime(){
        boolean recurse = true;
        if(accept(TokenClass.EQ, 
            TokenClass.NE,
            TokenClass.LT,
            TokenClass.GT,
            TokenClass.GE,
            TokenClass.LE,
            TokenClass.PLUS,
            TokenClass.MINUS,
            TokenClass.ASTERIX,
            TokenClass.DIV,
            TokenClass.REM,
            TokenClass.LOGOR,
            TokenClass.LOGAND)){
            nextToken();
            parseExp();
        }else if(accept(TokenClass.LSBR)){
            nextToken();
            parseExp();
            expect(TokenClass.RSBR);
        }else if(accept(TokenClass.DOT)){
            nextToken();
            expect(TokenClass.IDENTIFIER);
        }else{
            recurse = false;
        }

        if(recurse){
            parseExpPrime();
        }
    }

    private void parseFunctionCall(){
        expect(TokenClass.IDENTIFIER);
        expect(TokenClass.LPAR);
        if(accept(TokenClass.RPAR)){
            nextToken();
            return;
        }
        while(true){
            parseExp();
            if(accept(TokenClass.COMMA)){
                nextToken();
            }else{
                break;
            }
        }
        expect(TokenClass.RPAR);
    }

    private void parseSizeof(){
        expect(TokenClass.SIZEOF);
        expect(TokenClass.LPAR);
        parseType();
        expect(TokenClass.RPAR);
    }

    private void parseTypeCast(){
        expect(TokenClass.LPAR);
        parseType();
        expect(TokenClass.RPAR);
        parseExp();
    }
    // to be completed ...
}
