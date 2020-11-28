package miniplc0java.tokenizer;

public enum TokenType {
    /** 空 */
    None,
    /** 无符号整数 */
    Uint,
    /** 标识符 */
    Ident,
    /** 字符串 */
    String,
    /** 浮点数 */
    Double,
    /** 字符 */
    Char,
    /** 函数 */
    Fn,
    /** 赋值 */
    Let,
    /** 常量 */
    Const,
    /** 类型转换 */
    As,
    /** while循环 */
    While,
    /** 判断 */
    If,
    Else,
    /** 返回 */
    Return,
    Break,
    Continue,
    //运算符
    Plus,//+
    Minus,//-
    Mul,//*
    Div,// /
    Assign,//=
    Eq,//==
    Neq,// !=
    Lt,// <
    Gt,// >
    LE,
    GE,
    LParen,
    RParen,
    LBrace,//{
    RBrace,//}
    Arrow, // ->
    Comma, // ,
    Colon, // :
    Semicolon, // ;
    Nege,//负号
    Void,
    Bool,
    /** 文件尾 */
    EOF;

    @Override
    public String toString() {
        switch (this) {
            case None:
                return "NullToken";
            case String:
                return "String";
            case Double:
                return "Double";
            case Char:
                return "Char";
            case Fn:
                return "Function";
            case Let:
                return "Let";
            case Const:
                return "Const";
            case As:
                return "As";
            case While:
                return "While";
            case If:
                return "If";
            case Else:
                return "Else";
            case Return:
                return "Return";
            case Break:
                return "Break";
            case Continue:
                return "Continue";
            case Uint:
                return "UnsignedInteger";
            case Ident:
                return "Ident";
            case Plus:
                return "Plus";
            case Minus:
                return "Minus";
            case Mul:
                return "Mul";
            case Div:
                return "Div";
            case Assign:
                return "Assign";
            case Eq:
                return "Equal";
            case Neq:
                return "notEqual";
            case Lt:
                return "LessThan";
            case Gt:
                return "GreaterThan";
            case LE:
                return "LessEqual";
            case GE:
                return "GreaterEqual";
            case LParen:
                return "Lparen";
            case RParen:
                return "RParen";
            case LBrace:
                return "LBrace";
            case RBrace:
                return "RBrace";
            case Arrow:
                return "Arrow";
            case Comma:
                return "Comma";
            case Colon:
                return "Colon";
            case Semicolon:
                return "Semicolon";
            case EOF:
                return "EOF";
            case Nege:
                return "Nege";
            case Void:
                return "Void";
            case Bool:
                return "Bool";
            default:
                return "InvalidToken";
        }
    }
}
