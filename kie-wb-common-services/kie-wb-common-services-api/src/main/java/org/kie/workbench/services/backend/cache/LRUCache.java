package org.kie.workbench.services.backend.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.kie.commons.validation.PortablePreconditions;

/**
 * A simple LRU cache keyed on Paths
 */
public abstract class LRUCache<Path, V> implements Cache<Path, V> {

    private static final int MAX_ENTRIES = 20;

    private Map<Path, V> cache;

    public LRUCache() {
        cache = new LinkedHashMap<Path, V>( MAX_ENTRIES + 1,
                                            0.75f,
                                            true ) {
            public boolean removeEldestEntry( Map.Entry eldest ) {
                return size() > MAX_ENTRIES;
            }
        };
        cache = (Map) Collections.synchronizedMap( cache );
    }

    @Override
    public V getEntry( final Path path ) {
        PortablePreconditions.checkNotNull( "path",
                                            path );
        return cache.get( path );
    }

    @Override
    public void setEntry( final Path path,
                          final V value ) {
        PortablePreconditions.checkNotNull( "path",
                                            path );
        PortablePreconditions.checkNotNull( "value",
                                            value );
        cache.put( path,
                   value );
    }

    @Override
    public void invalidateCache() {
        this.cache.clear();
    }

    @Override
    public void invalidateCache( final Path path ) {
        PortablePreconditions.checkNotNull( "path",
                                            path );
        this.cache.remove( path );
    }

    public Set<Path> getKeys() {
        return cache.keySet();
    }

}
