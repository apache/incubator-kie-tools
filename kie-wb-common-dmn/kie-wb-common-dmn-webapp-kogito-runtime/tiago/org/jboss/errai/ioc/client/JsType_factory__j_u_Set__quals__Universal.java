package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__j_u_Set__quals__Universal extends Factory<Set> { public JsType_factory__j_u_Set__quals__Universal() {
    super(new FactoryHandleImpl(Set.class, "JsType_factory__j_u_Set__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Set.class, Collection.class, Iterable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Set createInstance(final ContextManager contextManager) {
    return (Set) WindowInjectionContextStorage.createOrGet().getBean("java.util.Set");
  }
}