package miniplc0java.analyser;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.CompileError;
import miniplc0java.error.ErrorCode;
import miniplc0java.error.ExpectedTokenError;
import miniplc0java.error.TokenizeError;
import miniplc0java.instruction.Instruction;
import miniplc0java.instruction.Operation;
import miniplc0java.program.FunctionDef;
import miniplc0java.program.GlobalDef;
import miniplc0java.program.o0;
import miniplc0java.symboltable.SymbolEntry;
import miniplc0java.symboltable.SymbolTableUtil;
import miniplc0java.tokenizer.Token;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.tokenizer.Tokenizer;
import miniplc0java.util.Pos;

import java.util.*;

public final class Analyser {

    Tokenizer tokenizer;

    //函数列表
    o0 program;

    //保存全局复制指令，就是_start中的指令
    ArrayList<Instruction> globalInstructions;
    //用于存储函数指令
    ArrayList<Instruction> instructions;
    //符号表
    SymbolTableUtil symbolTable;

    /**
     * 当前偷看的 token
     */
    Token peekedToken = null;

    //当前函数是否返回，返回类型
    Boolean ifReturn = false;
    TokenType curReturnType;


    /**
     *运算符栈
     */
    private Stack<Token> operator = new Stack<>();

    private int checkOperator(Token token){
        if(token==null) return -1;
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
        if(operator.size()==0) {
            operator.push(pop);
            return null;
        }
        if(checkOperator(operator.peek())<6){
            Token peek = operator.peek();
            operator.push(pop);
            return peek;
        }
        return null;
    }

    private void pushToken(Token token) throws AnalyzeError {
        TokenType tokenType = token.getTokenType();
        int x=checkOperator(token);//栈外优先级
        switch (tokenType){
            case Semicolon:
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
        while(vtToken!=null&&x<=y&&y!=0){
            //进行一次运算
            analyseOperator();

            //更新栈内运算符
            vtToken = getVtToken();
            y = checkOperator(vtToken);
        }
        operator.push(token);
    }

    private void analyseOperator() throws AnalyzeError {
        Token y = operator.pop();
        Token op = operator.pop();
        if(op.getTokenType()==TokenType.Nege){
            if(y.getTokenType()==TokenType.Ident) symbolTable.getType(y.getValueString(),y.getStartPos());
            y.setStartPos(op.getStartPos());
            operator.push(y);
            instructions.add(new Instruction(Operation.NEGI));
            return;
        }

        Token x = operator.pop();
        if(op.getTokenType()==TokenType.As){
            if(x.getTokenType()==TokenType.Uint&&y.getTokenType()==TokenType.Double){
                instructions.add(new Instruction(Operation.ITOF));
            }else if(y.getTokenType()==TokenType.Uint&&x.getTokenType()==TokenType.Double){
                instructions.add(new Instruction(Operation.FTOI));
            }
            y.setStartPos(x.getStartPos());
            operator.push(y);
            return;
        }

        //判断类型
        TokenType type1,type2;
        if(x.getTokenType()==TokenType.Ident){
            type1 = symbolTable.getType(x.getValueString(),x.getStartPos());
        }else{
            type1 = x.getTokenType();
        }
        if(y.getTokenType()==TokenType.Ident){
            type2 = symbolTable.getType(y.getValueString(),y.getStartPos());
        }else{
            type2 = y.getTokenType();
        }
        if(op.getTokenType()!=TokenType.As&&type1!=type2) throw new AnalyzeError(ErrorCode.TypeMismatch,op.getStartPos());

        if(checkOperator(op)<=1){ //如果是布尔运算符
            y.setStartPos(x.getStartPos());
            y.setTokenType(TokenType.Bool);
            operator.push(y);
            if(type1==TokenType.Uint) instructions.add(new Instruction(Operation.CMPI));
            else if(type1==TokenType.Double) instructions.add(new Instruction(Operation.CMPF));
            switch (op.getTokenType()){
                case Eq:instructions.add(new Instruction(Operation.NOT));break;
                case Gt:instructions.add(new Instruction(Operation.SETGT));break;
                case Lt:instructions.add(new Instruction(Operation.SETLT));break;
                case GE:instructions.add(new Instruction(Operation.SETLT));
                        instructions.add(new Instruction(Operation.NOT));break;
                case LE:instructions.add(new Instruction(Operation.SETGT));
                        instructions.add(new Instruction(Operation.NOT));break;
            }
        }else{
            y.setStartPos(x.getStartPos());
            y.setTokenType(type1);
            operator.push(y);
            if(type1==TokenType.Uint){
                switch (op.getTokenType()){
                    case Plus:instructions.add(new Instruction(Operation.ADDI));break;
                    case Minus:instructions.add(new Instruction(Operation.SUBI));break;
                    case Mul:instructions.add(new Instruction(Operation.MULI));break;
                    case Div:instructions.add(new Instruction(Operation.DIVI));break;
                }
            }else if(type1==TokenType.Double){
                switch (op.getTokenType()){
                    case Plus:instructions.add(new Instruction(Operation.ADDF));break;
                    case Minus:instructions.add(new Instruction(Operation.SUBF));break;
                    case Mul:instructions.add(new Instruction(Operation.MULF));break;
                    case Div:instructions.add(new Instruction(Operation.DIVF));break;
                }
            }
        }
    }

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.instructions = new ArrayList<>();
        this.globalInstructions = new ArrayList<>();
        this.symbolTable = new SymbolTableUtil();
        program = new o0();
    }

