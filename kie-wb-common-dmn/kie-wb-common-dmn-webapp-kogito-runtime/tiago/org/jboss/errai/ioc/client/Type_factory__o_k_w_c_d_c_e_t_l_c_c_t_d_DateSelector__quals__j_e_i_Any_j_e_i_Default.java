package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<DateSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateSelector.class, Object.class, TypedValueSelector.class });
  }

  public DateSelector createInstance(final ContextManager contextManager) {
    final DateValueFormatter _dateValueFormatter_1 = (DateValueFormatter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (DateSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final DateSelector instance = new DateSelector(_view_0, _dateValueFormatter_1);
    registerDependentScopedReference(instance, _dateValueFormatter_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}