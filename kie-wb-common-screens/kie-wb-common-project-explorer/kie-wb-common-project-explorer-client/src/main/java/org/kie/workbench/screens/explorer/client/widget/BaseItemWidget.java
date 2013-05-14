package org.kie.workbench.screens.explorer.client.widget;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import org.kie.commons.validation.PortablePreconditions;
import org.kie.workbench.screens.explorer.client.ExplorerPresenter;
import org.kie.workbench.screens.explorer.client.resources.Resources;
import org.uberfire.backend.vfs.Path;

/**
 * Base Widget for all Items
 */
public abstract class BaseItemWidget extends Composite {

    protected final Anchor anchor = new Anchor();
    protected final HorizontalPanel container = new HorizontalPanel();

    protected final Path path;
    protected final String caption;
    protected final ExplorerPresenter presenter;

    public BaseItemWidget( final Path path,
                           final ExplorerPresenter presenter ) {
        this( path,
              path.getFileName(),
              presenter );
    }

    public BaseItemWidget( final Path path,
                           final String caption,
                           final ExplorerPresenter presenter ) {
        PortablePreconditions.checkNotNull( "caption",
                                            caption );
        PortablePreconditions.checkNotNull( "presenter",
                                            presenter );
        this.path = path;
        this.caption = caption;
        this.presenter = presenter;

        container.add( getImage() );
        container.add( anchor );
        container.setStyleName( Resources.INSTANCE.CSS().item() );
        anchor.setText( caption );
        initWidget( container );
    }

    public abstract Image getImage();

}
