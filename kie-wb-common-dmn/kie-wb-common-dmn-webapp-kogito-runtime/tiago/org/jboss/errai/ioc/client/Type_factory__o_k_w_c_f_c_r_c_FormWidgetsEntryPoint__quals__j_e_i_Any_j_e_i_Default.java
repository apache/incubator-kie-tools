package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.common.rendering.client.FormWidgetsEntryPoint;

public class Type_factory__o_k_w_c_f_c_r_c_FormWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<FormWidgetsEntryPoint> { public Type_factory__o_k_w_c_f_c_r_c_FormWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormWidgetsEntryPoint.class, "Type_factory__o_k_w_c_f_c_r_c_FormWidgetsEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { FormWidgetsEntryPoint.class, Object.class });
  }

  public FormWidgetsEntryPoint createInstance(final ContextManager contextManager) {
    final FormWidgetsEntryPoint instance = new FormWidgetsEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final FormWidgetsEntryPoint instance) {
    FormWidgetsEntryPoint_init(instance);
  }

  public native static void FormWidgetsEntryPoint_init(FormWidgetsEntryPoint instance) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.FormWidgetsEntryPoint::init()();
  }-*/;
}