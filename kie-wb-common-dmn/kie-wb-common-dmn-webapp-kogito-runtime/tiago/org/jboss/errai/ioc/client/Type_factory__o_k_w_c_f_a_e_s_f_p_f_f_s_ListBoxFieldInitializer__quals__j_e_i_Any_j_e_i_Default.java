package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.FieldInitializer;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.selectors.ListBoxFieldInitializer;

public class Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_ListBoxFieldInitializer__quals__j_e_i_Any_j_e_i_Default extends Factory<ListBoxFieldInitializer> { public Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_ListBoxFieldInitializer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListBoxFieldInitializer.class, "Type_factory__o_k_w_c_f_a_e_s_f_p_f_f_s_ListBoxFieldInitializer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListBoxFieldInitializer.class, Object.class, FieldInitializer.class });
  }

  public ListBoxFieldInitializer createInstance(final ContextManager contextManager) {
    final ListBoxFieldInitializer instance = new ListBoxFieldInitializer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}