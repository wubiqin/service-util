package com.wbq.common.limiting;

/**
 *  *
 *  * @author biqin.wu
 *  * @since 15 九月 2018
 *  
 */
public class LimitRule {
    private int seconds;
    /**
     * limit count per seconds
     */
    private int limitCount;

    private LimitRule(Builder builder) {
        setSeconds(builder.seconds);
        setLimitCount(builder.limitCount);
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }


    public static final class Builder {
        private int seconds;
        private int limitCount;

        public Builder() {
        }

        public Builder seconds(int val) {
            seconds = val;
            return this;
        }

        public Builder limitCount(int val) {
            limitCount = val;
            return this;
        }

        public LimitRule build() {
            return new LimitRule(this);
        }
    }
}
