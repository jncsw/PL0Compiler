package gramma;
/*
指令类，保存了OP指令类型，以及l和m属性
 */
public class Instruction {
    public Op opt;
    public int l,m;

    public void setValue(Op opt,int l,int m){
        this.opt = opt;
        this.l = l;
        this.m = m;
    }

    public Instruction(){


    }


}
