package com.bikerlifeYang;

public class Bikerlife_plan_imgshow extends Thread{

    private final Bilerlife_plan activity;
    private int what;

    public Bikerlife_plan_imgshow(Bilerlife_plan activity){
        this.activity=activity;
    }


    @Override
    public void run() {
        while (true){activity.point.sendEmptyMessage((what++)%4);

            try {
                Thread.sleep(1500);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
