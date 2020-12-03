package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.workbench.model.menu.HasEnabledStateChangeListeners;
import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuItem;

public class JsType_factory__o_u_w_m_m_MenuCustom__quals__Universal extends Factory<MenuCustom> { public JsType_factory__o_u_w_m_m_MenuCustom__quals__Universal() {
    super(new FactoryHandleImpl(MenuCustom.class, "JsType_factory__o_u_w_m_m_MenuCustom__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { MenuCustom.class, MenuItem.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, HasEnabledStateChangeListeners.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public MenuCustom createInstance(final ContextManager contextManager) {
    return (MenuCustom) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.MenuCustom");
  }
}