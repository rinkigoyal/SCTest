package com.gabriele.sctest.cache;


public interface Cache<K, V> {
    V get(K key);
}