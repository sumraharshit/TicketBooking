package com.booking.ticketBooking.util;

import org.springframework.data.redis.core.script.DefaultRedisScript;

public class Constant {
    public static final Long TIME_TO_LIVE = 10L;

    public static final DefaultRedisScript<Long> RELEASE_LOCK_LUCA_SCRIPT =
            new DefaultRedisScript<>(
                    """
          local current = redis.call('get',KEYS[1])
          if current == ARGV[1] then
              redis.call('del',KEYS[1])
              return 1
          end
          return 0
          """, Long.class
            );
}
