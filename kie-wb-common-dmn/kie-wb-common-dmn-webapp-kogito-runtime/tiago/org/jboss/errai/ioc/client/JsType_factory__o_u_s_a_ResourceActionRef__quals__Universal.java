package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.authz.ResourceActionRef;

public class JsType_factory__o_u_s_a_ResourceActionRef__quals__Universal extends Factory<ResourceActionRef> { public JsType_factory__o_u_s_a_ResourceActionRef__quals__Universal() {
    super(new FactoryHandleImpl(ResourceActionRef.class, "JsType_factory__o_u_s_a_ResourceActionRef__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ResourceActionRef.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ResourceActionRef createInstance(final ContextManager contextManager) {
    return (ResourceActionRef) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.security.authz.ResourceActionRef");
  }
}