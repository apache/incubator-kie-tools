package org.kie.workbench.common.screens.server.management.client.util;

import com.google.gwt.dom.client.Element;
import org.kie.workbench.common.screens.server.management.client.resources.ContainerResources;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;

/**
 * TODO: update me
 */
public final class ContainerStatusUtil {

    public static void setupStatus( final Element status,
                                    final ContainerStatus state ) {
        status.removeAttribute( "class" );
        switch ( state ) {
            case STARTED:
                status.setTitle( "Started" );
                status.addClassName( "icon-play-circle" );
                status.addClassName( ContainerResources.INSTANCE.CSS().green() );
                break;
            case STOPPED:
                status.setTitle( "Stopped" );
                status.addClassName( "icon-off" );
                status.addClassName( ContainerResources.INSTANCE.CSS().orange() );
                break;
            case LOADING:
                status.setTitle( "Loading" );
                status.addClassName( "icon-spin" );
                status.addClassName( "icon-refresh" );
                break;
            case ERROR:
                status.setTitle( "Error" );
                status.addClassName( "icon-exclamation-sign" );
                status.addClassName( ContainerResources.INSTANCE.CSS().red() );
                break;
            default:
                status.setTitle( "" );
                break;
        }
    }
}
