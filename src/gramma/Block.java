package gramma;

import Assist.CError;
import Assist.Factor;
import lex.GetSYM;
import lex.SYM;

import java.util.Iterator;
import java.util.Vector;

import static Assist.CError.HandleErr;
import static Assist.CError.setInfo;


public class Block {

    public static int[] dx = new int[Factor.MAX_BLOCK_SIZE];


    static int lev = 0,dxp = 0,tx = 0;

    static String id = "",num = "";
    static int lineNum = 0,kind = 0;
    public static Table[] symbolTable;
    public static Instruction[] code;
    public static Vector<SYM> lecSYM = GetSYM.getLecSYM();
    public static Vector<Integer> lecLine = GetSYM.getLecLines();
    public static Vector<String> lecInt = GetSYM.getLecInt();
    public static Vector<String> lecID = GetSYM.getLecID();
    public static Iterator<SYM> SYMIterator = lecSYM.iterator();
    public static Iterator<Integer> LineIterator = lecLine.iterator();
    public static Iterator<String> IDIterator = lecID.iterator();
    public static Iterator<String> IntIterator = lecInt.iterator();



    /*
    拿到下一个token，并将相应记录放入相应的变量
     */
    public static SYM getNextToken(){
        SYM token = null;
        if(SYMIterator.hasNext()){
            token = SYMIterator.next();
            lineNum = LineIterator.next();
            if(token == SYM.ident){
                id = IDIterator.next();

            }else if(token == SYM.num){
                num = IntIterator.next();
            }
            return token;
        }
        return null;

    }


    public static int cx;
    public static SYM token;

/*
发出一条指令
 */
    public static void emit(Op opt,int l,int m){
        if(cx > Factor.MAX_CODE_SIZE)HERROR(20);


        code[cx].setValue(opt,l,m);
        cx++;

    }
/*
将一个符号放入符号表
 */

    public static void enterTable(int k,int lev,int dxptr){
//        System.out.println("tx = "+tx);
        tx++;//符号表指针
        symbolTable[tx].name = id.toString();
        symbolTable[tx].kind = k;
        //const
        if(k==1){//记录数值
            symbolTable[tx].val = Integer.parseInt(num);
//            System.out.println("Got const : "+symbolTable[tx].val);
        }
        //Var
        else if(k==2){//记录L和M

            symbolTable[tx].level = lev;
            symbolTable[tx].adr = dx[dxptr];
//            System.out.println("Got VAR : "+symbolTable[tx].level+" "+symbolTable[tx].adr);
            dx[dxptr]++;
        }
        else{ //procedure
            symbolTable[tx].level = lev;
        }


    }

    /*

    处理倡廉定义
     */
    public static void HandleConst(int lev,int dxptr) {
        if(token == SYM.ident){
            token = getNextToken();
            if(token == SYM.eq || token == SYM.assign){
                if(token == SYM.assign){
                    HERROR(5);
                }
                token = getNextToken();
                if(token == SYM.num){
                    enterTable(1,lev,dxptr);
                    token = getNextToken();
                }
            }
        }

    }


    /*
    处理异常，同时提供行数的输出
     */
    public static void HERROR(int code){
        setInfo(""+lineNum);
        HandleErr(code);


    }

/*
处理变量定义Var
 */
    public static void HandleVar(int lev,int dxptr) {
        if(token == SYM.ident){
            enterTable(2,lev,dxptr); // var
            token = getNextToken();
        }else HERROR(7);

    }

    public static int diff,preDiff = 0;


    /*
    拿到变量在符号表中的位置
     */
    public static int getPosition(String id,int lev){
        int ptr = tx;
        int cur = 0;
        int diffCnt = 0;
        while (ptr!=0){
            if(symbolTable[ptr].name.equals(id)){
                /*
                如果变量在这一层无法访问
                 */
                if(symbolTable[ptr].level<=lev){
                    if(diffCnt !=0){
                        preDiff = diff;
                    }
                    diff = lev - symbolTable[ptr].level;
                    if(diffCnt==0){
                        cur = ptr;
                    }
                    if(diff<preDiff){
                        cur = ptr;
                    }
                    diffCnt++;
                }

            }
            ptr--;
        }
        return cur;

    }
/*
处理一个变量或者常数，附加对括号的处理
 */

