local key = KEY[1]
local value = ARGV[1]
--expire time
local expireTime =tonumber(ARGV[2])

local count = tonumber(redis.call('setnx',key,value) or "0")

if count == 1
    then redis.call('expire',expireTime)
    return 1
else
   return 0
end
