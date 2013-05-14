package org.kie.workbench.screens.explorer.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.screens.explorer.client.ExplorerPresenter;
import org.kie.workbench.screens.explorer.client.resources.images.ImageResources;
import org.uberfire.backend.vfs.Path;

/**
 * A widget representing a folder
 */
public class FolderWidget extends BaseItemWidget {

    public FolderWidget( final Path path,
                         final ExplorerPresenter presenter ) {
        super( path,
               presenter );
    }

    public FolderWidget( final Path path,
                         final String caption,
                         final ExplorerPresenter presenter ) {
        super( path,
               caption,
               presenter );
        anchor.addClickHandler( new ClickHandler() {

            @Override
            public void onClick( ClickEvent event ) {
                presenter.setContext( path );
            }
        } );
    }

    @Override
    public Image getImage() {
        return new Image( ImageResources.INSTANCE.folderIcon() );
    }

}
