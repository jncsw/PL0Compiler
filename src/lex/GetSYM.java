package lex;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;
import static Assist.CError.*;

public class GetSYM {



    public static String FileName;
    public static File Source;
    public static Vector<SYM> lecSYM = new Vector<>();
    public static Vector<Integer> lecLines = new Vector<>();
    public static Vector<String> lecInt = new Vector<>();
    public static Vector<String> lecID = new Vector<>();
    public static BufferedReader buffreader = null;


    public static char ch = '\0';
    public static String strToken = "";
    public static String line = "";
    public static int ptr = 0;
    public static int strlen = 0;
    public static int lineNum = 0;
    //获取下一个字符
    public static void GetChar(){
        if(ptr<strlen){
            ch = line.charAt(ptr);
        }else{
            ch = '\n';
        }
        ptr++;
    }
    //获取空格符、制表符和回车
    public static void GetBC(){
        while (ch==' '|| ch == '\t' ){
            if(ch == '\n')return;
            GetChar();
        }
    }
    //判断是否为字母
    public static boolean isLetter(){
        if(ch>='a' && ch<='z' || ch>='A' && ch<='Z')return  true;
        return false;
    }
    //判断是否为数字
    public static boolean isDigit(){
        if(ch>='0' && ch<='9' )return  true;
        return false;
    }
    //链接字符串
    public static void Concat(){
        strToken=strToken+ch;
    }
    //对符号进行处理
    public static SYM handleSymbol(){
        switch (ch){

            case '\n':
                return SYM.nxtline;

            case '+':
                return SYM.plus;
            case '-':
                return SYM.minus;
            case '*':
                GetChar();
                if(ch=='*'){
                    return SYM.pow;//乘方
                }
                else {
                    Retract();
                    return SYM.mult;
                }
            case '/':
                return SYM.div;
            case '\\':
                return SYM.backslash;
            case '=':
                    return SYM.eq;

            case ':':
                GetChar();
                if(ch=='=')
                    return SYM.assign;
                else {
                    Retract();
                    return SYM.unknow;
                }
            case '#':
                return SYM.neq;
            case '<':
                GetChar();
                if(ch=='=')
                    return SYM.leq;//小于等于
                else {
                    Retract();
                    return SYM.less;
                }
            case '>':
                GetChar();
                if(ch=='=')
                    return SYM.geq;
                else {
                    Retract();
                    return SYM.greater;
                }
            case '(':
                return SYM.LeftParenthesis;
            case ')':
                return SYM.RightParenthesis;
            case ',':
                return SYM.comma;
            case ';':
                return SYM.semicolon;
            case '.':
                return SYM.period;
         default:
             return SYM.unknow;
        }
    }
    //处理保留字
    public static SYM Reserve(){
        String Token = strToken.toLowerCase();
        switch (Token){
            case "begin":
                return SYM.begin;
            case "end":
                return SYM.end;
            case "if":
                return SYM.ifSYM;
            case "then":
                return SYM.thenSYM;
            case "else":
                return SYM.elseSYM;
            case "while":
                return SYM.whileSYM;
            case "do":
                return SYM.doSYM;
            case "call":
                return SYM.callSYM;
            case "const":
                return SYM.constSYM;
            case "var":
                return SYM.varSYM;
            case "procedure":
                return SYM.proc;
            case "write":
                return SYM.writeSYM;
            case "read":
                return SYM.readSYM;
            case "odd":
                return SYM.odd;
            default:
                return SYM.ident;
        }
    }

    //指针撤回并返回ch为空格
    public static void Retract(){
        ptr--;
        ch = ' ';
    }
    public static SYM type;

