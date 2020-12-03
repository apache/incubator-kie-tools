package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.DefaultModelValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldStateValidatorImpl;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormValidatorImpl;

public class Type_factory__o_k_w_c_f_p_e_h_i_FormValidatorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FormValidatorImpl> { public Type_factory__o_k_w_c_f_p_e_h_i_FormValidatorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormValidatorImpl.class, "Type_factory__o_k_w_c_f_p_e_h_i_FormValidatorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormValidatorImpl.class, Object.class, FormValidator.class });
  }

  public FormValidatorImpl createInstance(final ContextManager contextManager) {
    final ModelValidator _modelValidator_0 = (DefaultModelValidator) contextManager.getInstance("Type_factory__o_k_w_c_f_p_e_h_i_DefaultModelValidator__quals__j_e_i_Any_j_e_i_Default");
    final FieldStateValidator _fieldStateValidator_1 = (FieldStateValidatorImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default");
    final FormValidatorImpl instance = new FormValidatorImpl(_modelValidator_0, _fieldStateValidator_1);
    registerDependentScopedReference(instance, _modelValidator_0);
    registerDependentScopedReference(instance, _fieldStateValidator_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}