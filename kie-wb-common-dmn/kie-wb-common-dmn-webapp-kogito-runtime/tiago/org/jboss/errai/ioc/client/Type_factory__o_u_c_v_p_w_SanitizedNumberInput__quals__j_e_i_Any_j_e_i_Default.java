package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.NumberInput;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.SanitizedNumberInput;

public class Type_factory__o_u_c_v_p_w_SanitizedNumberInput__quals__j_e_i_Any_j_e_i_Default extends Factory<SanitizedNumberInput> { public Type_factory__o_u_c_v_p_w_SanitizedNumberInput__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SanitizedNumberInput.class, "Type_factory__o_u_c_v_p_w_SanitizedNumberInput__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SanitizedNumberInput.class, Object.class, IsElement.class });
  }

  public SanitizedNumberInput createInstance(final ContextManager contextManager) {
    final SanitizedNumberInput instance = new SanitizedNumberInput();
    setIncompleteInstance(instance);
    final NumberInput SanitizedNumberInput_input = (NumberInput) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_NumberInput__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SanitizedNumberInput_input);
    SanitizedNumberInput_NumberInput_input(instance, SanitizedNumberInput_input);
    setIncompleteInstance(null);
    return instance;
  }

  native static NumberInput SanitizedNumberInput_NumberInput_input(SanitizedNumberInput instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.SanitizedNumberInput::input;
  }-*/;

  native static void SanitizedNumberInput_NumberInput_input(SanitizedNumberInput instance, NumberInput value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.SanitizedNumberInput::input = value;
  }-*/;
}