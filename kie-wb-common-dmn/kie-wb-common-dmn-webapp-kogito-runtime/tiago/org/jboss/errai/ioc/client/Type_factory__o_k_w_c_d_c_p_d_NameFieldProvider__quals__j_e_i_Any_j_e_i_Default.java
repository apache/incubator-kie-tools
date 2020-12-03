package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.property.dmn.NameFieldProvider;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;

public class Type_factory__o_k_w_c_d_c_p_d_NameFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<NameFieldProvider> { public Type_factory__o_k_w_c_d_c_p_d_NameFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NameFieldProvider.class, "Type_factory__o_k_w_c_d_c_p_d_NameFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NameFieldProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class });
  }

  public NameFieldProvider createInstance(final ContextManager contextManager) {
    final NameFieldProvider instance = new NameFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final NameFieldProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}