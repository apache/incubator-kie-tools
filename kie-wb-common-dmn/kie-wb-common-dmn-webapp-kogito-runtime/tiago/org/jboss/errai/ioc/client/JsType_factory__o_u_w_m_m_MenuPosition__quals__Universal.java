package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.menu.MenuPosition;

public class JsType_factory__o_u_w_m_m_MenuPosition__quals__Universal extends Factory<MenuPosition> { public JsType_factory__o_u_w_m_m_MenuPosition__quals__Universal() {
    super(new FactoryHandleImpl(MenuPosition.class, "JsType_factory__o_u_w_m_m_MenuPosition__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { MenuPosition.class, Enum.class, Object.class, Comparable.class, Serializable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public MenuPosition createInstance(final ContextManager contextManager) {
    return (MenuPosition) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.MenuPosition");
  }
}