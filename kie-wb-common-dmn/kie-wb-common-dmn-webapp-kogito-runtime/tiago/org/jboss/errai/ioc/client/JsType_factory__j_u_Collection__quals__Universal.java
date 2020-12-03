package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__j_u_Collection__quals__Universal extends Factory<Collection> { public JsType_factory__j_u_Collection__quals__Universal() {
    super(new FactoryHandleImpl(Collection.class, "JsType_factory__j_u_Collection__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Collection.class, Iterable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Collection createInstance(final ContextManager contextManager) {
    return (Collection) WindowInjectionContextStorage.createOrGet().getBean("java.util.Collection");
  }
}