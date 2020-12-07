package miniplc0java.symboltable;

import miniplc0java.error.AnalyzeError;
import miniplc0java.error.ErrorCode;
import miniplc0java.tokenizer.TokenType;
import miniplc0java.util.Pos;

import java.util.HashMap;
import java.util.List;

public class SymbolTableUtil {
    private SymbolTable currentTable;
    private SymbolTable rootTable;

    public HashMap<String, SymbolEntry> getRootMap() {
        return rootTable.getMap();
    }

    public int addEmptyGlobalEntry() {
        return rootTable.getNextVariableOffset();
    }

    public SymbolEntry getSymbolEntry(String name, Pos curPos) throws AnalyzeError {
        SymbolTable temp = currentTable;
        while (temp != null) {
            SymbolEntry entry = temp.getMap().get(name);
            if (entry != null) return entry;
            temp = temp.getParent();
        }
        throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
    }

    public boolean isGlobal(String name, Pos curPos) throws AnalyzeError {
        SymbolTable temp = currentTable;
        while (temp != null) {
            SymbolEntry entry = temp.getMap().get(name);
            if (entry != null) {
                if (temp.getParent() == null) return true;
                return false;
            }
            temp = temp.getParent();
        }
        throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
    }

    //递归获取变量偏移
    public int getOffset(String name, Pos curPos) throws AnalyzeError {
        return this.getSymbolEntry(name, curPos).getStackOffset();
    }

    public TokenType getType(String name, Pos curPos) throws AnalyzeError {
        return this.getSymbolEntry(name, curPos).getType();
    }

    public int getCurrentSize() {
        return currentTable.getMap().size();
    }

    public int getMaxSize(){
        int maxSize = currentTable.getMaxSize();
        int size = currentTable.getMap().size();
        if(size>maxSize) return size;
        return maxSize;
    }

    public SymbolTableUtil() {
        currentTable = new SymbolTable(null);
        rootTable = currentTable;
    }

    public void addSymbol(String name, TokenType type, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        currentTable.addSymbol(name, type, isInitialized, isConstant, curPos);
    }

    public void addGlobalSymbol(String name, TokenType type, boolean isInitialized, boolean isConstant, Pos curPos) throws AnalyzeError {
        rootTable.addSymbol(name, type, isInitialized, isConstant, curPos);
    }

    public void addParam(String name, TokenType type, boolean isConstant, Pos curPos, int offset) throws AnalyzeError {
        currentTable.addParam(name, type, isConstant, curPos, -offset);
    }

    public void addFunction(String name, TokenType returnType, List<TokenType> params, Pos curPos) throws AnalyzeError {
        rootTable.addFunction(name, params, returnType, curPos);
    }

    public void declareSymbol(String name, Pos curPos) throws AnalyzeError {
        currentTable.declareSymbol(name, curPos);
    }

    public int getGlobalOffset(String name, Pos curPos) throws AnalyzeError {
        return rootTable.getOffset(name, curPos);
    }

    public boolean isConstant(String name, Pos curPos) throws AnalyzeError {
        SymbolEntry symbolEntry = this.getSymbolEntry(name, curPos);
        return symbolEntry.getConst();
    }

    public void deleteCurrentTable() {
        int size = currentTable.getParent().getMap().size()+currentTable.getMaxSize();
        currentTable = currentTable.getParent();
        if(size > currentTable.getMaxSize()) currentTable.setMaxSize(size);
    }

    public void createTable(boolean ifBlock) {
        if (ifBlock) {
            currentTable = new SymbolTable(currentTable, currentTable.getNextOffset());
        } else {
            currentTable = new SymbolTable(currentTable);
        }
    }
}

class SymbolTable {

    int maxSize=0;

    public int getMaxSize() {
        int size = this.getMap().size();
        if(maxSize>size)  return maxSize;
        return size;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

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

    public HashMap<String, SymbolEntry> getMap() {
        return symbolTable;
    }

    public SymbolEntry getSymbolEntry(String name, Pos curPos) throws AnalyzeError {
        var entry = this.symbolTable.get(name);
        if (entry == null) {
            throw new AnalyzeError(ErrorCode.NotDeclared, curPos);
        } else {
            return entry;
        }
    }


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

    public SymbolTable(SymbolTable parent, int nextOffset) {
        this.parent = parent;
        this.nextOffset = nextOffset;
    }

    /**
     * 获取下一个变量的栈偏移
     *
     * @return
     */
    public int getNextVariableOffset() {
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

    public void addFunction(String name, List<TokenType> params, TokenType returnType, Pos curPos) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.put(name, new SymbolEntry(TokenType.Fn, returnType, getNextVariableOffset(), params));
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

    public void addParam(String name, TokenType type, boolean isConstant, Pos curPos, int offset) throws AnalyzeError {
        if (this.symbolTable.get(name) != null) {
            throw new AnalyzeError(ErrorCode.DuplicateDeclaration, curPos);
        } else {
            this.symbolTable.put(name, new SymbolEntry(type, isConstant, true, offset));
        }
    }
}

