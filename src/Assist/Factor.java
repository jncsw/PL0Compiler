package Assist;
/*
保存编译器参数

 */
public class Factor {

    public static int MAX_STACK_SIZE = 1000;
    public static int MAX_LEVEL = 3;
    public static int MAX_TABLE_SIZE = 1000;
    public static int MAX_CODE_SIZE = 1000;
    public static int MAX_BLOCK_SIZE = 100000;


    /*
    提供参数修改方法
     */

    public static int getMaxStackSize() {
        return MAX_STACK_SIZE;
    }

    public static void setMaxStackSize(int maxStackSize) {
        MAX_STACK_SIZE = maxStackSize;
    }

    public static int getMaxLevel() {
        return MAX_LEVEL;
    }

    public static void setMaxLevel(int maxLevel) {
        MAX_LEVEL = maxLevel;
    }

    public static int getMaxTableSize() {
        return MAX_TABLE_SIZE;
    }

    public static void setMaxTableSize(int maxTableSize) {
        MAX_TABLE_SIZE = maxTableSize;
    }

    public static int getMaxCodeSize() {
        return MAX_CODE_SIZE;
    }

    public static void setMaxCodeSize(int maxCodeSize) {
        MAX_CODE_SIZE = maxCodeSize;
    }

    public static int getMaxBlockSize() {
        return MAX_BLOCK_SIZE;
    }

    public static void setMaxBlockSize(int maxBlockSize) {
        MAX_BLOCK_SIZE = maxBlockSize;
    }
}
