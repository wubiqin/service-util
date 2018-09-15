local key = KEY[1]
--expire time
local expireTime =tonumber(ARGV[1])

local count = tonumber(redis.call('setnx',key) or "0")

if count == 1
    then redis.call('expire',expireTime)
    return 1
else
   return 0
end
