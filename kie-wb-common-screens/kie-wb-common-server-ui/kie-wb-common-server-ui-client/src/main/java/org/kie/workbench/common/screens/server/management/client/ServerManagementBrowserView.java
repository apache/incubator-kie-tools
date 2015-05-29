package org.kie.workbench.common.screens.server.management.client;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.workbench.common.screens.server.management.client.box.BoxPresenter;
import org.kie.workbench.common.screens.server.management.client.header.HeaderPresenter;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

@Dependent
public class ServerManagementBrowserView extends Composite
        implements ServerManagementBrowserPresenter.View {

    private final FlowPanel panel = new FlowPanel();

    private HeaderPresenter header = null;

    @PostConstruct
    public void setup() {
        initWidget( panel );
        panel.getElement().getStyle().setProperty( "minWidth", "550px" );
    }

    @Override
    public void setHeader( final HeaderPresenter header ) {
        if ( this.header == null ) {
            this.header = header;

            panel.getElement().getStyle().setPaddingLeft( 20, Style.Unit.PX );
            panel.getElement().getStyle().setPaddingRight( 20, Style.Unit.PX );
            panel.add( header.getView() );
        }
    }

    @Override
    public void addBox( final BoxPresenter container ) {
        panel.add( container.getView() );
    }

    @Override
    public void addBox( final BoxPresenter container,
                        final BoxPresenter parentContainer ) {
        panel.insert( container.getView(), panel.getWidgetIndex( parentContainer.getView() ) + 1 );
    }

    @Override
    public void removeBox( final BoxPresenter value ) {
        panel.remove( value.getView() );
    }

    @Override
    public void cleanup() {
        panel.clear();
        if ( header != null ) {
            panel.add( header.getView() );
        }
    }

    @Override
    public void confirmDeleteOperation( final Collection<String> serverNames,
                                        final Collection<List<String>> container2delete,
                                        final Command onConfirm ) {
        YesNoCancelPopup.newYesNoCancelPopup(
                "Delete",
                buildMessage( serverNames, container2delete ),
                new Command() {
                    @Override
                    public void execute() {
                        onConfirm.execute();
                    }
                },
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.YES(),
                ButtonType.DANGER,
                IconType.EXCLAMATION_SIGN,

                new Command() {
                    @Override
                    public void execute() {
                    }
                },
                org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants.INSTANCE.NO(),
                ButtonType.DEFAULT, null, null, null, null, null ).show();

    }

    private String buildMessage( final Collection<String> serverNames,
                                 final Collection<List<String>> container2delete ) {
        final StringBuilder sb = new StringBuilder();
        if ( !serverNames.isEmpty() ) {
            sb.append( Constants.INSTANCE.confirm_delete_servers() ).append( "<br/>" );
            for ( final String s : serverNames ) {
                sb.append( s ).append( ", " );
            }
            sb.setLength( sb.length() - 2 );
            sb.append( "." );
        }
        if ( !container2delete.isEmpty() ) {
            if ( serverNames.isEmpty() ) {
                sb.append( Constants.INSTANCE.confirm_delete_containers() ).append( "<br/>" );
            } else {
                sb.append( "<br/>" ).append( Constants.INSTANCE.and_containers() ).append( "<br/>" );
            }
            for ( final List<String> entry : container2delete ) {
                for ( final String s : entry ) {
                    sb.append( s ).append( ", " );
                }
            }
            sb.setLength( sb.length() - 2 );
            sb.append( "." );
        }
        return sb.toString();
    }
}
