package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

public class JsType_factory__o_u_w_m_CompassPosition__quals__Universal extends Factory<CompassPosition> { public JsType_factory__o_u_w_m_CompassPosition__quals__Universal() {
    super(new FactoryHandleImpl(CompassPosition.class, "JsType_factory__o_u_w_m_CompassPosition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { CompassPosition.class, Enum.class, Object.class, Comparable.class, Serializable.class, Position.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public CompassPosition createInstance(final ContextManager contextManager) {
    return (CompassPosition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.CompassPosition");
  }
}