package org.jboss.errai.ioc.client;

import javax.inject.Provider;
import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.validation.client.ValidatorProvider;

public class Type_factory__o_j_e_v_c_ValidatorProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidatorProvider> { public Type_factory__o_j_e_v_c_ValidatorProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValidatorProvider.class, "Type_factory__o_j_e_v_c_ValidatorProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValidatorProvider.class, Object.class, Provider.class });
  }

  public ValidatorProvider createInstance(final ContextManager contextManager) {
    final ValidatorProvider instance = new ValidatorProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}