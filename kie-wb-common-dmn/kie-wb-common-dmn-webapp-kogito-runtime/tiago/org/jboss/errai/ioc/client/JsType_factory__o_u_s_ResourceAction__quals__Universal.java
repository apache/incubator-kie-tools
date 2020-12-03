package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.ResourceAction;

public class JsType_factory__o_u_s_ResourceAction__quals__Universal extends Factory<ResourceAction> { public JsType_factory__o_u_s_ResourceAction__quals__Universal() {
    super(new FactoryHandleImpl(ResourceAction.class, "JsType_factory__o_u_s_ResourceAction__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ResourceAction.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ResourceAction createInstance(final ContextManager contextManager) {
    return (ResourceAction) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.security.ResourceAction");
  }
}