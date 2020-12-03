package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class JsType_factory__o_u_w_t_ResourceTypeDefinition__quals__Universal extends Factory<ResourceTypeDefinition> { public JsType_factory__o_u_w_t_ResourceTypeDefinition__quals__Universal() {
    super(new FactoryHandleImpl(ResourceTypeDefinition.class, "JsType_factory__o_u_w_t_ResourceTypeDefinition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ResourceTypeDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ResourceTypeDefinition createInstance(final ContextManager contextManager) {
    return (ResourceTypeDefinition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.type.ResourceTypeDefinition");
  }
}