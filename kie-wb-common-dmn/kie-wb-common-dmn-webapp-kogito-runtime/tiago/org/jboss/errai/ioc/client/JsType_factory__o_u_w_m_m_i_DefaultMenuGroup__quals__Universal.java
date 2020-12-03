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
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.impl.DefaultMenuGroup;

public class JsType_factory__o_u_w_m_m_i_DefaultMenuGroup__quals__Universal extends Factory<DefaultMenuGroup> { public JsType_factory__o_u_w_m_m_i_DefaultMenuGroup__quals__Universal() {
    super(new FactoryHandleImpl(DefaultMenuGroup.class, "JsType_factory__o_u_w_m_m_i_DefaultMenuGroup__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { DefaultMenuGroup.class, Object.class, MenuGroup.class, MenuItem.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, HasEnabledStateChangeListeners.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public DefaultMenuGroup createInstance(final ContextManager contextManager) {
    return (DefaultMenuGroup) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.menu.impl.DefaultMenuGroup");
  }
}