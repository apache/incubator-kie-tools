package org.jboss.errai.ioc.client;

import javax.inject.Provider;
import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationServiceProvider;

public class Type_factory__o_j_e_u_c_l_s_TranslationServiceProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TranslationServiceProvider> { public Type_factory__o_j_e_u_c_l_s_TranslationServiceProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TranslationServiceProvider.class, "Type_factory__o_j_e_u_c_l_s_TranslationServiceProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TranslationServiceProvider.class, Object.class, Provider.class });
  }

  public TranslationServiceProvider createInstance(final ContextManager contextManager) {
    final TranslationServiceProvider instance = new TranslationServiceProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}