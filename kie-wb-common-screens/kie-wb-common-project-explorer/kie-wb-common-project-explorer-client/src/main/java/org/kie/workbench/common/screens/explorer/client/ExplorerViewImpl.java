package org.kie.workbench.common.screens.explorer.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.workbench.common.screens.explorer.client.resources.Resources;
import org.kie.workbench.common.screens.explorer.client.util.FoldersFirstAlphabeticalComparator;
import org.kie.workbench.common.screens.explorer.client.widget.BreadCrumbsWidget;
import org.kie.workbench.common.screens.explorer.client.widget.FileWidget;
import org.kie.workbench.common.screens.explorer.client.widget.FolderWidget;
import org.kie.workbench.common.screens.explorer.client.widget.PackageWidget;
import org.kie.workbench.common.screens.explorer.client.widget.ProjectWidget;
import org.kie.workbench.common.screens.explorer.client.widget.RepositoryWidget;
import org.kie.workbench.common.screens.explorer.model.ExplorerContent;
import org.kie.workbench.common.screens.explorer.model.Item;

/**
 * The Explorer's view implementation
 */
public class ExplorerViewImpl extends Composite implements ExplorerView {

    private final FoldersFirstAlphabeticalComparator sorter = new FoldersFirstAlphabeticalComparator();

    private ExplorerPresenter presenter;

    private final VerticalPanel container = new VerticalPanel();
    private final VerticalPanel itemWidgetsContainer = new VerticalPanel();
    private final SimplePanel breadCrumbsWidgetContainer = new SimplePanel();
    private final BreadCrumbsWidget breadCrumbsWidget = new BreadCrumbsWidget();

    public ExplorerViewImpl() {
        container.setStyleName( Resources.INSTANCE.CSS().container() );
        breadCrumbsWidgetContainer.setStyleName( Resources.INSTANCE.CSS().breadCrumbsContainer() );

        breadCrumbsWidgetContainer.add( breadCrumbsWidget );
        container.add( breadCrumbsWidgetContainer );
        container.add( itemWidgetsContainer );

        initWidget( container );
    }

    @Override
    public void init( final ExplorerPresenter presenter ) {
        this.presenter = presenter;
        this.breadCrumbsWidget.setPresenter( presenter );
    }

    @Override
    public void setContent( final ExplorerContent content ) {

        //Bread Crumbs
        breadCrumbsWidget.setBreadCrumbs( content.getBreadCrumbs() );

        //Items - ExplorerContent returns an unmodifiable List
        final List<Item> items = new ArrayList<Item>( content.getItems() );
        Collections.sort( items,
                          sorter );
        itemWidgetsContainer.clear();

        for ( final Item item : items ) {
            IsWidget itemWidget = null;
            switch ( item.getType() ) {
                case PARENT_FOLDER:
                    itemWidget = new FolderWidget( item.getPath(),
                                                   item.getCaption(),
                                                   presenter );
                    break;
                case PARENT_PACKAGE:
                    itemWidget = new PackageWidget( item.getPath(),
                                                    item.getCaption(),
                                                    presenter );
                    break;
                case REPOSITORY:
                    itemWidget = new RepositoryWidget( item.getPath(),
                                                       item.getCaption(),
                                                       presenter );
                    break;
                case PROJECT:
                    itemWidget = new ProjectWidget( item.getPath(),
                                                    item.getCaption(),
                                                    presenter );
                    break;
                case PACKAGE:
                    itemWidget = new PackageWidget( item.getPath(),
                                                    item.getCaption(),
                                                    presenter );
                    break;
                case FOLDER:
                    itemWidget = new FolderWidget( item.getPath(),
                                                   item.getCaption(),
                                                   presenter );
                    break;
                case FILE:
                    itemWidget = new FileWidget( item.getPath(),
                                                 item.getCaption(),
                                                 presenter );
                    break;
            }
            if ( itemWidget != null ) {
                itemWidgetsContainer.add( itemWidget );
            }
        }

    }

}
