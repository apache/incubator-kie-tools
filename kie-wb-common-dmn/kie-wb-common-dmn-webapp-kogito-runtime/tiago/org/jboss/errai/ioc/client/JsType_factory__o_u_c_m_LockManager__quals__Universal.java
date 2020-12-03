package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockManager;

public class JsType_factory__o_u_c_m_LockManager__quals__Universal extends Factory<LockManager> { public JsType_factory__o_u_c_m_LockManager__quals__Universal() {
    super(new FactoryHandleImpl(LockManager.class, "JsType_factory__o_u_c_m_LockManager__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { LockManager.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public LockManager createInstance(final ContextManager contextManager) {
    return (LockManager) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.mvp.LockManager");
  }
}