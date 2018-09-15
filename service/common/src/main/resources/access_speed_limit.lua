--限流的key
local key = KEYS[1]
--限流的大小
local limit = tonumber(ARGV[1])
--限流的时间
local seconds=tonumber(ARGV[2])
--当前流量大小 tonumber 转化为数字
local currentLimit = tonumber(redis.call('get',key) or "0")

if currentLimit + 1 > limit
    --达到限流大小 返回
    then return currentLimit + 1;
else
    --没有达到 currentLimit+1
    redis.call("INCRBY",key,1)
    redis.call("EXPIRE",key,seconds)
    return currentLimit + 1
end