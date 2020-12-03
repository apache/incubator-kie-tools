package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.FormGroup;

public class Type_factory__o_u_c_v_p_w_FormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGroup> { public Type_factory__o_u_c_v_p_w_FormGroup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormGroup.class, "Type_factory__o_u_c_v_p_w_FormGroup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormGroup.class, Object.class, IsElement.class });
  }

  public FormGroup createInstance(final ContextManager contextManager) {
    final FormGroup instance = new FormGroup();
    setIncompleteInstance(instance);
    final Div FormGroup_div = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FormGroup_div);
    FormGroup_Div_div(instance, FormGroup_div);
    setIncompleteInstance(null);
    return instance;
  }

  native static Div FormGroup_Div_div(FormGroup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.FormGroup::div;
  }-*/;

  native static void FormGroup_Div_div(FormGroup instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.FormGroup::div = value;
  }-*/;
}