    public o0 analyse() throws CompileError {
        analyseProgram();
        return program;
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
        Token peek = peek();
        while (check(TokenType.Let)||check(TokenType.Const)) {
            if(check(TokenType.Let)) analyseLetStatement();
            else analyseConstStatement();
        }

        globalInstructions = new ArrayList<>(instructions);
        FunctionDef startFun = new FunctionDef(-1, 0, 0, 0, globalInstructions);
        program.getFunctions().add(startFun);

        while (check(TokenType.Fn)) {
            analyseFunction();
        }
        expect(TokenType.EOF);

        //添加_start符号表
        symbolTable.addFunction("_start",TokenType.Void,new ArrayList<>(), peek().getStartPos());
        startFun.setName(symbolTable.getGlobalOffset("_start",peek.getStartPos()));
        SymbolEntry main = symbolTable.getSymbolEntry("main", peek.getStartPos());
        TokenType returnType = main.getReturnType();
        int return_slot = 0;
        if(returnType!=TokenType.Void) return_slot=1;
        globalInstructions.add(new Instruction(Operation.STACKALLOC,Long.valueOf(return_slot)));
        int offset = symbolTable.getGlobalOffset("main",peek.getStartPos());
        int funID = program.getFunctions().indexOf(new FunctionDef(offset));
        globalInstructions.add(new Instruction(Operation.CALL,Long.valueOf(funID)));
        if(return_slot==1) globalInstructions.add(new Instruction(Operation.POP));
        //根据全局符号表生成全局变量列表
        List<GlobalDef> globals = program.getGlobals();
        HashMap<String, SymbolEntry> rootMap = symbolTable.getRootMap();
        for (Map.Entry<String, SymbolEntry> entry:rootMap.entrySet()){
            globals.add(new GlobalDef(entry.getValue(),entry.getKey()));
        }
        Collections.sort(globals);

    }

    private void analyseFunction() throws CompileError {
        ifReturn = false;
        //进入新的作用域
        symbolTable.createTable(false);

        expect(TokenType.Fn);
        Token funName = expect(TokenType.Ident);
        expect(TokenType.LParen);
        List<TokenType> params = new ArrayList<>();
        analyseFunctionParmList(params);

        expect(TokenType.RParen);
        expect(TokenType.Arrow);
        TokenType returnType = analyseType();
        curReturnType = returnType;
        //添加符号表
        symbolTable.addFunction(funName.getValueString(),returnType,params,funName.getStartPos());

        //添加函数列表
        instructions = new ArrayList<>();
        String nameValueString = funName.getValueString();
        int name = symbolTable.getGlobalOffset(nameValueString,funName.getStartPos());
        int return_slots = (funName.getTokenType()==TokenType.Void)?0:1;
        int param_slots = params.size();
        int loc_slots = symbolTable.getCurrentSize()-param_slots;
        program.getFunctions().add(new FunctionDef(name,return_slots,param_slots,loc_slots,instructions));

        analyseBlockStatement();
        //退出作用域
        symbolTable.deleteCurrentTable();
        if(!ifReturn&&curReturnType!=TokenType.Void){
            throw new AnalyzeError(ErrorCode.NotAllRoutesReturn,funName.getStartPos());
        }
    }

