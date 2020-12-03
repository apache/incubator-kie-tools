package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.menu.Menus;

public class JsType_factory__o_u_w_m_m_Menus__quals__Universal extends Factory<Menus> { public JsType_factory__o_u_w_m_m_Menus__quals__Universal() {
    super(new FactoryHandleImpl(Menus.class, "JsType_factory__o_u_w_m_m_Menus__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { Menus.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public Menus createInstance(final ContextManager contextManager) {
    return (Menus) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.Menus");
  }
}