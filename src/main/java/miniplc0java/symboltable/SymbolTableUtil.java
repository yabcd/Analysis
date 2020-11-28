package miniplc0java.symboltable;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.util.Pos;

import java.util.HashMap;

public class SymbolTableUtil {
    private SymbolTable currentTable = new SymbolTable(null);
    public void addSymbol(String name, TokenType type, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        currentTable.addSymbol(name,type,isInitialized,isConstant,curPos);
    }
    public void declareSymbol(String name, Pos curPos) throws AnalyzeError {
        currentTable.declareSymbol(name, curPos);
    }
    public int getOffset(String name, Pos curPos) throws AnalyzeError {
        return currentTable.getOffset(name,curPos);
    }
    public boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        return currentTable.isConstant(name, curPos);
    }
    public void deleteCurrentTable(){
        currentTable=currentTable.getParent();
    }
    public void createTable(boolean ifBlock){
        if(ifBlock){
            currentTable = new SymbolTable(currentTable,currentTable.getNextOffset());
        }else{
            currentTable = new SymbolTable(currentTable);
        }
    }
}
class SymbolTable {
    /**
     * 符号表
     */
    private SymbolTable parent = null;

    public SymbolTable getParent() {
        return parent;
    }

    public void setParent(SymbolTable parent) {
        this.parent = parent;
    }

    private HashMap<String, SymbolEntry> symbolTable = new HashMap<>();

    /**
     * 下一个变量的栈偏移
     */
    private int nextOffset = 0;

    public int getNextOffset() {
        return nextOffset;
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public SymbolTable(SymbolTable parent,int nextOffset) {
        this.parent = parent;
        this.nextOffset = nextOffset;
    }

    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    private int getNextVariableOffset() {
        return this.nextOffset++;
    }

    /**
     * 添加一个符号
     *
     * @param name          名字
     * @param isInitialized 是否已赋值
     * @param isConstant    是否是常量
     * @param curPos        当前 token 的位置（报错用）
     * @throws AnalyzeError 如果重复定义了则抛异常
     */
    public void addSymbol(String name, TokenType type, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.put(name, new SymbolEntry(type, isConstant, isInitialized, getNextVariableOffset()));
        }
    }

    /**
     * 设置符号为已赋值
     *
     * @param name   符号名称
     * @param curPos 当前位置（报错用）
     * @throws AnalyzeError 如果未定义则抛异常
     */
    public void declareSymbol(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            entry.setInitialized(true);
        }
    }

    /**
     * 获取变量在栈上的偏移
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 栈偏移
     * @throws AnalyzeError
     */
    public int getOffset(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.getStackOffset();
        }
    }

    /**
     * 获取变量是否是常量
     *
     * @param name   符号名
     * @param curPos 当前位置（报错用）
     * @return 是否为常量
     * @throws AnalyzeError
     */
    public boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry.isConstant();
        }
    }

}

