package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.ContextDisplayMode;

public class JsType_factory__o_u_w_m_ContextDisplayMode__quals__Universal extends Factory<ContextDisplayMode> { public JsType_factory__o_u_w_m_ContextDisplayMode__quals__Universal() {
    super(new FactoryHandleImpl(ContextDisplayMode.class, "JsType_factory__o_u_w_m_ContextDisplayMode__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { ContextDisplayMode.class, Enum.class, Object.class, Comparable.class, Serializable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public ContextDisplayMode createInstance(final ContextManager contextManager) {
    return (ContextDisplayMode) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.workbench.model.ContextDisplayMode");
  }
}