    private void analyseFunctionParmList(List<TokenType> params) throws CompileError {
        TokenType paramType = TokenType.Void;
        int index = 0;
        if(check(TokenType.Ident)){
            paramType = analyseFunctionParm(index++);
            params.add(paramType);
            while (nextIf(TokenType.Comma) != null) {
                paramType = analyseFunctionParm(index++);
                params.add(paramType);
            }
        }
    }

    private TokenType analyseFunctionParm(int index) throws CompileError {
        boolean isConst = (nextIf(TokenType.Const)!=null);
        Token ident = expect(TokenType.Ident);
        expect(TokenType.Colon);
        TokenType parmType = analyseType();
        symbolTable.addParam(ident.getValueString(),parmType,isConst,ident.getStartPos(),index+1);
        return parmType;
    }

    private void analyseStatement() throws CompileError {
        TokenType returnType = null;
        TokenType peek = peek().getTokenType();
        switch (peek) {
            case If -> analyseIfStatement();
            case While -> analyseWhileStatement();
            case Return ->  analyseReturnStatement();
            case LBrace ->  analyseBlockStatement();
            case Semicolon -> analyseEmptyStatement();
            case Let -> analyseLetStatement();
            case Const -> analyseConstStatement();
            default -> analyseExpressionStatement();
        }
    }

    //表达式语句
    private void analyseExpressionStatement() throws CompileError {
        TokenType tokenType = analyseExpression();
        expect(TokenType.Semicolon);
        if(tokenType!=TokenType.Void){
            instructions.add(new Instruction(Operation.POP));
        }
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
            if(type.equals("void")){
                return TokenType.Void;
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
            getVariableAddr(ident);
            analyseExpression();
            symbolTable.declareSymbol(ident.getValueString(),peek().getStartPos());
            instructions.add(new Instruction(Operation.STORE64));
        }
        expect(TokenType.Semicolon);
    }

