package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.toolbar.ToolBarIcon;

public class JsType_factory__o_u_w_m_t_ToolBarIcon__quals__Universal extends Factory<ToolBarIcon> { public JsType_factory__o_u_w_m_t_ToolBarIcon__quals__Universal() {
    super(new FactoryHandleImpl(ToolBarIcon.class, "JsType_factory__o_u_w_m_t_ToolBarIcon__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ToolBarIcon.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ToolBarIcon createInstance(final ContextManager contextManager) {
    return (ToolBarIcon) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.toolbar.ToolBarIcon");
  }
}