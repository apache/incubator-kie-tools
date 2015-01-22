#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;

public interface AppResource
        extends
        ClientBundle {

    AppResource INSTANCE = GWT.create( AppResource.class );

    AppImages images();

    @Source("css/welcome.css")
    WelcomeCss CSS();

    @Source("images/downloads-bgr.png")
    DataResource downloadsBgr();

    @Source("images/get-started-bgr.png")
    DataResource getStartedBgr();

    @Source("images/git-bgr.png")
    DataResource gitBgr();

}