    //const语句
    private void analyseConstStatement() throws CompileError {
        expect(TokenType.Const);
        Token ident = expect(TokenType.Ident);
        expect(TokenType.Colon);

        getVariableAddr(ident);

        TokenType type = analyseType();
        expect(TokenType.Assign);
        analyseExpression();
        symbolTable.addSymbol(ident.getValueString(),type,true,true,ident.getStartPos());

        instructions.add(new Instruction(Operation.STORE64));

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
        instructions.add(new Instruction(Operation.BRTRUE,1L));
        Instruction ifBlockLength = new Instruction(Operation.BR, 0L);
        instructions.add(ifBlockLength);
        int size1 = instructions.size();
        analyseBlockStatement();
        Boolean ifBlockReturn = false;
        if(ifReturn){//if分支可以返回
            ifReturn = false;
            ifBlockReturn = true;
        }
        ifBlockLength.setX(Long.valueOf(instructions.size()-size1+1));
        if (check(TokenType.Else)) {
            expect(TokenType.Else);
            if (check(TokenType.If)) {
                analyseIfStatement();
            } else {
                Instruction elseBlockLength = new Instruction(Operation.BR, 0L);
                instructions.add(ifBlockLength);
                int size = instructions.size();
                analyseBlockStatement();
                ifBlockLength.setX(Long.valueOf(instructions.size()-size+1));
                instructions.add(new Instruction(Operation.BR,0L));
            }
        }else{
            ifReturn = true;
        }
        if(ifBlockReturn&&ifReturn){
        }else{
            ifReturn = false;
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
        instructions.add(new Instruction(Operation.BR,0L));
        int size1 = instructions.size();
        expect(TokenType.While);
        analyseExpression();
        instructions.add(new Instruction(Operation.BRTRUE,1L));
        Instruction whileBlock = new Instruction(Operation.BR, 0L);
        instructions.add(whileBlock);
        int size2 = instructions.size();
        analyseBlockStatement();
        int size3 = instructions.size();
        instructions.add(new Instruction(Operation.BR,Long.valueOf(size1-size3-1)));
        whileBlock.setX(Long.valueOf(size3-size2+1));
    }

    //return语句
    private void analyseReturnStatement() throws CompileError {
        expect(TokenType.Return);
        if (!check(TokenType.Semicolon)) {
            instructions.add(new Instruction(Operation.ARGA,0L));
            Token peek = peek();
            TokenType tokenType = analyseExpression();
            if(tokenType == TokenType.Ident) tokenType = TokenType.Uint;
            if(tokenType!=curReturnType){
                throw new AnalyzeError(ErrorCode.TypeMismatch,peek.getStartPos());
            }
            instructions.add(new Instruction(Operation.STORE64));
        }
        instructions.add(new Instruction(Operation.RET));
        expect(TokenType.Semicolon);
        ifReturn = true;
    }

    private void analyseIdentExpression() throws CompileError{
        Token ident = expect(TokenType.Ident);
        if (check(TokenType.Assign)) {//赋值语句
            Token assign = expect(TokenType.Assign);
            getVariableAddr(ident.getValueString(),ident.getStartPos());//将变量地址加载到栈顶
            TokenType type1 = analyseExpression();
            instructions.add(new Instruction(Operation.STORE64));

            if(symbolTable.getType(ident.getValueString(),ident.getStartPos())!=type1){
                throw new AnalyzeError(ErrorCode.TypeMismatch,assign.getStartPos());
            }
            ident.setTokenType(TokenType.Void);
        } else if (nextIf(TokenType.LParen) != null) {//函数调用
            //从符号表获取函数信息
            SymbolEntry symbolEntry = null;
            try{
                symbolTable.getSymbolEntry(ident.getValueString(), ident.getStartPos());
            }catch (AnalyzeError a){
                //没有这个自定义的函数，判断是否是标准库
                TokenType returnType = null;
                List<TokenType> params = null;
                switch (ident.getValueString()){
                    case "getint":returnType = TokenType.Uint;params = new ArrayList();break;
                    case "getdouble":returnType = TokenType.Double;params = new ArrayList();break;
                    case "getchar":returnType = TokenType.Uint;params = new ArrayList();break;
                    case "putint":returnType = TokenType.Void;params = new ArrayList<>(){{add(TokenType.Uint);}};break;
                    case "putdouble":returnType = TokenType.Void;params = new ArrayList<>(){{add(TokenType.Double);}};break;
                    case "putchar":returnType = TokenType.Void;params = new ArrayList<>(){{add(TokenType.Uint);}};break;
                    case "putstr":returnType = TokenType.Void;params = new ArrayList<>(){{add(TokenType.Uint);}};break;
                    case "putln":returnType = TokenType.Void;params = new ArrayList<>();break;
                }
                if(returnType!=null) {
                    symbolTable.addFunction(ident.getValueString(),returnType,params,ident.getStartPos());
                    symbolTable.getSymbolEntry(ident.getValueString(), ident.getStartPos()).setConstant(true);
                }
            }
            symbolEntry= symbolTable.getSymbolEntry(ident.getValueString(), ident.getStartPos());
            List params = symbolEntry.getParams();
            TokenType returnType = symbolEntry.getReturnType();

            int return_slot = 0;
            if(returnType!=TokenType.Void) return_slot=1;
            instructions.add(new Instruction(Operation.STACKALLOC,Long.valueOf(return_slot)));

            int index = 0;
            if(!check(TokenType.RParen)){
                TokenType eType = analyseExpression();
                if(index>=params.size()) throw new AnalyzeError(ErrorCode.FuncParamSizeMismatch,peek().getStartPos());
                if(params.get(index++)!=eType) throw new AnalyzeError(ErrorCode.TypeMismatch,peek().getStartPos());
            }
            while (nextIf(TokenType.Comma) != null) {
                TokenType eType = analyseExpression();
                if(index>=params.size()) throw new AnalyzeError(ErrorCode.FuncParamSizeMismatch,peek().getStartPos());
                if(params.get(index++)!=eType) throw new AnalyzeError(ErrorCode.TypeMismatch,peek().getStartPos());
            }
            if(index<params.size()) throw new AnalyzeError(ErrorCode.FuncParamSizeMismatch,peek().getStartPos());
            expect(TokenType.RParen);
            ident.setTokenType(returnType);
            int offset = symbolTable.getGlobalOffset(ident.getValueString(),ident.getStartPos());
            if(symbolEntry.isConstant()){//是标准库
                instructions.add(new Instruction(Operation.CALLNAME,Long.valueOf(offset)));
            }else{
                int funID = program.getFunctions().indexOf(new FunctionDef(offset));
                instructions.add(new Instruction(Operation.CALL,Long.valueOf(funID)));
            }
        }else{//标识符表达式
            getVariableAddr(ident);
            instructions.add(new Instruction(Operation.LOAD64));
        }

        pushToken(ident);
    }

    private void analyseOtherExpression() throws CompileError {
        if (check(TokenType.Ident)) {
            analyseIdentExpression();
        } else if (check(TokenType.Uint) || check(TokenType.Double) || check(TokenType.String)) {
            Token next = next();
            if(next.getTokenType()==TokenType.Uint) instructions.add(new Instruction(Operation.PUSH,(Long)next.getValue()));
            else if(next.getTokenType()==TokenType.Double) {
                instructions.add(new Instruction(Operation.PUSH,Double.doubleToRawLongBits((Double) next.getValue())));
            }else{
                int offset = symbolTable.addEmptyGlobalEntry();
                program.getGlobals().add(new GlobalDef(offset,next.getValueString()));
                instructions.add(new Instruction(Operation.PUSH,Long.valueOf(offset)));
                next.setTokenType(TokenType.Uint);
                next.setValue(offset);
            }
            pushToken(next);
        } else if (check(TokenType.LParen)) {
            Token LParen = expect(TokenType.LParen);
            operator.add(new Token(TokenType.Semicolon,peek()));
            TokenType tokenType = analyseExpression();
            operator.pop();
            pushToken(new Token(tokenType,LParen));
            expect(TokenType.RParen);
        }else {
            throw new AnalyzeError(ErrorCode.UnExpectedToken,peek().getStartPos());
        }
    }

    //表达式
    private TokenType analyseExpression() throws CompileError {
        if (!check(TokenType.Minus)) {
            analyseOtherExpression();
        } else {
            Token expect = expect(TokenType.Minus);
            expect.setTokenType(TokenType.Nege);
            pushToken(expect);
            Token peek = peek();
            TokenType tokenType = analyseExpression();
            pushToken(new Token(tokenType,peek));
        }
        while (checkOperator() || check(TokenType.As)) {
            if (checkOperator()) {
                pushToken(next());
                Token peek = peek();
                TokenType tokenType = analyseExpression();
                pushToken(new Token(tokenType,peek));
            } else {
                pushToken(expect(TokenType.As));
//                pushToken(expect(TokenType.Ident));
                Token peek = peek();
                pushToken(new Token(analyseType(),peek));
            }
        }
        //放入一个优先级为0的分号，结束运算再弹出
        pushToken(new Token(TokenType.Semicolon,peek()));
        operator.pop();
        return operator.pop().getTokenType();
    }

    //将变量地址加载到栈顶
    private void getVariableAddr(String name, Pos curPos) throws AnalyzeError {
        int offset = symbolTable.getOffset(name, curPos);
        if(offset<0){
            if(curReturnType==TokenType.Void) offset++;
            instructions.add(new Instruction(Operation.ARGA,Long.valueOf(-offset)));
            return;
        }
        if(symbolTable.isGlobal(name,curPos)){
            instructions.add(new Instruction(Operation.GLOBA,Long.valueOf(offset)));
        }else{
            instructions.add(new Instruction(Operation.LOCA,Long.valueOf(offset)));
        }
    }
    private void getVariableAddr(Token token) throws AnalyzeError {
        getVariableAddr(token.getValueString(),token.getStartPos());
    }
}