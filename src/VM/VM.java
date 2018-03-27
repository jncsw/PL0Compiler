package VM;

import Assist.CError;
import Assist.Factor;
import gramma.Block;
import gramma.Instruction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class VM {


/*
变量初始化
 */
    public static int SP = 0;//堆栈指针
    public static int BP = 1;//基址寄存器
    public static int PC = 0;//程序计数器
    public static int cx = 0;//code指针，来自语法分析
    public static int[] stack = new int[Factor.MAX_STACK_SIZE];//系统栈
    public static Instruction[] instructions;//保存指令
    public static Instruction inst;//保存正在运行的指令
    public static Instruction[] code;//来自语法分析，保存操作码
/*
处理OPR

OPR
NEG 1
ADD 2
SUB 3
MUL 4
DIV 5
ODD 6
EQL 8
NEQ 9
LSS 10
LEQ 11
GTR 12
GEQ 13
POW 14

 */
    public static void handleOPR(){
        switch (inst.m){
            case 0: //RET
                SP=BP-1;
                PC=stack[SP+4];
                BP=stack[SP+3];
                break;
            case 1: //NEG
                stack[SP]=-stack[SP];
                break;
            case 2: //ADD
                SP=SP-1;
                stack[SP]=stack[SP]+stack[SP+1];
                break;
            case 3: //SUB
                SP=SP-1;
                stack[SP]=stack[SP]-stack[SP+1];
                break;
            case 4: //MUL
                SP=SP-1;
                stack[SP]=stack[SP]*stack[SP+1];
                break;
            case 5: //DIV
                SP=SP-1;
                stack[SP]=stack[SP]/stack[SP+1];
                break;
            case 6: //ODD
                stack[SP]=stack[SP]%2;
                break;
            case 7: //MOD
                SP=SP-1;
                stack[SP]=stack[SP]%stack[SP+1];
                break;
            case 8: //EQL
                SP=SP-1;
                stack[SP]=(stack[SP]==stack[SP+1])?1:0;
                break;
            case 9: //NEQ
                SP=SP-1;
                stack[SP]=(stack[SP]!=stack[SP+1])?1:0;
                break;
            case 10: //LSS
                SP=SP-1;
                stack[SP]=(stack[SP]<stack[SP+1])?1:0;
                break;
            case 11: //LEQ
                SP=SP-1;
                stack[SP]=(stack[SP]<=stack[SP+1])?1:0;
                break;
            case 12: //GTR
                SP=SP-1;
                stack[SP]=(stack[SP]>stack[SP+1])?1:0;
                break;
            case 13: //GEQ
                SP=SP-1;
                stack[SP]=(stack[SP]>=stack[SP+1])?1:0;
                break;
            case 14: //POW
                SP=SP-1;
                stack[SP]=(int)Math.pow(stack[SP]*1.0,stack[SP+1]*1.0);
                break;
            default:
                CError.setInfo(inst.m+"");
                CError.HandleErr(21);

        }
    }


    /*

    基地址计算
    找到L层以下的stack基地址

     */
    public static int base() {
        int l = inst.l;
        int b1; //find base L levels down
        b1 = BP;
        while (l>0) {
            b1=stack[b1+1];
            l--;
        }
        return b1;
    }


/*
执行指令
 */

    public static void exec(){
        switch (inst.opt){
            case JMP://无条件跳转到M字段中给出的地址。
                PC = inst.m;
                break;
            case INT://开辟M个新的栈空间
                SP = SP + inst.m;
                break;
            case OPR://执行算术运算等
                handleOPR();
                break;
            case LIT://将立即数放到栈顶
                SP++;
                stack[SP] = inst.m;
                break;
            case LOD://将一个变量加载到栈顶
                SP++;
                stack[SP] = stack[base()+inst.m];
                break;
            case STO://将栈顶值存储到变量中去
                stack[base()+inst.m]=stack[SP];
                SP--;
                break;
            case CAL://调用一个程序。level给出层差，地址给出被调用程序中第一条指令的地址。
                /*
                保留返回地址
                 */
                stack[SP+1]=0;
                /*
                计算基地址，静态链
                 */
                stack[SP+2]=base();
                /*
                动态链
                 */
                stack[SP+3]=BP;
                /*
                返回地址
                 */
                stack[SP+4]=PC;
                BP=SP+1;
                PC=inst.m;
                break;
            case JPC://如果栈顶为0，跳转到地址字段给出的地址
                if(stack[SP]==0){
                    PC = inst.m;
                }
                SP--;
                break;
            case WRT://写到屏幕
                System.out.println("Write:"+stack[SP]);
                SP--;
                break;
            case RED://从标准输入读入
                SP++;
                System.out.println("Waiting For input:");
                Scanner in = new Scanner(System.in);
                stack[SP] = in.nextInt();
                break;
            default:
                CError.setInfo(inst.opt.name());
                CError.HandleErr(22);
        }
    }

    static FileWriter fw = null;
    public static void show(int SP,int BP) throws IOException {

        if(BP == 0)return;//BP=0,程序结束
        if(BP == 1){//在主程序，从1打印到SP
            for(int i=1;i<=SP;i++){
                fw.write(stack[i]+" ");
            }
            return;
        }else{
            /*
            递归调用每个‘页’
             */
            show(BP-1,stack[BP+2]);
            if(SP<BP){
                /*
                新一页，刚刚创建
                 */
                fw.write("# ");
                for(int i=0;i<4;i++){
                    fw.write(stack[BP+i]+" ");
                }
            }
            else{
                /*
                新一页，创建完成
                 */
                fw.write("# ");
                for(int i=BP;i<=SP;i++){
                    fw.write(stack[i]+" ");
                }
            }
            return;
        }
    }

    public VM() throws IOException {

        code = Block.getCode();
        cx = Block.getCx();

        /*
        将栈信息输出到文件
         */
        File f = new File("StackInfo.txt");

        f.createNewFile();
        fw =  new FileWriter(f);
        fw.write("PC"+"\t"+"OP"+"\t"+"l"+"\t"+"m"+"\n");
        for(int i=0;i<cx;i++){
            inst = code[i];
            fw.write(i+"\t"+inst.opt.name()+"\t"+inst.l+"\t"+inst.m+"\n");
        }
        fw.write("\n");
        fw.write("\n");


        System.out.println();
        System.out.println("Starting VM...");
        System.out.println();
        fw.write("\t\t\t\tPC\tBP\tSP\tStack\n");
        fw.write("\t\t\t\t"+PC+"\t"+BP+"\t"+SP+" \n");



        for(int i =0;i< Factor.MAX_STACK_SIZE;i++)stack[i] = 0;
        instructions = new Instruction[Factor.MAX_CODE_SIZE];
        for(int i =0;i< Factor.MAX_CODE_SIZE;i++)instructions[i] = new Instruction();

        while (BP!=0){

            /*
            取指令
             */
            inst = code[PC];
            fw.write(PC+"\t"+inst.opt.name()+"\t"+inst.l+"\t"+inst.m);
            PC++;

            /*
            执行
             */
            exec();
            fw.write("\t"+PC+" \t"+BP+"\t"+SP+"\t");
            /*
            展示结果
             */
            show(SP,BP);
            fw.write('\n');
        }
        fw.flush();
        fw.close();




    }

}
