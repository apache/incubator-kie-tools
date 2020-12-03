package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.PerspectiveDefinition;

public class JsType_factory__o_u_w_m_PerspectiveDefinition__quals__Universal extends Factory<PerspectiveDefinition> { public JsType_factory__o_u_w_m_PerspectiveDefinition__quals__Universal() {
    super(new FactoryHandleImpl(PerspectiveDefinition.class, "JsType_factory__o_u_w_m_PerspectiveDefinition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PerspectiveDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PerspectiveDefinition createInstance(final ContextManager contextManager) {
    return (PerspectiveDefinition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.PerspectiveDefinition");
  }
}