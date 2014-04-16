package org.drools.workbench.common.services.rest.json;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

public class JaxbJacksonObjectMapper extends ObjectMapper {

    public JaxbJacksonObjectMapper() {
        super();

        final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();

        this.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, true);
        this.configure(org.codehaus.jackson.map.SerializationConfig.Feature.WRAP_ROOT_VALUE, true);

        this.setDeserializationConfig(this.getDeserializationConfig().withAnnotationIntrospector(introspector));
        this.setSerializationConfig(this.getSerializationConfig().withAnnotationIntrospector(introspector));

        this.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }
}