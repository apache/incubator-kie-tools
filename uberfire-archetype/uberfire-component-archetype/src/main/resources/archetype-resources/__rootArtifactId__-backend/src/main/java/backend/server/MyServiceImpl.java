package ${package}.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import ${package}.model.MyModel;
import ${package}.service.MyService;

@Service
@ApplicationScoped
public class MyServiceImpl implements MyService {

    @Override
    public MyModel execute( String param ) {
        return new MyModel( "Value from ${capitalizedComponentId} server! " + param );
    }
}