    public static void BFactor(int lev){
        int i,level,adr,val;

        while (token == SYM.ident || token == SYM.num || token == SYM.LeftParenthesis){
            if(token == SYM.ident){
                i = getPosition(id,lev);
                if(i==0){
                    HERROR(11);

                }else {
                    kind = symbolTable[i].kind;
                    level = symbolTable[i].level;
                    adr = symbolTable[i].adr;
                    val = symbolTable[i].val;
                    if(kind == 1){ // const
                        emit(Op.LIT,0,val);
                    }else if(kind ==2){//Var
                        emit(Op.LOD,lev-level,adr);
                    }else HERROR(12);


                }
                token = getNextToken();

            }
            else if(token == SYM.num){
                emit(Op.LIT,0, Integer.parseInt(num));
                token = getNextToken();

            }else if(token == SYM.LeftParenthesis){
                token = getNextToken();
                Expression(lev);
                if(token == SYM.RightParenthesis){
                    token = getNextToken();
                }
                else HERROR(13);

            }
        }
    }
/*
处理乘除以及乘方
 */

    public static void Term(int lev){
        SYM mult;
        BFactor(lev);
        while (token == SYM.mult || token == SYM.div || token == SYM.pow){
            mult = token;
            token = getNextToken();
            BFactor(lev);
            if(mult == SYM.mult){
                emit(Op.OPR,0,4);
            }else if(mult==SYM.div) emit(Op.OPR,0,5);
            else emit(Op.OPR,0,14);
        }
    }

    /*
    处理一个表达式
     */
    public static void Expression(int lev){
        SYM addopt;
        if(token == SYM.plus || token == SYM.minus){
            addopt = token;
            token = getNextToken();
            Term(lev);
            if(addopt == SYM.minus){
                emit(Op.OPR,0,1);
            }

        }else{
            Term(lev);//乘除以及乘方
        }
        while (token == SYM.plus || token == SYM.minus){
            addopt = token;
            token = getNextToken();
            Term(lev);
            if(addopt == SYM.plus){
                emit(Op.OPR,0,2);
            }else emit(Op.OPR,0,3);

        }


    }

    /*
    处理条件语句，如 = # < <= > >=
     */
    public static void HandleCondition(int lev){
        SYM Relation;
        if(token == SYM.odd){
            token = getNextToken();
            Expression(lev);
            emit(Op.OPR,0,6);
        }else{
            Expression(lev);
            if(token != SYM.eq && token != SYM.neq && token!=SYM.less && token!=SYM.leq && token!=SYM.geq && token!=SYM.greater){
                HERROR(16);
            }else{
                Relation = token;
                token = getNextToken();
                Expression(lev);
                if (Relation == SYM.eq) emit(Op.OPR,0,8);
                if (Relation == SYM.neq) emit(Op.OPR,0,9);
                if (Relation == SYM.less) emit(Op.OPR,0,10);
                if (Relation == SYM.leq) emit(Op.OPR,0,11);
                if (Relation == SYM.greater) emit(Op.OPR,0,12);
                if (Relation == SYM.geq) emit(Op.OPR,0,13);
            }
        }
    }
/*
拿到一个左括号
 */
    private static void getLP(){
        if(token != SYM.LeftParenthesis)HERROR(23);
        token = getNextToken();

    }
/*
拿到一个右括号
 */
    private static void getRP(){
        if(token != SYM.RightParenthesis)HERROR(13);
        token = getNextToken();

    }

/*
处理一个语句
 */
    public static void Statement(int lev){
        int i,cx1,cx2;
        if(token == SYM.ident){//标识符
            i = getPosition(id,lev);
            if(i==0){
                HERROR(8);

            }else if(symbolTable[i].kind!=2){
                HERROR(9);
                i=0;
            }
            token = getNextToken();
            if(token == SYM.assign){
                token = getNextToken();
            }else{
                HERROR(10);
            }
            Expression(lev);
            if(i!=0){
                emit(Op.STO,lev-symbolTable[i].level,symbolTable[i].adr);
            }

            /*
            CALL 语句
             */

        }else if(token == SYM.callSYM){
            token = getNextToken();
            if(token !=SYM.ident){
                HERROR(14);
            }else{
                i = getPosition(id,lev);
                if(i==0)HERROR(11);
                else if(symbolTable[i].kind==3){
//                    System.out.println("CAL EMIT");
                    emit(Op.CAL,lev-symbolTable[i].level,symbolTable[i].adr);

                }else HERROR(15);
                token = getNextToken();

            }


            /*

            if-then-else语句
             */
        }else if(token == SYM.ifSYM){
            token = getNextToken();
            HandleCondition(lev);
            if(token == SYM.thenSYM){
                token = getNextToken();
            }else HERROR(17);
            cx1 = cx;
            emit(Op.JPC,0,0);
            Statement(lev);

            //处理 ELSE
            if(token == SYM.elseSYM){
                token = getNextToken();
                code[cx1].m = cx+1;
                cx1 = cx;
                emit(Op.JMP,0,0);
                Statement(lev);
            }
            code[cx1].m = cx;


        }else if(token == SYM.begin){
            token = getNextToken();
            Statement(lev);
            while (token == SYM.semicolon){
                token = getNextToken();
                Statement(lev);
            }
            if(token == SYM.end){
                token = getNextToken();

            }else HERROR(18);

            /*
            while-do语句
             */

        }else if(token == SYM.whileSYM){
            cx1 = cx;
            token = getNextToken();
            HandleCondition(lev);
            cx2 = cx;
            emit(Op.JPC,0,0);
            if(token == SYM.doSYM){
                token = getNextToken();
            }
            else HERROR(19);
            Statement(lev);
            emit(Op.JMP,0,cx1);
            code[cx2].m = cx;

            /*
            输入输出处理
             */
        }else if(token == SYM.writeSYM){
            token = getNextToken();
            getLP();
            Expression(lev);
            emit(Op.WRT,0,1);
            getRP();
        }else if(token == SYM.readSYM){
            token = getNextToken();
            getLP();
            emit(Op.RED,0,2);
            i = getPosition(id,lev);
            if(i==0){
                HERROR(11);
            }else if(symbolTable[i].kind !=2){
                HERROR(9);
                i=0;
            }
            if(i!=0){
                emit(Op.STO,lev-symbolTable[i].level,symbolTable[i].adr);
            }
            token = getNextToken();
            getRP();
        }
    }

