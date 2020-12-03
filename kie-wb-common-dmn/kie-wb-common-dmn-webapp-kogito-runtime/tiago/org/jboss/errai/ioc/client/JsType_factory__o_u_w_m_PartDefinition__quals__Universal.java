package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.PartDefinition;

public class JsType_factory__o_u_w_m_PartDefinition__quals__Universal extends Factory<PartDefinition> { public JsType_factory__o_u_w_m_PartDefinition__quals__Universal() {
    super(new FactoryHandleImpl(PartDefinition.class, "JsType_factory__o_u_w_m_PartDefinition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PartDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PartDefinition createInstance(final ContextManager contextManager) {
    return (PartDefinition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.PartDefinition");
  }
}