    //分析一行并进行处理
    public static void handleLine(){
        strToken = "";
        GetChar();
//        System.out.println("Got char:\'"+ch+"\'");
        if(ch=='\n')return;
        GetBC();
        boolean letter = isLetter();
        boolean digit = isDigit();
        while(isLetter()||isDigit())
        {
            Concat();
            GetChar();
        }
        if(letter){

            Retract();
            type = Reserve();
            lecSYM.add(type);
            lecLines.add(lineNum);
            if(type == SYM.ident){
                lecID.add(strToken);
            }
        }else if(digit){
            Retract();
            lecSYM.add(SYM.num);
            lecLines.add(lineNum);
            lecInt.add(strToken);
        }else{
            SYM tmp = handleSymbol();
            if(tmp == SYM.nxtline)return;
            if(tmp==SYM.unknow){
                setInfo(""+lineNum);
                setInfo2(""+ch);
                HandleErr(2);
            }else{
                lecSYM.add(tmp);
                lecLines.add(lineNum);
            }
        }

    }
    /*词法分析主程序
    负责打开文件，每次读取一行，并调用handleLine函数对该行进行词法分析。
     */
    public GetSYM(String FileName) throws IOException {
        lineNum = 0;
        lecInt.clear();
        lecSYM.clear();
        lecLines.clear();
        lecID.clear();

        this.FileName = FileName;
        Source = new File(FileName);
        if(!(Source.exists() && Source.canRead() && Source.isFile())){
            HandleErr(1);
        }
        buffreader = new BufferedReader(new FileReader(Source));
        line = buffreader.readLine();
        while (line!=null){
            lineNum++;
            ch = ' ';
            ptr = 0;
            strlen = line.length();
            if(strlen != 0){
//                System.out.println(line);
                while (ch!='\n'){
                    handleLine();
                }
            }
            line = buffreader.readLine();
        }
        show();


    }

    /*
    将词法分析的结果使用Markdown进行展示
     */
    public static void show(){
        System.out.println("The lexical analysis is completed.");
        System.out.println("Results are listed as follow:");
        System.out.println("Results can be viewed in Markdown Editor");
        Iterator<SYM> SYMIterator = lecSYM.iterator();
        Iterator<String> IDIterator = lecID.iterator();
        Iterator<String> IntIterator = lecInt.iterator();
        Iterator<Integer> LineIterator = lecLines.iterator();
        System.out.println();

        System.out.println("|SYM|ID|NUM|Line|");
        System.out.println("|-|-|-|-|");
        while (SYMIterator.hasNext()){
            System.out.print("|");
            SYM nxt = SYMIterator.next();
            System.out.print(nxt.name());

            System.out.print("|");
            if(nxt == SYM.ident){
                System.out.print(IDIterator.next());
            }else System.out.print("\t");
            System.out.print("|");
            if(nxt == SYM.num){
                System.out.print(IntIterator.next());
            }else System.out.print("\t");
            System.out.print("|");
            System.out.print(LineIterator.next());

            System.out.println("|");
        }

        System.out.println();
        System.out.println("************** Finished **************");

    }

/*
获取类内变量相关方法
 */
    public static String getFileName() {
        return FileName;
    }

    public static void setFileName(String fileName) {
        FileName = fileName;
    }

    public static File getSource() {
        return Source;
    }

    public static void setSource(File source) {
        Source = source;
    }

    public static Vector<SYM> getLecSYM() {
        return lecSYM;
    }

    public static void setLecSYM(Vector<SYM> lecSYM) {
        GetSYM.lecSYM = lecSYM;
    }

    public static Vector<String> getLecInt() {
        return lecInt;
    }

    public static void setLecInt(Vector<String> lecInt) {
        GetSYM.lecInt = lecInt;
    }

    public static Vector<String> getLecID() {
        return lecID;
    }

    public static void setLecID(Vector<String> lecID) {
        GetSYM.lecID = lecID;
    }

    public static BufferedReader getBuffreader() {
        return buffreader;
    }

    public static void setBuffreader(BufferedReader buffreader) {
        GetSYM.buffreader = buffreader;
    }

    public static char getCh() {
        return ch;
    }

    public static void setCh(char ch) {
        GetSYM.ch = ch;
    }

    public static String getStrToken() {
        return strToken;
    }

    public static void setStrToken(String strToken) {
        GetSYM.strToken = strToken;
    }

    public static String getLine() {
        return line;
    }

    public static void setLine(String line) {
        GetSYM.line = line;
    }

    public static int getPtr() {
        return ptr;
    }

    public static void setPtr(int ptr) {
        GetSYM.ptr = ptr;
    }

    public static int getStrlen() {
        return strlen;
    }

    public static void setStrlen(int strlen) {
        GetSYM.strlen = strlen;
    }

    public static int getLineNum() {
        return lineNum;
    }

    public static void setLineNum(int lineNum) {
        GetSYM.lineNum = lineNum;
    }

    public static SYM getType() {
        return type;
    }

    public static void setType(SYM type) {
        GetSYM.type = type;
    }

    public static Vector<Integer> getLecLines() {
        return lecLines;
    }

    public static void setLecLines(Vector<Integer> lecLines) {
        GetSYM.lecLines = lecLines;
    }
}
