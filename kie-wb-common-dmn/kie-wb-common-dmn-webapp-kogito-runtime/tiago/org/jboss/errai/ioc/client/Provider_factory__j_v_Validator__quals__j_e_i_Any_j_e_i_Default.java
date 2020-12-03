package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import javax.inject.Provider;
import javax.validation.Validator;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default extends Factory<Validator> { public Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Validator.class, "Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Validator.class });
  }

  public Validator createInstance(final ContextManager contextManager) {
    final Provider<Validator> provider = (Provider<Validator>) contextManager.getInstance("Type_factory__o_j_e_v_c_ValidatorProvider__quals__j_e_i_Any_j_e_i_Default");
    final Validator instance = provider.get();
    return instance;
  }
}