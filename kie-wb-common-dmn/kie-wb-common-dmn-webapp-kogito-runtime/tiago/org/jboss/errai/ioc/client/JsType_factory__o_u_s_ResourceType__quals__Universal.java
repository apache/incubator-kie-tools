package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.ResourceType;

public class JsType_factory__o_u_s_ResourceType__quals__Universal extends Factory<ResourceType> { public JsType_factory__o_u_s_ResourceType__quals__Universal() {
    super(new FactoryHandleImpl(ResourceType.class, "JsType_factory__o_u_s_ResourceType__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ResourceType.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ResourceType createInstance(final ContextManager contextManager) {
    return (ResourceType) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.security.ResourceType");
  }
}