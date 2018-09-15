package com.wbq.common.lock;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public interface LockTemplate {

    /**
     * execute
     *
     * @param lockId   lock id (business unique)
     * @param timeout  unit mill seconds
     * @param callBack callback method
     * @return
     */
    Object execute(String lockId, long timeout, CallBack callBack);
}
