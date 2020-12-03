package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldStateValidatorImpl;

public class Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldStateValidatorImpl> { public Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldStateValidatorImpl.class, "Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldStateValidatorImpl.class, Object.class, FieldStateValidator.class });
  }

  public FieldStateValidatorImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FieldStateValidatorImpl instance = new FieldStateValidatorImpl(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final FieldStateValidatorImpl instance) {
    instance.initialize();
  }
}