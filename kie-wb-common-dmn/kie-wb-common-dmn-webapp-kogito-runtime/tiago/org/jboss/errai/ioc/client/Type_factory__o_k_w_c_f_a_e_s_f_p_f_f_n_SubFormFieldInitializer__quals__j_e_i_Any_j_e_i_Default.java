package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.AbstractEmbeddedFormsInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer;

public class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_SubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<SubFormFieldInitializer> { public Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_SubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SubFormFieldInitializer.class, "Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_n_SubFormFieldInitializer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SubFormFieldInitializer.class, AbstractEmbeddedFormsInitializer.class, Object.class, FieldInitializer.class });
  }

  public SubFormFieldInitializer createInstance(final ContextManager contextManager) {
    final SubFormFieldInitializer instance = new SubFormFieldInitializer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}