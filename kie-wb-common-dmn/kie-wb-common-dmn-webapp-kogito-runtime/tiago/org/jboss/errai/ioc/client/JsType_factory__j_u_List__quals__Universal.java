package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__j_u_List__quals__Universal extends Factory<List> { public JsType_factory__j_u_List__quals__Universal() {
    super(new FactoryHandleImpl(List.class, "JsType_factory__j_u_List__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { List.class, Collection.class, Iterable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public List createInstance(final ContextManager contextManager) {
    return (List) WindowInjectionContextStorage.createOrGet().getBean("java.util.List");
  }
}