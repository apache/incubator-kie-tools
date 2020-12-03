package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.ContextDefinition;

public class JsType_factory__o_u_w_m_ContextDefinition__quals__Universal extends Factory<ContextDefinition> { public JsType_factory__o_u_w_m_ContextDefinition__quals__Universal() {
    super(new FactoryHandleImpl(ContextDefinition.class, "JsType_factory__o_u_w_m_ContextDefinition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ContextDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ContextDefinition createInstance(final ContextManager contextManager) {
    return (ContextDefinition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.ContextDefinition");
  }
}