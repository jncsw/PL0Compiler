import Assist.Factor;
import VM.VM;
import gramma.Block;
import lex.GetSYM;

import java.io.IOException;

public class Main {
/*
主程序，依次调用词法分析、语法分析、虚拟机运行

 */
    public static void main(String[] args) {

        //可选：修改编译器参数，比如栈大小等。
//        Factor.setMaxLevel(4);

        try {
            //词法分析
//            GetSYM lex = new GetSYM("testfiles\\test6.txt");
//            GetSYM lex = new GetSYM("testfiles\\NotAFile");
            GetSYM lex = new GetSYM("ERROR.txt");
//            GetSYM lex = new GetSYM("MAXIMUN_LEVEL.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //语法分析
        new Block().InitBlock();
        try {
            //虚拟机
            new VM();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
