package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceStatus;

public class JsType_factory__o_u_c_m_PlaceStatus__quals__Universal extends Factory<PlaceStatus> { public JsType_factory__o_u_c_m_PlaceStatus__quals__Universal() {
    super(new FactoryHandleImpl(PlaceStatus.class, "JsType_factory__o_u_c_m_PlaceStatus__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PlaceStatus.class, Enum.class, Object.class, Comparable.class, Serializable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PlaceStatus createInstance(final ContextManager contextManager) {
    return (PlaceStatus) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.mvp.PlaceStatus");
  }
}