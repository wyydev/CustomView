package cn.kinglian.www.customview;

import android.support.annotation.NonNull;

/**
 * 雷达view数据
 * Created by wen on 2018/5/23.
 */

public class RadarData {
    //属性
    private String name;
    //分数
    private double value;

    public RadarData(@NonNull String name, double value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