    /*
    一个基本Block
     */
    public static void Block(int lev,int dxptr){
        /*
        超出最大的level
         */
        if(lev > Factor.MAX_LEVEL)HandleErr(3);
//        dx[dxptr] = 3;
        dx[dxptr] = 4; //将dx设置为4，在保存PC、SP、BP的基础上增加返回地址的记录。
        dxp++;
        int txx;
        txx = tx;
        symbolTable[tx].adr = cx;
        emit(Op.JMP,0,0);
        do{
            if(token == SYM.constSYM){
                token = getNextToken();
                do{
                    HandleConst(lev,dxptr);
                    while (token == SYM.comma){
                        token = getNextToken();
                        HandleConst(lev,dxptr);
                    }
                    if(token== SYM.semicolon){
                        token = getNextToken();
                    }else{
                        HERROR(6);
                    }

                }while (token==SYM.ident);


            }


            if(token == SYM.varSYM){

                token = getNextToken();
//                System.out.println("Got var : "+id);
                do{
                    HandleVar(lev,dxptr);
                    while (token == SYM.comma){
                        token = getNextToken();
                        HandleVar(lev,dxptr);

                    }
                    if(token == SYM.semicolon){
                        token = getNextToken();
                    }else HERROR(6);

                }while (token==SYM.ident);

            }
            //过程定义
            //拿到定义的标识符并将kind、level等等信息存入符号表
            while (token == SYM.proc){
                token = getNextToken();
                //地址之后回填
                if(token == SYM.ident){
                    enterTable(3,lev,dxptr);
                    token = getNextToken();
                }else{
                    HERROR(7);
                }
                if(token == SYM.semicolon){
                    token = getNextToken();
                }else HERROR(6);
                Block(lev+1,dxp);//过程定义，level加一

                if(token == SYM.semicolon){
                    token = getNextToken();
                }else HERROR(6);
            }
        }while (token == SYM.constSYM || token == SYM.varSYM || token == SYM.proc);

        //回填跳转地址
//        System.out.println("JMP addr = "+symbolTable[txx].adr);
        code[symbolTable[txx].adr].m = cx;
        symbolTable[txx].adr = cx;
        emit(Op.INT,0,dx[dxptr]);
//        System.out.println("dx = "+dx);
        Statement(lev);
        emit(Op.OPR, 0, 0);
    }

/*
语法分析主程序，将词法分析的符号表导入，并创建第一个基本块，最终将语法分析结果进行展示
 */
    public static void InitBlock(){
        int tmpval = Factor.MAX_TABLE_SIZE;
        symbolTable = new Table[tmpval];
        for(int i=0;i<tmpval;i++)symbolTable[i] = new Table();
        dxp = 0;
        tmpval = Factor.MAX_CODE_SIZE;
        code = new Instruction[tmpval];
        for(int i=0;i<tmpval;i++)code[i] = new Instruction();

        token = getNextToken();
        tx = 0;
        Block(0,dxp);
//        System.out.println("Finally");
        if(token != SYM.period){
            HERROR(4);
        }
        show();
        show2();

    }


