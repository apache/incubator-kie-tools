package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Comparator;

import org.kie.workbench.common.screens.explorer.model.Item;
import org.kie.workbench.common.screens.explorer.model.Package;
import org.kie.workbench.common.screens.explorer.model.Project;
import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

/**
 * Sorters
 */
public class Sorters {

    public static Comparator<Group> GROUP_SORTER = new Comparator<Group>() {
        @Override
        public int compare( final Group o1,
                            final Group o2 ) {
            return o1.getName().compareTo( o2.getName() );
        }
    };

    public static Comparator<Repository> REPOSITORY_SORTER = new Comparator<Repository>() {
        @Override
        public int compare( final Repository o1,
                            final Repository o2 ) {
            return o1.getAlias().compareTo( o2.getAlias() );
        }
    };

    public static Comparator<Project> PROJECT_SORTER = new Comparator<Project>() {
        @Override
        public int compare( final Project o1,
                            final Project o2 ) {
            return o1.getTitle().compareTo( o2.getTitle() );
        }
    };

    public static Comparator<Package> PACKAGE_SORTER = new Comparator<Package>() {
        @Override
        public int compare( final Package o1,
                            final Package o2 ) {
            return o1.getCaption().compareTo( o2.getCaption() );
        }
    };

    public static Comparator<Item> ITEM_SORTER = new Comparator<Item>() {
        @Override
        public int compare( final Item o1,
                            final Item o2 ) {
            return o1.getFileName().compareTo( o2.getFileName() );
        }
    };

}
