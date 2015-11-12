package org.uberfire.commons.lifecycle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class PriorityDisposableRegistry {

    private static Set<PriorityDisposable> disposables = new HashSet<PriorityDisposable>();

    private PriorityDisposableRegistry() {
    }

    public static void register( final PriorityDisposable priorityDisposable ) {
        disposables.add( priorityDisposable );
    }

    public static void clear() {
        disposables.clear();
    }

    public static Collection<PriorityDisposable> getDisposables() {
        return disposables;
    }
}
