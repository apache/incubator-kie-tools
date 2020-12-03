package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.ModelTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.provider.SubFormFieldProvider;

public class Type_factory__o_k_w_c_f_f_s_f_r_s_p_SubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormFieldProvider> { public Type_factory__o_k_w_c_f_f_s_f_r_s_p_SubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SubFormFieldProvider.class, "Type_factory__o_k_w_c_f_f_s_f_r_s_p_SubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SubFormFieldProvider.class, Object.class, ModelTypeFieldProvider.class, FieldProvider.class });
  }

  public SubFormFieldProvider createInstance(final ContextManager contextManager) {
    final SubFormFieldProvider instance = new SubFormFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}