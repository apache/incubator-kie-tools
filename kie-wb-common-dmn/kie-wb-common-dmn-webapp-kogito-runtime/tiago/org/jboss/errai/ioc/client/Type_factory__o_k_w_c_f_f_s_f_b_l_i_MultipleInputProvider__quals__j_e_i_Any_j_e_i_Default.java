package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.MultipleValueFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.input.MultipleInputProvider;

public class Type_factory__o_k_w_c_f_f_s_f_b_l_i_MultipleInputProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInputProvider> { public Type_factory__o_k_w_c_f_f_s_f_b_l_i_MultipleInputProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleInputProvider.class, "Type_factory__o_k_w_c_f_f_s_f_b_l_i_MultipleInputProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleInputProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class, MultipleValueFieldProvider.class });
  }

  public MultipleInputProvider createInstance(final ContextManager contextManager) {
    final MultipleInputProvider instance = new MultipleInputProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MultipleInputProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}