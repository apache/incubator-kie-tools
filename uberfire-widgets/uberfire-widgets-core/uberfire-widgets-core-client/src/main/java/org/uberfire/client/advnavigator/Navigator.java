package org.uberfire.client.advnavigator;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

public interface Navigator extends IsWidget {

    void loadContent( final Path path );

    boolean isAttached();

    public static interface NavigatorItem {

        public void addDirectory( final Path child );

        public void addFile( final Path child );
    }

}
