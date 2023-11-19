package com.khumakers.studystacker

import java.text.NumberFormat


fun Int.toTimestamp(): String{
    val x = this;
    val hh = x / 3600 % 60;
    val mm = x / 60 % 60;
    val ss = x % 60;
    var ans = ""

    if(hh<10){
        ans += "0";
    }
    ans += hh.toString();
    ans += ":";

    if(mm<10){
        ans += "0";
    }
    ans += mm.toString();
    ans += ":";

    if(ss<10){
        ans += "0";
    }
    ans += ss.toString();

    return ans;
}