package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeResource;

public class JsType_factory__o_u_s_a_RuntimeResource__quals__Universal extends Factory<RuntimeResource> { public JsType_factory__o_u_s_a_RuntimeResource__quals__Universal() {
    super(new FactoryHandleImpl(RuntimeResource.class, "JsType_factory__o_u_s_a_RuntimeResource__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { RuntimeResource.class, Resource.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public RuntimeResource createInstance(final ContextManager contextManager) {
    return (RuntimeResource) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.security.authz.RuntimeResource");
  }
}