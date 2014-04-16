#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface AppResource
        extends
        ClientBundle {

    AppResource INSTANCE = GWT.create( AppResource.class );

    AppImages images();

}
