#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.component.service;

import ${package}.component.model.MyModel;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface MyService {

    MyModel execute( final String param );

}
