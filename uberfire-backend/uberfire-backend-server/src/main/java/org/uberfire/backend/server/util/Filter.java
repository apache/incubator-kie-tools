package org.uberfire.backend.server.util;

public interface Filter<T> {

    boolean doFilter( final T t );

}