    /*
    采用Markdown展示语法分析结果
     */

    public static void show(){
        System.out.println();
        System.out.println("The Grammar analysis is completed.");
        System.out.println();
        System.out.println();
        System.out.println("\t\t\t Code");
        System.out.println();
        System.out.println("|OP|L|M|");
        System.out.println("|-|-|-|");
        for(int i=0;i<cx;i++){
            System.out.print("|");
            System.out.print(code[i].opt.name());
            System.out.print("|");
            System.out.print(code[i].l);
            System.out.print("|");
            System.out.print(code[i].m);
            System.out.println("|");
        }

        System.out.println();
//        System.out.println("************** Finished **************");

    }
    public static void show2(){
        System.out.println();
        System.out.println("\t\t\tSymbol Table");
        System.out.println();

        System.out.println("|name|Kind|Val|level|addr|");
        System.out.println("|-|-|-|-|-|");
        for(int i=1;i<=tx;i++){
            System.out.print("|");
            System.out.print(symbolTable[i].name);
            System.out.print("|");
            System.out.print(symbolTable[i].kind);
            System.out.print("|");
            System.out.print(symbolTable[i].val);
            System.out.print("|");
            System.out.print(symbolTable[i].level);
            System.out.print("|");
            System.out.print(symbolTable[i].adr);
            System.out.println("|");

        }

        System.out.println();
        System.out.println("************** Finished **************");

    }

    /*

    获取类内变量
     */
    public static int getLev() {
        return lev;
    }

    public static void setLev(int lev) {
        Block.lev = lev;
    }


    public static int getTx() {
        return tx;
    }

    public static void setTx(int tx) {
        Block.tx = tx;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Block.id = id;
    }

    public static String getNum() {
        return num;
    }

    public static void setNum(String num) {
        Block.num = num;
    }

    public static int getLineNum() {
        return lineNum;
    }

    public static void setLineNum(int lineNum) {
        Block.lineNum = lineNum;
    }

    public static int getKind() {
        return kind;
    }

    public static void setKind(int kind) {
        Block.kind = kind;
    }

    public static Table[] getSymbolTable() {
        return symbolTable;
    }

    public static void setSymbolTable(Table[] symbolTable) {
        Block.symbolTable = symbolTable;
    }

    public static Instruction[] getCode() {
        return code;
    }

    public static void setCode(Instruction[] code) {
        Block.code = code;
    }

    public static Vector<SYM> getLecSYM() {
        return lecSYM;
    }

    public static void setLecSYM(Vector<SYM> lecSYM) {
        Block.lecSYM = lecSYM;
    }

    public static Vector<Integer> getLecLine() {
        return lecLine;
    }

    public static void setLecLine(Vector<Integer> lecLine) {
        Block.lecLine = lecLine;
    }

    public static Vector<String> getLecInt() {
        return lecInt;
    }

    public static void setLecInt(Vector<String> lecInt) {
        Block.lecInt = lecInt;
    }

    public static Vector<String> getLecID() {
        return lecID;
    }

    public static void setLecID(Vector<String> lecID) {
        Block.lecID = lecID;
    }

    public static Iterator<SYM> getSYMIterator() {
        return SYMIterator;
    }

    public static void setSYMIterator(Iterator<SYM> SYMIterator) {
        Block.SYMIterator = SYMIterator;
    }

    public static Iterator<Integer> getLineIterator() {
        return LineIterator;
    }

    public static void setLineIterator(Iterator<Integer> lineIterator) {
        LineIterator = lineIterator;
    }

    public static Iterator<String> getIDIterator() {
        return IDIterator;
    }

    public static void setIDIterator(Iterator<String> IDIterator) {
        Block.IDIterator = IDIterator;
    }

    public static Iterator<String> getIntIterator() {
        return IntIterator;
    }

    public static void setIntIterator(Iterator<String> intIterator) {
        IntIterator = intIterator;
    }

    public static int getCx() {
        return cx;
    }

    public static void setCx(int cx) {
        Block.cx = cx;
    }

    public static SYM getToken() {
        return token;
    }

    public static void setToken(SYM token) {
        Block.token = token;
    }

    public static int getDiff() {
        return diff;
    }

    public static void setDiff(int diff) {
        Block.diff = diff;
    }

    public static int getPreDiff() {
        return preDiff;
    }

    public static void setPreDiff(int preDiff) {
        Block.preDiff = preDiff;
    }
}
