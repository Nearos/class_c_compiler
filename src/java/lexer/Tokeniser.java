package lexer;

import lexer.Token.TokenClass;

import java.io.EOFException;
import java.io.IOException;

/**
 * @author cdubach
 */
public class Tokeniser {

    private Scanner scanner;

    private int error = 0;
    public int getErrorCount() {
	return this.error;
    }

    public Tokeniser(Scanner scanner) {
        this.scanner = scanner;
    }

    private void error(char c, int line, int col) {
        System.out.println("Lexing error: unrecognised character ("+c+") at "+line+":"+col);
	   error++;
    }


    public Token nextToken() {
        Token result;
        try {
             result = next();
        } catch (EOFException eof) {
            // end of file, nothing to worry about, just return EOF token
            return new Token(TokenClass.EOF, scanner.getLine(), scanner.getColumn());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            // something went horribly wrong, abort
            System.exit(-1);
            return null;
        }
        return result;
    }

    private char resolveEscape(char letter){
        if(letter == 't')return '\t';
        if(letter == 'b')return '\b';
        if(letter == 'n')return '\n';
        if(letter == 'r')return '\r';
        if(letter == 'f')return '\f';
        if(letter == '\'')return '\'';
        if(letter == '\"')return '\"';
        if(letter == '\\')return '\\';
        if(letter == '0')return '\0';
        return 'n';
    }

