package com.gabriele.sctest.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.gabriele.sctest.cache.CacheStatus.ERROR;

@Slf4j
public class GabCache<K, V> implements Cache<K, V> {

    private final Function<K, V> function;

    private final Map<K, GabCacheValue<V>> cache = new ConcurrentHashMap<>();

    /**
     * This implementation allows to retrieve values from a cache. Currently there is no implementation to clean the cache,
     * but if this is a requirement, multiple approaches could be used. We could add lastAccessed to GabCacheValue and clean up
     * based on the time stored on it, or we could use a SoftReference to clean up values when heap is full.
     * I decided to return null in case of error in the function calculation because (based on business requirements) the function
     * could actually be written to return an Optional and use None (for example) to identify the absence of an output value for the given input.
     *
     * In our team we use the Spring Boot cache wrapper with caffeine cache. This cache also handles the case of calculating the value
     * concurrently for same key (ie. the function would be called only once).
     *
     * @param key The key to find in the cache. Currently null values are not supported and we throw an exception if null passed as argument.
     * @return The value for the passed key or null if the computation of the function resulted in an error.
     **/
    @Override
    public V get(K key) {
        log.info("Trying to retrieve value for key {}", key);
        if (key == null) {
            throw new IllegalArgumentException("Null values not allowed for keys");
        }
        final GabCacheValue<V> pending = new GabCacheValue<V>();
        final GabCacheValue<V> ret = cache.putIfAbsent(key, pending);
        if (ret == null) { //Value for the given key not in cache will compute it
            return computeAndGet(key, pending);
        }
        switch (ret.getCacheStatus()) {
            case DONE:
                log.info("Value found and status DONE, returning the value");
                return ret.getValue();
            case IN_PROGRESS:
                log.info("Value found and status IN_PROGRESS, will wait until done");
                return waitAndGet(ret);
            case ERROR:
                log.info("Value found and status ERROR, will try to compute it again");
                return handleErrorAndGet(key);
            default:
                return null;
        }
    }

    private V waitAndGet(final GabCacheValue<V> ret) {
        try {
            synchronized (ret) {
                while (ret.getCacheStatus() == CacheStatus.IN_PROGRESS) {
                    ret.wait();
                }
                return ret.getValue();
            }
        } catch (InterruptedException e) {
            log.warn("There was an InterruptedException returning null");
            return null;
        }
    }

    private V handleErrorAndGet(final K key) {
        log.info("Trying to handle error for {}", key);
        final GabCacheValue<V> pending = new GabCacheValue<V>();
        boolean stillError = false;
        synchronized (cache) {
            final GabCacheValue<V> ret = cache.get(key);
            if (ret != null && ERROR.equals(ret.getCacheStatus())) {
                stillError = true;
                cache.put(key, pending);
            }
        }
        if (stillError) {
            log.info("Value for {} still in error state will set to PENDING and re-compute", key);
            return computeAndGet(key, pending);
        } else {
            return get(key);
        }
    }

    private V computeAndGet(final K key, final GabCacheValue<V> pending) {
        log.info("Computing value for {}", key);
        synchronized (pending) {
            try {
                final V value = function.apply(key);
                pending.setValue(value);
                pending.setCacheStatus(CacheStatus.DONE);
                return value;
            } catch (Exception ex) {
                log.warn("There was an exception computing function for value {}. Exception: {}", key, ex.getClass().getName());
                pending.setValue(null);
                pending.setCacheStatus(ERROR);
                return null;
            } finally {
                pending.notify();
            }
        }
    }

    public GabCache(Function<K, V> function) {
        this.function = function;
    }
}
