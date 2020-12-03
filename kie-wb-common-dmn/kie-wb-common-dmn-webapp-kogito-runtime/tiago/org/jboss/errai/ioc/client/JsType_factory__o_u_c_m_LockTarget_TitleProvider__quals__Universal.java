package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.LockTarget.TitleProvider;

public class JsType_factory__o_u_c_m_LockTarget_TitleProvider__quals__Universal extends Factory<TitleProvider> { public JsType_factory__o_u_c_m_LockTarget_TitleProvider__quals__Universal() {
    super(new FactoryHandleImpl(TitleProvider.class, "JsType_factory__o_u_c_m_LockTarget_TitleProvider__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { TitleProvider.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public TitleProvider createInstance(final ContextManager contextManager) {
    return (TitleProvider) WindowInjectionContextStorage.createOrGet().getBean("org.uberfire.client.mvp.LockTarget$TitleProvider");
  }
}