package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.JsTypeProvider;

public class JsType_factory__o_j_e_i_c_c_JsTypeProvider__quals__Universal extends Factory<JsTypeProvider> { public JsType_factory__o_j_e_i_c_c_JsTypeProvider__quals__Universal() {
    super(new FactoryHandleImpl(JsTypeProvider.class, "JsType_factory__o_j_e_i_c_c_JsTypeProvider__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { JsTypeProvider.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public JsTypeProvider createInstance(final ContextManager contextManager) {
    return (JsTypeProvider) WindowInjectionContextStorage.createOrGet().getBean("org.jboss.errai.ioc.client.container.JsTypeProvider");
  }
}