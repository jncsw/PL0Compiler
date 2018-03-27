package Assist;

public class CError {
    static int errorCode;
    private static String info;
    private static String info2;
    /*
    设置异常信息
     */
    public static void setInfo(String information){
        info = information;
    }
    /*
    设置异常信息
     */
    public static void setInfo2(String information){

        info2 = information;
    }
/*
抛出异常，并解释异常信息
 */
    public static void HandleErr(int err){
        errorCode = err;
        System.out.println();//等待标准输出结束
        System.out.flush();
        System.out.println();//等待标准输出结束
        System.err.println();//等待标准输出结束
        switch(err){
            case 1:
                System.err.println("Source File ERROR");
                System.err.println("File not Found or cannot Read");
                break;
            case 2:
                System.err.println("Unknow Symbol at Line: "+info);
                System.err.println("Sybmol : \'"+info2+"\'");

                break;
            case 3:
                System.err.println("MAXIMUN LEVEL RANGE CHECK ERROR");
                break;
            case 4:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Period expected.");
                break;
            case 5:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println(" := used in const declaration.");
                break;
            case 6:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("'.' Expected ");
                break;
            case 7:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Var Const Procedure declaration ERROR. A identifier must be followed. ");
                break;

            case 8:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Identifier is not defined.");
                break;
            case 9:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Const Value Or Procedure can't be re-assigned");
                break;
            case 10:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Got '=' while Assign token ':=' is Expected.");
                break;

            case 11:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Undefined identifier");
                break;
            case 12:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Found Procedure in a expression");
                break;

            case 13:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("A ')' is Expected");
                break;


            case 14:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("A identifier must be followed in a CALL statement");
                break;

            case 15:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Cannot CALL a var or const");
                break;


            case 16:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Need a Relation Operator");
                break;

            case 17:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("THEN Expected After IF");
                break;
            case 18:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("  ';' Expected ");
                break;
            case 19:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println(" \"do\" Expected ");
                break;
            case 20:
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("Extended MAX CODE SIZE");
                break;
            case 21: // For Verify
                System.err.println("Unable to Handle OPR:"+info);
                System.err.println("Unknown OPR");
                break;
            case 22: // For Verify
                System.err.println("Unable to Handle EXEC:"+info);
                break;
            case 23: // For Verify
                System.err.println("Grammatical analysis error at Line "+info);
                System.err.println("A '(' is Expected");
                break;



            default:
                System.err.println("Unknown Error");
                break;
        }

        System.exit(err);

    }


    CError(){
        System.out.println("Init ERROR Handler");
        errorCode = 0;
    }



}
