package org.uberfire.java.nio.file;

import org.uberfire.commons.data.Pair;

public final class StandardWatchEventKind {

    private StandardWatchEventKind() {
    }

    public static final WatchEvent.Kind<Path> ENTRY_CREATE = new StdWatchEventKind<Path>( "ENTRY_CREATE", Path.class );

    public static final WatchEvent.Kind<Path> ENTRY_DELETE = new StdWatchEventKind<Path>( "ENTRY_DELETE", Path.class );

    public static final WatchEvent.Kind<Path> ENTRY_MODIFY = new StdWatchEventKind<Path>( "ENTRY_MODIFY", Path.class );

    public static final WatchEvent.Kind<Pair> ENTRY_RENAME = new StdWatchEventKind<Pair>( "ENTRY_RENAME", Pair.class );

    private static class StdWatchEventKind<T> implements WatchEvent.Kind<T> {

        private final String name;
        private final Class<T> type;

        StdWatchEventKind( String name,
                           Class<T> type ) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
