package miniplc0java.instruction;

import miniplc0java.error.AnalyzeError;

public enum Operation {
    NOP, PUSH, POP, POPN, DUP, LOCA, ARGA, GLOBA, LOAD8, LOAD16,
    LOAD32, LOAD64, STORE8, STORE16, STORE32, STORE64, ALLOC,
    FREE, STACKALLOC, ADDI, SUBI, MULI, DIVI, ADDF, SUBF, MULF,
    DIVF, DIVU, SHR, AND, OR, XOR, NOT, CMPI, CMPF, CMPU, NEGI,
    NEGF, ITOF, FTOI, SHRL, SETLT, SETGT, BR, BRFALSE, BRTRUE,
    CALL, RET, CALLNAME, SCANI, SCANF, PRINTI, PRINTC, PRINTF,
    PRINTS, PRINTLN, PANIC;

    @Override
    public String toString() {
        switch (this) {
            case NOP:
                return "00";
            case PUSH:
                return "01";
            case POP:
                return "02";
            case POPN:
                return "03";
            case DUP:
                return "04:";
            case LOCA:
                return "0a";
            case ARGA:
                return "0b";
            case GLOBA:
                return "0c";
            case LOAD8:
                return "10";
            case LOAD16:
                return "11,";
            case LOAD32:
                return "12";
            case LOAD64:
                return "13";
            case STORE8:
                return "14";
            case STORE16:
                return "15";
            case STORE32:
                return "16";
            case STORE64:
                return "17";
            case ALLOC:
                return "18";
            case FREE:
                return "19";
            case STACKALLOC:
                return "1a";
            case ADDI:
                return "20";
            case SUBI:
                return "21";
            case MULI:
                return "22";
            case DIVI:
                return "23";
            case ADDF:
                return "24";
            case SUBF:
                return "25";
            case MULF:
                return "26";
            case DIVF:
                return "27";
            case DIVU:
                return "28";
            case SHR:
                return "2a";
            case AND:
                return "2b";
            case OR:
                return "2c";
            case XOR:
                return "2d";
            case NOT:
                return "2e";
            case CMPI:
                return "30";
            case CMPF:
                return "32";
            case CMPU:
                return "31";
            case NEGI:
                return "34,";
            case NEGF:
                return "35";
            case ITOF:
                return "36";
            case FTOI:
                return "37";
            case SHRL:
                return "38";
            case SETLT:
                return "39";
            case SETGT:
                return "3a";
            case BR:
                return "41";
            case BRFALSE:
                return "42";
            case BRTRUE:
                return "43";
            case CALL:
                return "48";
            case RET:
                return "49";
            case CALLNAME:
                return "4a";
            case SCANI:
                return "50";
            case SCANF:
                return "52";
            case PRINTI:
                return "54";
            case PRINTC:
                return "55";
            case PRINTF:
                return "56";
            case PRINTS:
                return "57";
            case PRINTLN:
                return "58";
            case PANIC:
                return "fe";
            default:
                return "InvalidOperation";
        }
    }

//    @Override
//    public String toString() {
//        switch (this) {
//            case NOP:
//                return "NOP";
//            case PUSH:
//                return "PUSH";
//            case POP:
//                return "POP";
//            case POPN:
//                return "POPN";
//            case DUP:
//                return "DUP:";
//            case LOCA:
//                return "LOCA";
//            case ARGA:
//                return "ARGA";
//            case GLOBA:
//                return "GLOBA";
//            case LOAD8:
//                return "LOAD8";
//            case LOAD16:
//                return "LOAD16,";
//            case LOAD32:
//                return "LOAD32";
//            case LOAD64:
//                return "LOAD64";
//            case STORE8:
//                return "STORE8";
//            case STORE16:
//                return "STORE16";
//            case STORE32:
//                return "STORE32";
//            case STORE64:
//                return "STORE64";
//            case ALLOC:
//                return "ALLOC";
//            case FREE:
//                return "FREE";
//            case STACKALLOC:
//                return "STACKALLOC";
//            case ADDI:
//                return "ADDI";
//            case SUBI:
//                return "SUBI";
//            case MULI:
//                return "MULI";
//            case DIVI:
//                return "DIVI";
//            case ADDF:
//                return "ADDF";
//            case SUBF:
//                return "SUBF";
//            case MULF:
//                return "MULF";
//            case DIVF:
//                return "DIVF";
//            case DIVU:
//                return "DIVU";
//            case SHR:
//                return "SHR";
//            case AND:
//                return "AND";
//            case OR:
//                return "OR";
//            case XOR:
//                return "XOR";
//            case NOT:
//                return "NOT";
//            case CMPI:
//                return "CMPI";
//            case CMPF:
//                return "CMPF";
//            case CMPU:
//                return "CMPU";
//            case NEGI:
//                return "NEGI,";
//            case NEGF:
//                return "NEGF";
//            case ITOF:
//                return "ITOF";
//            case FTOI:
//                return "FTOI";
//            case SHRL:
//                return "SHRL";
//            case SETLT:
//                return "SETLT";
//            case SETGT:
//                return "SETGT";
//            case BR:
//                return "BR";
//            case BRFALSE:
//                return "BRFALSE";
//            case BRTRUE:
//                return "BRTRUE";
//            case CALL:
//                return "CALL";
//            case RET:
//                return "RET";
//            case CALLNAME:
//                return "CALLNAME";
//            case SCANI:
//                return "SCANI";
//            case SCANF:
//                return "SCANF";
//            case PRINTI:
//                return "PRINTI";
//            case PRINTC:
//                return "PRINTC";
//            case PRINTF:
//                return "PRINTF,";
//            case PRINTS:
//                return "PRINTS";
//            case PRINTLN:
//                return "PRINTLN";
//            case PANIC:
//                return "PANIC";
//            default:
//                return "InvalidOperation";
//        }
//    }
}