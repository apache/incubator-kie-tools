#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.component.backend.server;

import javax.enterprise.context.ApplicationScoped;

import ${package}.component.model.MyModel;
import ${package}.component.service.MyService;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class MyServiceImpl implements MyService {

    @Override
    public MyModel execute( String param ) {
        return new MyModel( "Value from Cooltech server! " + param );
    }
}
