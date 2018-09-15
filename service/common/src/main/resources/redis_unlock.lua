local key = KEY[1]

local value = ARGV[1]

local real_value = redis.call('get',key)

local res = 0

if value == real_value
    then res = redis.call('del',key)
    return res
end