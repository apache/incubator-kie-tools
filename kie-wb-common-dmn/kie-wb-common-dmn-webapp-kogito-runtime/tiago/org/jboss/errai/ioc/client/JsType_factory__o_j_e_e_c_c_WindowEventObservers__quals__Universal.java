package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.WindowEventObservers;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__o_j_e_e_c_c_WindowEventObservers__quals__Universal extends Factory<WindowEventObservers> { public JsType_factory__o_j_e_e_c_c_WindowEventObservers__quals__Universal() {
    super(new FactoryHandleImpl(WindowEventObservers.class, "JsType_factory__o_j_e_e_c_c_WindowEventObservers__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { WindowEventObservers.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public WindowEventObservers createInstance(final ContextManager contextManager) {
    return (WindowEventObservers) WindowInjectionContextStorage.createOrGet().getBean("org.jboss.errai.enterprise.client.cdi.WindowEventObservers");
  }
}