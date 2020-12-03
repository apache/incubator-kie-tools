package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.fields.shared.FieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.image.provider.PictureFieldProvider;

public class Type_factory__o_k_w_c_f_f_s_f_b_i_p_PictureFieldProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureFieldProvider> { public Type_factory__o_k_w_c_f_f_s_f_b_i_p_PictureFieldProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PictureFieldProvider.class, "Type_factory__o_k_w_c_f_f_s_f_b_i_p_PictureFieldProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PictureFieldProvider.class, BasicTypeFieldProvider.class, Object.class, FieldProvider.class });
  }

  public PictureFieldProvider createInstance(final ContextManager contextManager) {
    final PictureFieldProvider instance = new PictureFieldProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PictureFieldProvider instance) {
    BasicTypeFieldProvider_registerFields(instance);
  }

  public native static void BasicTypeFieldProvider_registerFields(BasicTypeFieldProvider instance) /*-{
    instance.@org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.BasicTypeFieldProvider::registerFields()();
  }-*/;
}