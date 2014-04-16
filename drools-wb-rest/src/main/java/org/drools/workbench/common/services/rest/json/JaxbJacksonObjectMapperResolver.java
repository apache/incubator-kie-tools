package org.drools.workbench.common.services.rest.json;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.map.ObjectMapper;


@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JaxbJacksonObjectMapperResolver implements ContextResolver<ObjectMapper> {

    // Do not make this static: there are no gaurantees about the thread-safety of the Jackson code
    private final ObjectMapper objectMapper = new JaxbJacksonObjectMapper();
            
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }

}
