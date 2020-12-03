package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

public class JsType_factory__o_u_w_m_i_PanelDefinitionImpl__quals__Universal extends Factory<PanelDefinitionImpl> { public JsType_factory__o_u_w_m_i_PanelDefinitionImpl__quals__Universal() {
    super(new FactoryHandleImpl(PanelDefinitionImpl.class, "JsType_factory__o_u_w_m_i_PanelDefinitionImpl__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { PanelDefinitionImpl.class, Object.class, PanelDefinition.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public PanelDefinitionImpl createInstance(final ContextManager contextManager) {
    return (PanelDefinitionImpl) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.impl.PanelDefinitionImpl");
  }
}