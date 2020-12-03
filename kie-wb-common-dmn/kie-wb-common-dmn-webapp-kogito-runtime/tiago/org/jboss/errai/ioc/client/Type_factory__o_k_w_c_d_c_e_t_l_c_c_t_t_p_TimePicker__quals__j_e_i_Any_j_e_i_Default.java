package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePickerView;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePicker__quals__j_e_i_Any_j_e_i_Default extends Factory<TimePicker> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePicker__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimePicker.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePicker__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimePicker.class, Object.class });
  }

  public TimePicker createInstance(final ContextManager contextManager) {
    final View _view_0 = (TimePickerView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimePickerView__quals__j_e_i_Any_j_e_i_Default");
    final TimePicker instance = new TimePicker(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}