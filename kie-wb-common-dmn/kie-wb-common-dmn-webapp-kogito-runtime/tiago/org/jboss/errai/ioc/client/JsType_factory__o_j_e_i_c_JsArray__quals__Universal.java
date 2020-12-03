package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__o_j_e_i_c_JsArray__quals__Universal extends Factory<JsArray> { public JsType_factory__o_j_e_i_c_JsArray__quals__Universal() {
    super(new FactoryHandleImpl(JsArray.class, "JsType_factory__o_j_e_i_c_JsArray__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { JsArray.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public JsArray createInstance(final ContextManager contextManager) {
    return (JsArray) WindowInjectionContextStorage.createOrGet().getBean("org.jboss.errai.ioc.client.JsArray");
  }
}