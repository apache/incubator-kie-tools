package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.forms.processing.engine.handling.FormHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldChangeHandlerManagerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormHandlerImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormValidatorImpl;

public class Type_factory__o_k_w_c_f_p_e_h_i_FormHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormHandlerImpl> { public Type_factory__o_k_w_c_f_p_e_h_i_FormHandlerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormHandlerImpl.class, "Type_factory__o_k_w_c_f_p_e_h_i_FormHandlerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormHandlerImpl.class, Object.class, FormHandler.class });
  }

  public FormHandlerImpl createInstance(final ContextManager contextManager) {
    final FieldChangeHandlerManager _fieldChangeManager_1 = (FieldChangeHandlerManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_p_e_h_i_FieldChangeHandlerManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final FormValidator _validator_0 = (FormValidatorImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_p_e_h_i_FormValidatorImpl__quals__j_e_i_Any_j_e_i_Default");
    final FormHandlerImpl instance = new FormHandlerImpl(_validator_0, _fieldChangeManager_1);
    registerDependentScopedReference(instance, _fieldChangeManager_1);
    registerDependentScopedReference(instance, _validator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}