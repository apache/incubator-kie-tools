package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.MultipleValueFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.lists.selector.MultipleSelectorProvider;

public class Type_factory__o_k_w_c_f_f_s_f_b_l_s_MultipleSelectorProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorProvider> { public Type_factory__o_k_w_c_f_f_s_f_b_l_s_MultipleSelectorProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleSelectorProvider.class, "Type_factory__o_k_w_c_f_f_s_f_b_l_s_MultipleSelectorProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleSelectorProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class, MultipleValueFieldProvider.class });
  }

  public MultipleSelectorProvider createInstance(final ContextManager contextManager) {
    final MultipleSelectorProvider instance = new MultipleSelectorProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MultipleSelectorProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}