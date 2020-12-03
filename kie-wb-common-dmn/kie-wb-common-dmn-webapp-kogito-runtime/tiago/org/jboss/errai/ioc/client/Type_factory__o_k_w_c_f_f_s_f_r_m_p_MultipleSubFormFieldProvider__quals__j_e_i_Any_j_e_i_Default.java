package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.MultipleValueFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.ModelTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.multipleSubform.provider.MultipleSubFormFieldProvider;

public class Type_factory__o_k_w_c_f_f_s_f_r_m_p_MultipleSubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSubFormFieldProvider> { public Type_factory__o_k_w_c_f_f_s_f_r_m_p_MultipleSubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleSubFormFieldProvider.class, "Type_factory__o_k_w_c_f_f_s_f_r_m_p_MultipleSubFormFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleSubFormFieldProvider.class, Object.class, ModelTypeFieldProvider.class, FieldProvider.class, MultipleValueFieldProvider.class });
  }

  public MultipleSubFormFieldProvider createInstance(final ContextManager contextManager) {
    final MultipleSubFormFieldProvider instance = new MultipleSubFormFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}