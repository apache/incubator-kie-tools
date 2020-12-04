package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimeSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimeSelector.class, Object.class, TypedValueSelector.class });
  }

  public TimeSelector createInstance(final ContextManager contextManager) {
    final TimeValueFormatter _formatter_1 = (TimeValueFormatter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (TimeSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final TimeSelector instance = new TimeSelector(_view_0, _formatter_1);
    registerDependentScopedReference(instance, _formatter_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}