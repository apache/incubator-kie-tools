package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.SelectorFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.listBox.provider.ListBoxFieldProvider;

public class Type_factory__o_k_w_c_f_f_s_f_b_s_l_p_ListBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ListBoxFieldProvider> { public Type_factory__o_k_w_c_f_f_s_f_b_s_l_p_ListBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListBoxFieldProvider.class, "Type_factory__o_k_w_c_f_f_s_f_b_s_l_p_ListBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListBoxFieldProvider.class, SelectorFieldProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class });
  }

  public ListBoxFieldProvider createInstance(final ContextManager contextManager) {
    final ListBoxFieldProvider instance = new ListBoxFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ListBoxFieldProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}