package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;

public class JsType_factory__o_u_c_m_PlaceManager__quals__Universal extends Factory<PlaceManager> { public JsType_factory__o_u_c_m_PlaceManager__quals__Universal() {
    super(new FactoryHandleImpl(PlaceManager.class, "JsType_factory__o_u_c_m_PlaceManager__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PlaceManager.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PlaceManager createInstance(final ContextManager contextManager) {
    return (PlaceManager) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.mvp.PlaceManager");
  }
}