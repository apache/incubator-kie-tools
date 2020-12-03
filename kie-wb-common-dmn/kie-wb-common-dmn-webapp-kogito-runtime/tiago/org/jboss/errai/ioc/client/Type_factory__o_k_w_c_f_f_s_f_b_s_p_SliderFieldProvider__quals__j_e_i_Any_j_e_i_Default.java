package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.slider.provider.SliderFieldProvider;

public class Type_factory__o_k_w_c_f_f_s_f_b_s_p_SliderFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFieldProvider> { public Type_factory__o_k_w_c_f_f_s_f_b_s_p_SliderFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SliderFieldProvider.class, "Type_factory__o_k_w_c_f_f_s_f_b_s_p_SliderFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SliderFieldProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class });
  }

  public SliderFieldProvider createInstance(final ContextManager contextManager) {
    final SliderFieldProvider instance = new SliderFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SliderFieldProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}