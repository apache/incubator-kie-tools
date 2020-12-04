package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeValueConverter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<DayTimeSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DayTimeSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DayTimeSelector.class, Object.class, TypedValueSelector.class });
  }

  public DayTimeSelector createInstance(final ContextManager contextManager) {
    final DayTimeValueConverter _converter_1 = (DayTimeValueConverter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeValueConverter__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DayTimeSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final DayTimeSelector instance = new DayTimeSelector(_view_0, _converter_1);
    registerDependentScopedReference(instance, _converter_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}