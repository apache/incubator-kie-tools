package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Map;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__j_u_Map__quals__Universal extends Factory<Map> { public JsType_factory__j_u_Map__quals__Universal() {
    super(new FactoryHandleImpl(Map.class, "JsType_factory__j_u_Map__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Map.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Map createInstance(final ContextManager contextManager) {
    return (Map) WindowInjectionContextStorage.createOrGet().getBean("java.util.Map");
  }
}