package com.hd123.jyzh.flowable;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * TODO
 *
 * @author ZhengYu
 * @version 1.0 2021/8/18 11:29
 **/
public class TestMain {
    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

    }
}
