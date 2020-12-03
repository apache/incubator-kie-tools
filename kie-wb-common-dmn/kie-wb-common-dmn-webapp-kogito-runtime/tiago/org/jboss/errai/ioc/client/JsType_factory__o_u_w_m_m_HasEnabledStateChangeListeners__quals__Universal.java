package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.menu.HasEnabledStateChangeListeners;

public class JsType_factory__o_u_w_m_m_HasEnabledStateChangeListeners__quals__Universal extends Factory<HasEnabledStateChangeListeners> { public JsType_factory__o_u_w_m_m_HasEnabledStateChangeListeners__quals__Universal() {
    super(new FactoryHandleImpl(HasEnabledStateChangeListeners.class, "JsType_factory__o_u_w_m_m_HasEnabledStateChangeListeners__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { HasEnabledStateChangeListeners.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public HasEnabledStateChangeListeners createInstance(final ContextManager contextManager) {
    return (HasEnabledStateChangeListeners) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.HasEnabledStateChangeListeners");
  }
}