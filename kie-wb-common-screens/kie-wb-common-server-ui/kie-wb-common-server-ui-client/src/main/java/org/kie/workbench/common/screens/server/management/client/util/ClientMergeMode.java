package org.kie.workbench.common.screens.server.management.client.util;

import org.kie.workbench.common.screens.server.management.model.MergeMode;

/**
 * TODO: update me
 */
public enum ClientMergeMode {
    KEEP_ALL( "Keep All", MergeMode.KEEP_ALL ),
    OVERRIDE_ALL( "Override All", MergeMode.OVERRIDE_ALL ),
    OVERRIDE_EMPTY( "Override Empty", MergeMode.OVERRIDE_EMPTY ),
    MERGE_COLLECTIONS( "Merge Collections", MergeMode.MERGE_COLLECTIONS );

    private final String value;
    private final MergeMode mergeMode;

    ClientMergeMode( final String value,
                     final MergeMode mergeMode ) {
        this.value = value;
        this.mergeMode = mergeMode;
    }

    @Override
    public String toString() {
        return value;
    }

    public MergeMode getMergeMode() {
        return mergeMode;
    }

    public static ClientMergeMode convert( final MergeMode mergeMode ) {
        switch ( mergeMode ) {
            case KEEP_ALL:
                return ClientMergeMode.KEEP_ALL;
            case OVERRIDE_ALL:
                return ClientMergeMode.OVERRIDE_ALL;
            case OVERRIDE_EMPTY:
                return ClientMergeMode.OVERRIDE_EMPTY;
            case MERGE_COLLECTIONS:
                return ClientMergeMode.MERGE_COLLECTIONS;
        }
        throw new RuntimeException( "Invalid parameter" );
    }

    public static ClientMergeMode convert( final String mergeMode ) {
        for ( ClientMergeMode clientMergeMode : ClientMergeMode.values() ) {
            if ( mergeMode.equals( clientMergeMode.toString() ) ) {
                return clientMergeMode;
            }
        }

        return ClientMergeMode.KEEP_ALL;
    }

}
