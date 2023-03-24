package com.zcy.mmapexperiment.dao;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Tick {
    // 股票代码
    private String stkCode;

    // 当前时间戳
    private long time;

    // 昨日收盘价
    private float last = 14.1f;

    // 开盘价
    private float open= 14.1f;

    // 今日最高点
    private float high= 14.1f;

    // 今日最低点
    private float low= 14.1f;

    // 实时价格
    private float price= 14.1f;

    // 成交量
    private long volT = 41000;

    // 成交金额
    private float amtT = 578100.0f;

    // 成交量增量
    private long vol = 41000;

    private String id;

    private long outsideVol = 0;
    private long insideVol = 41000;
    private float amt = 578100.0f;
    private int num = 0;
    private int numT = 0;
    private float wb = -0.43f;
    private String bs = "B";

    private float buyAmt = 137370.3f;
    private float sellAmt = 440729.7f;
    private String phase = "T111";
    private int sn1 = 2500;
    private int sn2 = 13300;
    private int sn3 = 13300;
    private int sn4 = 15900;
    private int sn5 = 72300;
    private int bn1 = 29000;
    private int bn2 = 3100;
    private int bn3 = 2800;
    private int bn4 = 2000;
    private int bn5 = 9800;
    private float sp1 = 14.11f;
    private float sp2 = 14.12f;
    private float sp3 = 14.13f;
    private float sp4 = 14.14f;
    private float sp5 = 14.15f;
    private float bp1 = 14.1f;
    private float bp2 = 14.09f;
    private float bp3 = 14.08f;
    private float bp4 = 14.07f;
    private float bp5 = 14.06f;
}
