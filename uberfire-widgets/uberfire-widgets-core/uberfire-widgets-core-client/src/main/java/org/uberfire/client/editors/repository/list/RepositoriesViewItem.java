package org.uberfire.client.editors.repository.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.commons.validation.PortablePreconditions;
import org.uberfire.client.resources.i18n.CoreConstants;

/**
 * Created with IntelliJ IDEA.
 * Date: 24/09/13
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
public class RepositoriesViewItem extends Composite {

    interface RepositoriesViewItemBinder
            extends
            UiBinder<Widget, RepositoriesViewItem> {

    }

    private static RepositoriesViewItemBinder uiBinder = GWT.create( RepositoriesViewItemBinder.class );

    @UiField
    Element alias;

    @UiField
    Element uri;

    @UiField
    Element root;

    private Command cmdRemoveRepository;

    public RepositoriesViewItem( final String alias,
                                 final String uri,
                                 final String root,
                                 final Command cmdRemoveRepository ) {
        PortablePreconditions.checkNotNull( "alias",
                                            alias );
        PortablePreconditions.checkNotNull( "uri",
                                            uri );
        PortablePreconditions.checkNotNull( "root",
                                            root );
        this.cmdRemoveRepository = PortablePreconditions.checkNotNull( "cmdRemoveRepository",
                                                                       cmdRemoveRepository );

        initWidget( uiBinder.createAndBindUi( this ) );
        this.alias.setInnerText( alias );
        this.uri.setInnerText( CoreConstants.INSTANCE.RepositoryViewUriLabel() + " " + uri );
        this.root.setInnerText( CoreConstants.INSTANCE.RepositoryViewRootLabel() + " " + root );
    }

    @UiHandler("btnRemoveRepository")
    public void onClickButtonRemoveRepository( final ClickEvent event ) {
        if ( cmdRemoveRepository != null ) {
            cmdRemoveRepository.execute();
        }
    }

}
