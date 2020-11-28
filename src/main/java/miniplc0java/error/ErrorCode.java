package miniplc0java.error;

public enum ErrorCode {
    NoError, // Should be only used internally.
    InvalidDoubleValue,InvalidString,InvalidChar,
    StreamError, EOF, InvalidInput, InvalidIdentifier, IntegerOverflow, // int32_t overflow.
    UnExpectedToken, NeedIdentifier, ConstantNeedValue, NoSemicolon, InvalidVariableDeclaration, IncompleteExpression,
    NotDeclared, AssignToConstant, DuplicateDeclaration, NotInitialized, InvalidAssignment, InvalidPrint, ExpectedToken,
    InvalidType
}
