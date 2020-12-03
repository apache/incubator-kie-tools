package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelector.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelectorView;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsValueConverter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<YearsMonthsSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(YearsMonthsSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { YearsMonthsSelector.class, Object.class, TypedValueSelector.class });
  }

  public YearsMonthsSelector createInstance(final ContextManager contextManager) {
    final View _view_0 = (YearsMonthsSelectorView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelectorView__quals__j_e_i_Any_j_e_i_Default");
    final YearsMonthsValueConverter _converter_1 = (YearsMonthsValueConverter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsValueConverter__quals__j_e_i_Any_j_e_i_Default");
    final YearsMonthsSelector instance = new YearsMonthsSelector(_view_0, _converter_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _converter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}