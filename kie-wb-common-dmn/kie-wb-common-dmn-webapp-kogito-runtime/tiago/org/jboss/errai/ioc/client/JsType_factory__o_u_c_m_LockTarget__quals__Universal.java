package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockTarget;

public class JsType_factory__o_u_c_m_LockTarget__quals__Universal extends Factory<LockTarget> { public JsType_factory__o_u_c_m_LockTarget__quals__Universal() {
    super(new FactoryHandleImpl(LockTarget.class, "JsType_factory__o_u_c_m_LockTarget__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { LockTarget.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public LockTarget createInstance(final ContextManager contextManager) {
    return (LockTarget) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.mvp.LockTarget");
  }
}