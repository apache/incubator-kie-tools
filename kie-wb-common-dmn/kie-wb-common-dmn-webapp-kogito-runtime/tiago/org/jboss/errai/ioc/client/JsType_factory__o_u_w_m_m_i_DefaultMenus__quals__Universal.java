package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.DefaultMenus;

public class JsType_factory__o_u_w_m_m_i_DefaultMenus__quals__Universal extends Factory<DefaultMenus> { public JsType_factory__o_u_w_m_m_i_DefaultMenus__quals__Universal() {
    super(new FactoryHandleImpl(DefaultMenus.class, "JsType_factory__o_u_w_m_m_i_DefaultMenus__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { DefaultMenus.class, Object.class, Menus.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public DefaultMenus createInstance(final ContextManager contextManager) {
    return (DefaultMenus) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.impl.DefaultMenus");
  }
}