package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.ExternalPathPlaceRequest;

public class JsType_factory__o_u_m_i_ExternalPathPlaceRequest__quals__Universal extends Factory<ExternalPathPlaceRequest> { public JsType_factory__o_u_m_i_ExternalPathPlaceRequest__quals__Universal() {
    super(new FactoryHandleImpl(ExternalPathPlaceRequest.class, "JsType_factory__o_u_m_i_ExternalPathPlaceRequest__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ExternalPathPlaceRequest.class, DefaultPlaceRequest.class, Object.class, PlaceRequest.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ExternalPathPlaceRequest createInstance(final ContextManager contextManager) {
    return (ExternalPathPlaceRequest) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.mvp.impl.ExternalPathPlaceRequest");
  }
}