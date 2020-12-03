package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.Resource;

public class JsType_factory__o_u_s_Resource__quals__Universal extends Factory<Resource> { public JsType_factory__o_u_s_Resource__quals__Universal() {
    super(new FactoryHandleImpl(Resource.class, "JsType_factory__o_u_s_Resource__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Resource.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Resource createInstance(final ContextManager contextManager) {
    return (Resource) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.security.Resource");
  }
}