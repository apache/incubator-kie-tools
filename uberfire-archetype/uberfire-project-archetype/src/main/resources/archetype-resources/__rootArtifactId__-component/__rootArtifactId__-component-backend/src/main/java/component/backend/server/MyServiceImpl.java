#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.component.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import ${package}.component.model.MyModel;
import ${package}.component.service.MyService;

@Service
@ApplicationScoped
public class MyServiceImpl implements MyService {

    @Override
    public MyModel execute( String param ) {
        return new MyModel( "Value from ${capitalizedRootArtifactId} server! " + param );
    }
}
