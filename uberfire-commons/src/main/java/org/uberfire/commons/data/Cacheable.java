package org.uberfire.commons.data;

public interface Cacheable {

    /**
     * Instructs any component that would like to cache the object
     * that in case it's already cached it should be refreshed inside the cache
     * @return
     */
    boolean requiresRefresh();

    /**
     * Marks the instance as cached. It should be done by the last cache in chain
     */
    void markAsCached();
}
