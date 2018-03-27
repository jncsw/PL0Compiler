package lex;
/*
单词类型记录

 */
public enum SYM {
    num,
    plus,
    minus,
    mult,
    div,
    //     slash == div,////
    backslash,//\\\
    eq,neq,
    assign,
    less,
    leq,
    greater,
    geq,
    LeftParenthesis,
    RightParenthesis,
    comma,//,
    semicolon,//;
    period,//.

    begin,//
    end,
    ifSYM,
    thenSYM,
    elseSYM,
    whileSYM,
    doSYM,
    callSYM,
    constSYM,
    varSYM,
    proc,
    writeSYM,
    readSYM,
    odd,
    ident,
    unknow,
    nxtline,


    pow,
    octothorpe;//#
}
