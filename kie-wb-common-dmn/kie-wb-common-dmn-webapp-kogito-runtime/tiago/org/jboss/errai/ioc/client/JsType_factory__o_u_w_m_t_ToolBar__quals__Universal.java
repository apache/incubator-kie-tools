package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class JsType_factory__o_u_w_m_t_ToolBar__quals__Universal extends Factory<ToolBar> { public JsType_factory__o_u_w_m_t_ToolBar__quals__Universal() {
    super(new FactoryHandleImpl(ToolBar.class, "JsType_factory__o_u_w_m_t_ToolBar__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ToolBar.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ToolBar createInstance(final ContextManager contextManager) {
    return (ToolBar) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.toolbar.ToolBar");
  }
}