    /*
     * To be completed
     */
    private Token next() throws IOException {

        int line = scanner.getLine();
        int column = scanner.getColumn();

        // get the next character
        char c = scanner.next();
 
        // skip white spaces
        if (Character.isWhitespace(c))
            return next();

        if(Character.isDigit(c)){
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            try{
                c = scanner.peek();
                while(Character.isDigit(c)){
                    scanner.next();
                    sb.append(c);

                    c = scanner.peek();
      
                }
            }catch(EOFException e){}

            return new Token(TokenClass.INT_LITERAL, sb.toString(), line, column);
        }

        if(Character.isLetter(c)||c=='_'){
            StringBuilder sb = new StringBuilder();
            sb.append(c);
            try{
                c = scanner.peek();
                while(Character.isLetter(c)||Character.isDigit(c)||c=='_'){
                    scanner.next();
                    sb.append(c);

                    c = scanner.peek();
                }
            }catch(EOFException e){}//file ended

            String name = sb.toString();

            if(name.equals("int"))
                return new Token(TokenClass.INT, line, column);

            if(name.equals("void"))
                return new Token(TokenClass.VOID, line, column);

            if(name.equals("char"))
                return new Token(TokenClass.CHAR, line, column);

            if(name.equals("if"))
                return new Token(TokenClass.IF, line, column);

            if(name.equals("else"))
                return new Token(TokenClass.ELSE, line, column);

            if(name.equals("while"))
                return new Token(TokenClass.WHILE, line, column);

            if(name.equals("return"))
                return new Token(TokenClass.RETURN, line, column);

            if(name.equals("struct"))
                return new Token(TokenClass.STRUCT, line, column);

            if(name.equals("sizeof"))
                return new Token(TokenClass.SIZEOF, line, column);

            return new Token(TokenClass.IDENTIFIER, name, line, column);
            
        }

        if( c == '\''){
            StringBuilder sb =  new StringBuilder();

            c = scanner.next();
            int read = 0;
            while(c!='\''){
                if( c == '\\'){
                    c = scanner.next(); //this one is allowed to be ' without terminating the literal
                    char unescaped = resolveEscape(c);
                    if(unescaped == 'n'){ //not a valid escape sequence
                        error(c, line, column);
                        return new Token(TokenClass.INVALID, line, column);
                    }
                    sb.append(unescaped); 
                }else{
                    sb.append(c);
                }
                c = scanner.next();
                read++;
            }
            String theCharacter = sb.toString();

            if(read==1)
                return new Token(TokenClass.CHAR_LITERAL, theCharacter, line, column);
        }

        if( c == '\"'){
            StringBuilder sb =  new StringBuilder();

            c = scanner.next();
            while(c!='\"'){
                if( c == '\\'){
                    c = scanner.next(); //this one is allowed to be ' without terminating the literal
                    char unescaped = resolveEscape(c);
                    if(unescaped == 'n'){ //not a valid escape sequence
                        error(c, line, column);
                        return new Token(TokenClass.INVALID, line, column);
                    }
                    sb.append('\\');
                    sb.append(c);
                }else{
                    sb.append(c);
                }
                c = scanner.next();
            }
            String theString = sb.toString();

            return new Token(TokenClass.STRING_LITERAL, theString, line, column);
        }

        if(c == '='){
            try{
                char next = scanner.peek();
                if(next == '='){
                    scanner.next();//consume the peeked character
                    return new Token(TokenClass.EQ, line, column);
                }
            }catch(EOFException e){}//ends with assign

            return new Token(TokenClass.ASSIGN, line, column);
        }

        try{
            if(c == '|'){
                char next = scanner.peek();
                if(next == '|'){
                    scanner.next();
                    return new Token(TokenClass.LOGOR, line, column);
                }//else error, eof error
            }

            if(c=='!'){
                char next = scanner.peek();

                if(next == '='){
                    scanner.next();
                    return new Token(TokenClass.NE, line, column);
                }//else error, eof error
            }
        }catch(EOFException e){} //Error... tokens are incomplete

        if(c=='<'){
            try{
                char next = scanner.peek();

                if(next == '='){
                    scanner.next();
                    return new Token(TokenClass.LE, line, column);
                }
            }catch(EOFException e){}
            return new Token(TokenClass.LT, line, column);
        }

        if(c=='>'){
            try{
                char next = scanner.peek();

                if(next == '='){
                    scanner.next();
                    return new Token(TokenClass.GE, line, column);
                }
            }catch(EOFException e){}
            return new Token(TokenClass.GT, line, column);
        }

        if(c=='#'){
            final String include ="include";
            for(char i : include.toCharArray()){
                try{
                    if(scanner.peek()!=i){
                        error(c, line, column);
                        return new Token(TokenClass.INVALID, line, column);
                    }
                }catch(EOFException e){
                    return new Token(TokenClass.INVALID, line, column); //file ended before token completed
                }
                scanner.next();
            }
            return new Token(TokenClass.INCLUDE, line, column);
        }

        if (c == '+')
            return new Token(TokenClass.PLUS, line, column);

        if (c == '-')
            return new Token(TokenClass.MINUS, line, column);

        if (c == '*')
            return new Token(TokenClass.ASTERIX, line, column);

        if (c == '/'){
            try{
                if(scanner.peek()=='/'){//this is a comment
                    try{
                        while(scanner.next()!='\n'){}
                        return nextToken();
                    }catch(EOFException e){
                        return new Token(TokenClass.EOF, line, column);
                    }
                }
                if(scanner.peek()=='*'){/*so is this*/
                    scanner.next();
                    try{
                        while(true){
                            c = scanner.next();
                            if(c == '*'){
                                c = scanner.peek();
                                if(c=='/'){
                                    scanner.next();
                                    break;
                                }
                            }

                        } 
                    }catch(EOFException e){
                        //unterminated /* comment
                        error(c, line, column);
                        return new Token(TokenClass.INVALID, line, column);
                    }    
                    return nextToken();
                }
            }catch(EOFException e){}//ends with / 
            return new Token(TokenClass.DIV, line, column);
        }

        if (c == '%')
            return new Token(TokenClass.REM, line, column);

        if (c == '&'){
            try{
                char next = scanner.peek();
                if(next == '&'){
                    scanner.next();
                    return new Token(TokenClass.LOGAND, line, column);
                }
            }catch (EOFException e){}
            return new Token(TokenClass.AND, line, column);
        }
        
        if (c == '.')
            return new Token(TokenClass.DOT, line, column);

        if(c == '{'){
            return new Token(TokenClass.LBRA, line, column);
        }

        if(c == '}'){
            return new Token(TokenClass.RBRA, line, column);
        }

        if(c == '('){
            return new Token(TokenClass.LPAR, line, column);
        }

        if(c == ')'){
            return new Token(TokenClass.RPAR, line, column);
        }

        if(c == '['){
            return new Token(TokenClass.LSBR, line, column);
        }

        if(c == ']'){
            return new Token(TokenClass.RSBR, line, column);
        }

        if(c == ';'){
            return new Token(TokenClass.SC, line, column);
        }

        if(c == ','){
            return new Token(TokenClass.COMMA, line, column);
        }


        // if we reach this point, it means we did not recognise a valid token
        error(c, line, column);
        return new Token(TokenClass.INVALID, line, column);
    }


}
