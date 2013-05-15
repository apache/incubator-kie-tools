package org.kie.workbench.services.backend.cache;

/**
 * Define operations of a cache
 */
public interface Cache<K, V> {

    /**
     * Retrieve the cache entry for the specified key.
     * @param key The cache entry key
     * @return V The cache entry
     */
    V getEntry( final K key );

    /**
     * Set the cache entry for the specified path.
     * @param key The cache entry key
     * @param value The cache entry
     */
    void setEntry( final K key,
                   final V value );

    /**
     * Invalidate the entire cache
     */
    void invalidateCache();

    /**
     * Invalidate the cache for a specific key.
     * @param key The cache entry key
     */
    void invalidateCache( final K key );
}
