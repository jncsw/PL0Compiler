package gramma;
/*
符号表记录
 */
public class Table {
    String name = ""; //记录name
    int kind = 0;  //const ： 1, var ： 2, procedure ： 3
    int val = 0;
    int level = 0;// 对应L
    int adr = 0;//对应M
/*
设置属性值
 */
    public void setTable(String name,int kind,int val,int level,int adr){
        this.name = name;
        this.kind = kind;
        this.val = val;
        this.level = level;
        this.adr = adr;

    }

    Table(){


    }
}
