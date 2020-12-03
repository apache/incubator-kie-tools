package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.provider.IntegerBoxFieldProvider;

public class Type_factory__o_k_w_c_f_f_s_f_b_i_p_IntegerBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBoxFieldProvider> { public Type_factory__o_k_w_c_f_f_s_f_b_i_p_IntegerBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IntegerBoxFieldProvider.class, "Type_factory__o_k_w_c_f_f_s_f_b_i_p_IntegerBoxFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IntegerBoxFieldProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class });
  }

  public IntegerBoxFieldProvider createInstance(final ContextManager contextManager) {
    final IntegerBoxFieldProvider instance = new IntegerBoxFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final IntegerBoxFieldProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}