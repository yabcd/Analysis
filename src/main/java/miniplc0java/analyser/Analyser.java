package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.symboltable.SymbolTableUtil;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;
    ArrayList<Instruction> instructions;
    //符号表
    SymbolTableUtil symbolTable;

    /**
     * 当前偷看的 token
     */
    Token peekedToken = null;


    /**
     *运算符栈
     */
    private Stack<Token> operator = new Stack<>();

    private int checkOperator(Token token){
        TokenType tokenType = token.getTokenType();
        switch (tokenType){
            case Semicolon:return 0;
            case Plus:
            case Minus:return 2;
            case Mul:
            case Div:return 3;
            case As:return 4;
            case Nege:return 5;
            case Gt:
            case Lt:
            case GE:
            case LE:
            case Eq:
            case Neq:return 1;
            default:return 6;
        }
    }

    //获取栈内第一个运算符的token
    private Token getVtToken(){
        if(operator.size()==0) return null;
        Token pop = operator.pop();
        if(checkOperator(pop)<6){
            operator.push(pop);
            return pop;
        }
        if(operator.size()==1) return null;
        if(checkOperator(operator.peek())<6){
            operator.push(pop);
            return operator.peek();
        }
        return null;
    }

    private void pushToken(Token token) throws AnalyzeError {
        TokenType tokenType = token.getTokenType();
        int x=checkOperator(token);//栈外优先级
        switch (tokenType){
            case Plus:
            case Minus:
            case Mul:
            case Div:
            case Nege:
            case As:
            case Gt:
            case Lt:
            case GE:
            case LE:
            case Eq:
            case Neq:break;
            default:operator.push(token);return;
        }
        Token vtToken = getVtToken();
//        //前面是一个bool表达式
//        if(checkOperator(vtToken)>4) throw new AnalyzeError(ErrorCode.UnExpectedToken,token.getStartPos());
        //栈内优先级
        int y = checkOperator(vtToken);
        while(x<y&&vtToken!=null){
            //进行一次运算
            Token newToken = new Token(vtToken);
            newToken.setTokenType(TokenType.Ident);
            newToken.setValue("id");
            if(y==5){
                operator.pop();
                operator.pop();
                operator.push(newToken);
            }
            operator.pop();
            operator.pop();
            operator.pop();
            operator.push(newToken);

            //更新栈内运算符
            vtToken = getVtToken();
            y = checkOperator(vtToken);
        }
        operator.push(token);
    }

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
        this.symbolTable = new SymbolTableUtil();
    }

    public List<Instruction> analyse() throws CompileError {
        analyseProgram();
        return instructions;
    }

    /**
     * 查看下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     *
     * @return
     * @throws TokenizeError
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            var token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     *
     * @param tt
     * @return
     * @throws TokenizeError
     * @return boolen
     */
    private boolean check(TokenType tt) throws TokenizeError {
        var token = peek();
        return token.getTokenType() == tt;
    }

    private boolean checkOperator() throws TokenizeError {
        var token = peek();
        switch (token.getTokenType()) {
            case Plus:
            case Minus:
            case Mul:
            case Div:
            case Eq:
            case Neq:
            case LE:
            case GE:
            case Lt:
            case Gt:
                return true;
            default:
                return false;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回这个 token
     * @param tt 类型
     * @throws TokenizeError
     * @return 如果匹配则返回这个 token，否则返回 null
     */
    private Token nextIf(TokenType tt) throws TokenizeError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            return null;
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     *
     * @param tt 类型
     * @return 这个 token
     * @throws CompileError 如果类型不匹配
     */
    private Token expect(TokenType tt) throws CompileError {
        var token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    private void analyseProgram() throws CompileError {
        while (check(TokenType.Let)||check(TokenType.Const)) {
            if(check(TokenType.Let)) analyseLetStatement();
            else analyseConstStatement();
        }
        while (check(TokenType.Fn)) {
            analyseFunction();
        }
        expect(TokenType.EOF);
    }

    private void analyseFunction() throws CompileError {
        expect(TokenType.Fn);
        expect(TokenType.Ident);
        expect(TokenType.LParen);
        analyseFunctionParmList();
        expect(TokenType.RParen);
        expect(TokenType.Arrow);
        analyseType();
        analyseBlockStatement();
    }

    private void analyseFunctionParmList() throws CompileError {
        analyseFunctionParm();
        while (nextIf(TokenType.Comma) != null) {
            analyseFunctionParm();
        }
    }

    private void analyseFunctionParm() throws CompileError {
        nextIf(TokenType.Const);
        expect(TokenType.Ident);
        expect(TokenType.Colon);
        analyseType();
    }


    private void analyseStatement() throws CompileError {
        TokenType peek = peek().getTokenType();
        switch (peek) {
            case If -> analyseIfStatement();
            case While -> analyseWhileStatement();
            case Return -> analyseReturnStatement();
            case LBrace -> analyseBlockStatement();
            case Semicolon -> analyseEmptyStatement();
            case Let -> analyseLetStatement();
            case Const -> analyseConstStatement();
            default -> analyseExpressionStatement();
        }
    }

    //表达式语句
    private void analyseExpressionStatement() throws CompileError {
        analyseExpression();
        expect(TokenType.Semicolon);
    }

    private TokenType analyseType() throws CompileError {
        Token token = nextIf(TokenType.Ident);
        if(token!=null) {
            String type = token.getValue().toString();
            if (type.equals("int")) {
                return TokenType.Uint;
            }
            if(type.equals("double")){
                return TokenType.Double;
            }
        }
        throw new AnalyzeError(ErrorCode.InvalidType, peek().getStartPos());
    }

    //let语句
    private void analyseLetStatement() throws CompileError {
        expect(TokenType.Let);
        Token ident = expect(TokenType.Ident);
        expect(TokenType.Colon);
        TokenType type= analyseType();
        symbolTable.addSymbol(ident.getValueString(),type,false,false,ident.getStartPos());
        if (nextIf(TokenType.Assign) != null) {
            analyseExpression();
            symbolTable.declareSymbol(ident.getValueString(),peek().getStartPos());
        }
        expect(TokenType.Semicolon);
    }

    //const语句
    private void analyseConstStatement() throws CompileError {
        expect(TokenType.Const);
        Token ident = expect(TokenType.Ident);
        expect(TokenType.Colon);
        TokenType type = analyseType();
        expect(TokenType.Assign);
        analyseExpression();
        symbolTable.addSymbol(ident.getValueString(),type,true,true,ident.getStartPos());
        expect(TokenType.Semicolon);
    }

    //空语句
    private void analyseEmptyStatement() throws CompileError {
        expect(TokenType.Semicolon);
    }

    //if语句
    private void analyseIfStatement() throws CompileError {
        expect(TokenType.If);
        analyseExpression();
        analyseBlockStatement();
        if (check(TokenType.Else)) {
            expect(TokenType.Else);
            if (check(TokenType.If)) {
                analyseIfStatement();
            } else {
                analyseBlockStatement();
            }
        }
    }

    //代码块
    private void analyseBlockStatement() throws CompileError {
        expect(TokenType.LBrace);
        while (!check(TokenType.RBrace)) {
            analyseStatement();
        }
        expect(TokenType.RBrace);
    }

    //while语句
    private void analyseWhileStatement() throws CompileError {
        expect(TokenType.While);
        analyseExpression();
        analyseBlockStatement();
    }

    //return语句
    private void analyseReturnStatement() throws CompileError {
        expect(TokenType.Return);
        if (!check(TokenType.Semicolon)) {
            analyseExpression();
        }
        expect(TokenType.Semicolon);
    }

    private void analyseOtherExpression() throws CompileError {
        if (check(TokenType.Ident)) {
            Token ident = expect(TokenType.Ident);
            if (nextIf(TokenType.Assign) != null) {
                analyseExpression();
            } else if (nextIf(TokenType.LParen) != null) {
                analyseExpression();
                while (nextIf(TokenType.Comma) != null) {
                    analyseExpression();
                }
            }
        } else if (check(TokenType.Uint) || check(TokenType.Double) || check(TokenType.String)) {
            next();
        } else if (check(TokenType.LParen)) {
            expect(TokenType.LParen);
            analyseExpression();
            expect(TokenType.RParen);
        }else {
            throw new AnalyzeError(ErrorCode.UnExpectedToken,peek().getStartPos());
        }

        Token token = new Token(peek());
        token.setTokenType(TokenType.Semicolon);
        token.setValue("id");
        pushToken(token);
    }

    //表达式
    private void analyseExpression() throws CompileError {
        if (!check(TokenType.Minus)) {
            analyseOtherExpression();
        } else {
            Token expect = expect(TokenType.Minus);
            expect.setTokenType(TokenType.Nege);
            pushToken(expect);
            analyseExpression();
        }
        while (checkOperator() || check(TokenType.As)) {
            if (checkOperator()) {
                pushToken(next());
                analyseExpression();
            } else {
                pushToken(expect(TokenType.As));
                pushToken(expect(TokenType.Ident));
            }
        }
        Token token = new Token(peek());
        token.setTokenType(TokenType.Semicolon);
        pushToken(token);
        operator.pop();
    }
}