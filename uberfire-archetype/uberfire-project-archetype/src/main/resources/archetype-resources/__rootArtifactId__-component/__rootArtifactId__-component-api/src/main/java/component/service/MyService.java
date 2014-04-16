package ${package}.component.service;

import org.jboss.errai.bus.server.annotations.Remote;
import ${package}.component.model.MyModel;

@Remote
public interface MyService {

    public MyModel execute( final String param );

}
