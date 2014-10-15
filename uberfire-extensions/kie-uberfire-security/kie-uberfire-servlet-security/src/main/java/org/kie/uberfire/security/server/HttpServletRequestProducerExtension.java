package org.kie.uberfire.security.server;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class HttpServletRequestProducerExtension implements Extension {

    private boolean isHttpRequestSupportAvailable = false;

    public HttpServletRequestProducerExtension() {
        try {
            Class.forName("javax.enterprise.inject.spi.CDI");
            isHttpRequestSupportAvailable = true;
        } catch (ClassNotFoundException e) {
            isHttpRequestSupportAvailable = false;
        }
    }

    <X> void processAnnotatedType(@Observes final javax.enterprise.inject.spi.ProcessAnnotatedType<X> pat, BeanManager beanManager) {
        if (isHttpRequestSupportAvailable) {

            final AnnotatedType<X> annotatedType = pat.getAnnotatedType();
            final Class<X> javaClass = annotatedType.getJavaClass();

            if (javaClass != null && javaClass.getName().equals(HttpRequestProducer.class.getName())) {
                pat.veto();
            }
        }
        return;
    }
}
