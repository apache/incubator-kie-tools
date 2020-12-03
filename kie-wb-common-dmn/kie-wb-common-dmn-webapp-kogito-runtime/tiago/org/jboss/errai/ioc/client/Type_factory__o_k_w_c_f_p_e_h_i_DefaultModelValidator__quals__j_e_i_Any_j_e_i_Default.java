package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import javax.validation.Validator;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.DefaultModelValidator;

public class Type_factory__o_k_w_c_f_p_e_h_i_DefaultModelValidator__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultModelValidator> { public Type_factory__o_k_w_c_f_p_e_h_i_DefaultModelValidator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultModelValidator.class, "Type_factory__o_k_w_c_f_p_e_h_i_DefaultModelValidator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultModelValidator.class, Object.class, ModelValidator.class });
  }

  public DefaultModelValidator createInstance(final ContextManager contextManager) {
    final Validator _validator_0 = (Validator) contextManager.getInstance("Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default");
    final DefaultModelValidator instance = new DefaultModelValidator(_validator_0);
    registerDependentScopedReference(instance, _validator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}