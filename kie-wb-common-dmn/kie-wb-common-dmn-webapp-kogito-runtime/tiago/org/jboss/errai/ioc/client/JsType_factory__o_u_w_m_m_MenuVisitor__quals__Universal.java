package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.menu.MenuVisitor;

public class JsType_factory__o_u_w_m_m_MenuVisitor__quals__Universal extends Factory<MenuVisitor> { public JsType_factory__o_u_w_m_m_MenuVisitor__quals__Universal() {
    super(new FactoryHandleImpl(MenuVisitor.class, "JsType_factory__o_u_w_m_m_MenuVisitor__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { MenuVisitor.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public MenuVisitor createInstance(final ContextManager contextManager) {
    return (MenuVisitor) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.MenuVisitor");
  }
}