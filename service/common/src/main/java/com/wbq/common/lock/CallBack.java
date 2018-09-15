package com.wbq.common.lock;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public interface CallBack {
    /**
     * execute after success
     *
     * @return obj
     * @throws InterruptedException exception
     */
    Object onSuccess() throws InterruptedException;

    /**
     * execute after timeout
     *
     * @return obj
     * @throws InterruptedException exception
     */
    Object onTimeout() throws InterruptedException;
}
