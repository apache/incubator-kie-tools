package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.PanelDefinition;

public class JsType_factory__o_u_w_m_PanelDefinition__quals__Universal extends Factory<PanelDefinition> { public JsType_factory__o_u_w_m_PanelDefinition__quals__Universal() {
    super(new FactoryHandleImpl(PanelDefinition.class, "JsType_factory__o_u_w_m_PanelDefinition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PanelDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PanelDefinition createInstance(final ContextManager contextManager) {
    return (PanelDefinition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.PanelDefinition");
  }
}