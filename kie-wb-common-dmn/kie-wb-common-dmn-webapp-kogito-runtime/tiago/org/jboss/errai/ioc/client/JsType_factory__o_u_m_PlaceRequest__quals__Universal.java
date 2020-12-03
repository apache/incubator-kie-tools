package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.mvp.PlaceRequest;

public class JsType_factory__o_u_m_PlaceRequest__quals__Universal extends Factory<PlaceRequest> { public JsType_factory__o_u_m_PlaceRequest__quals__Universal() {
    super(new FactoryHandleImpl(PlaceRequest.class, "JsType_factory__o_u_m_PlaceRequest__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PlaceRequest.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PlaceRequest createInstance(final ContextManager contextManager) {
    return (PlaceRequest) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.mvp.PlaceRequest");
  }
}