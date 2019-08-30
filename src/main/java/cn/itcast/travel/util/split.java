package cn.itcast.travel.util;

public class split {
    public static void main(String[] args) {
        String eq="2016,2013";
        String[]split=eq.split(",");
        System.out.println(split.length);
        if (split.length>10){
            System.out.println("zuer");
        }else {
            System.out.println("yaya");
        }
    }
}
