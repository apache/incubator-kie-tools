package org.jboss.errai.ioc.client;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBuiltinAggregator;

public class JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITBuiltinAggregator__quals__Universal extends Factory<JSITBuiltinAggregator> { public JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITBuiltinAggregator__quals__Universal() {
    super(new FactoryHandleImpl(JSITBuiltinAggregator.class, "JsType_factory__o_k_w_c_d_w_k_m_j_m_d_JSITBuiltinAggregator__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { JSITBuiltinAggregator.class, Enum.class, Object.class, Comparable.class, Serializable.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public JSITBuiltinAggregator createInstance(final ContextManager contextManager) {
    return (JSITBuiltinAggregator) WindowInjectionContextStorage.createOrGet().getBean("org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBuiltinAggregator");
  }
}