package com.hd123.jyzh.flowable.quickstart;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 委派调用外部系统
 *
 * @author ZhengYu
 * @version 1.0 2021/8/17 15:54
 **/
public class CallExternalSystemDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        System.out.println("Calling the external system for employee "
                + delegateExecution.getVariable("employee"));
    }
}
