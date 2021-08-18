package com.hd123.jyzh.flowable.metadata;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;

/**
 * ProcessEngine 单例工厂
 *
 * @author ZhengYu
 * @version 1.0 2021/8/17 16:18
 **/
public class ProcessEngineFactory {
    public static void main(String[] args) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    }
}
