package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeValueConverter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimeSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateTimeSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateTimeSelector.class, Object.class, TypedValueSelector.class });
  }

  public DateTimeSelector createInstance(final ContextManager contextManager) {
    final View _view_0 = (DateTimeSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final DateTimeValueConverter _converter_1 = (DateTimeValueConverter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeValueConverter__quals__j_e_i_Any_j_e_i_Default");
    final DateTimeSelector instance = new DateTimeSelector(_view_0, _converter_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _converter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}