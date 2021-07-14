package com.gabriele.sctest.cache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class GabCacheValue<V> {
    private V value;
    private CacheStatus cacheStatus = CacheStatus.IN_PROGRESS;
}
