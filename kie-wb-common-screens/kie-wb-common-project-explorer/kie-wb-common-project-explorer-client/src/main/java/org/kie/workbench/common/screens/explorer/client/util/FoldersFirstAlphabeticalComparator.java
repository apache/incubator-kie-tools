package org.kie.workbench.common.screens.explorer.client.util;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.uberfire.backend.vfs.Path;

import java.util.Comparator;

/**
 * A comparator to sort a list of items alphabetically by folder and then files.
 */
public class FoldersFirstAlphabeticalComparator implements Comparator<Item> {

    @Override
    public int compare( final Item o1,
                        final Item o2 ) {
        final int comparison = o1.getType().compareTo( o2.getType() );
        if ( comparison == 0 ) {
            return compareTo( o1,
                              o2 );
        }
        return comparison;
    }

    public int compareTo( final Item o1,
                          final Item o2 ) {
        return toLowerCase( o1.getPath() ).compareTo( toLowerCase( o2.getPath() ) );
    }

    private String toLowerCase( final Path path ) {
        return path == null ? "" : path.toURI().toLowerCase();
    }

}
