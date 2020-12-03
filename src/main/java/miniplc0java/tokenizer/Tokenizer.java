package miniplc0java.tokenizer;

import miniplc0java.error.CompileError;
import miniplc0java.error.TokenizeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.util.Pos;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    // 这里本来是想实现 Iterator<Token> 的，但是 Iterator 不允许抛异常，于是就这样了
    /**
     * 获取下一个 Token
     * 
     * @return
     * @throws TokenizeError 如果解析有异常则抛出
     */
    public Token nextToken() throws TokenizeError {
        it.readAll();
        // 跳过之前的所有空白字符
        skipSpaceCharacters();

        if (it.isEOF()) {
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }
        char peek = it.peekChar();
        if (Character.isDigit(peek)) {
            return lexUIntOrDouble();
        } else if (Character.isAlphabetic(peek)||peek=='_') {
            return lexIdentOrKeyword();
        }else if(peek=='"'){
            return lexString();
        }else if(peek=='\''){
            return lexChar();
        }
        else {
            Token token = lexOperatorOrUnknown();
            if(it.peekChar()=='/'&&token.getTokenType()==TokenType.Div){
                while(it.nextChar()!='\n');
                return nextToken();
            }
            return token;
        }
    }

    private Token lexChar() throws TokenizeError {
        Pos start = it.currentPos();
        StringBuffer sb = new StringBuffer();
        sb.append(it.nextChar());
        char peek = it.peekChar();
        if(peek!='\''&&peek!='\0'&&peek!='\n'){
            if(peek=='\\'){
                sb.append(it.nextChar());
                peek=it.peekChar();
                if(!isEscape_sequence(peek)){
                    throw new TokenizeError(ErrorCode.InvalidChar,it.currentPos());
                }else{
                    sb.append(it.nextChar());
                    peek = it.peekChar();
                }
            }else{
                sb.append(it.nextChar());
                peek = it.peekChar();
            }
        }
        if(it.peekChar()!='\''){
            throw new TokenizeError(ErrorCode.InvalidChar,it.currentPos());
        }
        sb.append(it.nextChar());
        return new Token(TokenType.String,sb.toString(),start,it.currentPos());
    }

    private boolean isEscape_sequence(char peek){
        return peek=='\\'||peek=='"'||peek=='\''||peek=='n'
                ||peek=='r'||peek=='t';
    }

    private Token lexString() throws TokenizeError {
        Pos start = it.currentPos();
        StringBuffer sb = new StringBuffer();
        sb.append(it.nextChar());
        char peek = it.peekChar();
        while(peek!='"'&&peek!='\0'){
            if(peek=='\n') {
                it.nextChar();
                peek = it.peekChar();
            }
            else if(peek=='\\'){
                sb.append(it.nextChar());
                peek=it.peekChar();
                if(!isEscape_sequence(peek)){
                    throw new TokenizeError(ErrorCode.InvalidString,it.currentPos());
                }
                else{
                    sb.append(it.nextChar());
                    peek = it.peekChar();
                }
            }else{
                sb.append(it.nextChar());
                peek=it.peekChar();
            }

        }
        if(it.peekChar()!='"'){
            throw new TokenizeError(ErrorCode.InvalidString,it.currentPos());
        }
        sb.append(it.nextChar());
        return new Token(TokenType.String,sb.toString(),start,it.currentPos());
    }

    private Token lexUIntOrDouble() throws TokenizeError {
        Pos start = it.currentPos();
        StringBuffer sb = new StringBuffer();
        do{
            sb.append(it.nextChar());
        }while(Character.isDigit(it.peekChar()));

        //有小数点，是double
        if(it.peekChar()=='.'){
            Double value =0.0;
            sb.append(it.nextChar());
            //小数点后不是数字,报错
            if(!Character.isDigit(it.peekChar())){
                throw new TokenizeError(ErrorCode.InvalidDoubleValue,it.currentPos());
            }
            do{
                sb.append(it.nextChar());
            }while(Character.isDigit(it.peekChar()));
            char peek = it.peekChar();
            if(peek=='e'||peek=='E'){
                sb.append(it.nextChar());
                if(it.peekChar()=='+'||it.peekChar()=='-'){
                    sb.append(it.nextChar());
                }
                if(!Character.isDigit(it.peekChar())){
                    throw new TokenizeError(ErrorCode.InvalidDoubleValue,it.currentPos());
                }
                do{
                    sb.append(it.nextChar());
                }while(Character.isDigit(it.peekChar()));
            }
            try{
                value = Double.parseDouble(sb.toString());
            }catch (Exception e){
                throw new TokenizeError(ErrorCode.InvalidDoubleValue,start);
            }
            return new Token(TokenType.Double,value,start,it.currentPos());
        }
        Long value = 0L;
        try{
             value = Long.valueOf(sb.toString());
        }catch (Exception e){
            throw new TokenizeError(ErrorCode.IntegerOverflow,start);
        }
        return new Token(TokenType.Uint,value,start,it.currentPos());
    }

    private Token lexIdentOrKeyword() throws TokenizeError {
        Pos start = it.currentPos();
        StringBuffer sb = new StringBuffer();
        do{
            sb.append(it.nextChar());
        }while(Character.isLetterOrDigit(it.peekChar())||it.peekChar()=='_');
        String value = sb.toString();
        TokenType tokenType = null;
        switch (value){
            case "fn":
                tokenType = TokenType.Fn;break;
            case "let":
                tokenType = TokenType.Let;break;
            case "const":
                tokenType = TokenType.Const;break;
            case "as":
                tokenType = TokenType.As;break;
            case "while":
                tokenType = TokenType.While;break;
            case "if":
                tokenType = TokenType.If;break;
            case "else":
                tokenType = TokenType.Else;break;
            case "return":
                tokenType = TokenType.Return;break;
            case "break":
                tokenType = TokenType.Break;break;
            case "continue":
                tokenType = TokenType.Continue;break;
            default:
                tokenType = TokenType.Ident;break;
        }
        return new Token(tokenType,value,start,it.currentPos());
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            case '+':
                return new Token(TokenType.Plus, '+', it.previousPos(), it.currentPos());
            case '-':
                if(it.peekChar()=='>'){
                    it.nextChar();
                    return new Token(TokenType.Arrow,"->",it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Minus, '-', it.previousPos(), it.currentPos());
            case '*':
                return new Token(TokenType.Mul, '*', it.previousPos(), it.currentPos());
            case '/':
                return new Token(TokenType.Div, '/', it.previousPos(), it.currentPos());
            case '=':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.Eq,"==",it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Assign, '=', it.previousPos(), it.currentPos());
            case '!':
                if(it.nextChar()=='='){
                    return new Token(TokenType.Neq, "!=", it.previousPos(), it.currentPos());
                }
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
            case '<':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.LE,"<=",it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Lt, '<', it.previousPos(), it.currentPos());
            case '>':
                if(it.peekChar()=='='){
                    it.nextChar();
                    return new Token(TokenType.GE,">=",it.previousPos(), it.currentPos());
                }
                return new Token(TokenType.Gt, '>', it.previousPos(), it.currentPos());
            case '{':
                return new Token(TokenType.LBrace,'{',it.previousPos(), it.currentPos());
            case '}':
                return new Token(TokenType.RBrace,'}',it.previousPos(), it.currentPos());
            case ',':
                return new Token(TokenType.Comma,',',it.previousPos(), it.currentPos());
            case ':':
                return new Token(TokenType.Colon,':',it.previousPos(), it.currentPos());
            case ';':
                return new Token(TokenType.Semicolon, ';', it.previousPos(), it.currentPos());
            case '(':
                return new Token(TokenType.LParen, '(', it.previousPos(), it.currentPos());
            case ')':
                return new Token(TokenType.RParen, ')', it.previousPos(), it.currentPos());
            default:
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }
}
