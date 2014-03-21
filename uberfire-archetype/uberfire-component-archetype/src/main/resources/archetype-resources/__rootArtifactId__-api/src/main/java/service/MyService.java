#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service;

import org.jboss.errai.bus.server.annotations.Remote;
import ${package}.model.MyModel;

@Remote
public interface MyService {

    public MyModel execute( final String param );

}
