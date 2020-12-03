package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.i18n.I18nServiceProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_i_I18nServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<I18nServiceProducer> { public Type_factory__o_a_k_b_c_i_I18nServiceProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(I18nServiceProducer.class, "Type_factory__o_a_k_b_c_i_I18nServiceProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { I18nServiceProducer.class, Object.class });
  }

  public I18nServiceProducer createInstance(final ContextManager contextManager) {
    final I18nServiceProducer instance = new I18nServiceProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}