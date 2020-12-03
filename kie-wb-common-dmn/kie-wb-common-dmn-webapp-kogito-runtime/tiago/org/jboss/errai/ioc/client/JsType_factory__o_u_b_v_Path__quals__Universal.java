package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.backend.vfs.Path;

public class JsType_factory__o_u_b_v_Path__quals__Universal extends Factory<Path> { public JsType_factory__o_u_b_v_Path__quals__Universal() {
    super(new FactoryHandleImpl(Path.class, "JsType_factory__o_u_b_v_Path__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Path.class, Comparable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Path createInstance(final ContextManager contextManager) {
    return (Path) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.backend.vfs.Path");
  }
}