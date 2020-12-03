package miniplc0java.symboltable;

import miniplc0java.tokenizer.TokenType;

import java.util.List;

public class SymbolEntry {
    boolean isConstant = false;
    boolean isInitialized = false;
    int stackOffset;
    TokenType type;//数据真实类型，int，double
    TokenType returnType;

    List<TokenType> params;

    public List<TokenType> getParams() {
        return params;
    }

    public void setParams(List<TokenType> params) {
        this.params = params;
    }

    public TokenType getReturnType() {
        return returnType;
    }

    public void setReturnType(TokenType returnType) {
        this.returnType = returnType;
    }




    public SymbolEntry(TokenType type,TokenType returnType,int stackOffset,List<TokenType> params){
        this.stackOffset = stackOffset;
        this.type = type;
        this.returnType = returnType;
        this.params = params;
    }
    public SymbolEntry(TokenType type,boolean isConstant, boolean isDeclared, int stackOffset) {
        this.isConstant = isConstant;
        this.isInitialized = isDeclared;
        this.stackOffset = stackOffset;
        this.type = type;
    }

    /**
     * @return the stackOffset
     */
    public int getStackOffset() {
        return stackOffset;
    }

    /**
     * @return the isConstant
     */
    public boolean isConstant() {
        return isConstant;
    }

    /**
     * @return the isInitialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * @param isConstant the isConstant to set
     */
    public void setConstant(boolean isConstant) {
        this.isConstant = isConstant;
    }

    /**
     * @param isInitialized the isInitialized to set
     */
    public void setInitialized(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    /**
     * @param stackOffset the stackOffset to set
     */
    public void setStackOffset(int stackOffset) {
        this.stackOffset = stackOffset;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }
}
