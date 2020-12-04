package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.TypedValueComponentSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.generic.GenericSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.number.NumberSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.string.StringSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months.YearsMonthsSelector;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<TypedValueComponentSelector> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TypedValueComponentSelector.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_TypedValueComponentSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TypedValueComponentSelector.class, Object.class });
  }

  public TypedValueComponentSelector createInstance(final ContextManager contextManager) {
    final TimeSelector _timeSelector_6 = (TimeSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_TimeSelector__quals__j_e_i_Any_j_e_i_Default");
    final GenericSelector _genericSelector_0 = (GenericSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_g_GenericSelector__quals__j_e_i_Any_j_e_i_Default");
    final DayTimeSelector _dayTimeSelector_2 = (DayTimeSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DayTimeSelector__quals__j_e_i_Any_j_e_i_Default");
    final StringSelector _stringSelector_4 = (StringSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_s_StringSelector__quals__j_e_i_Any_j_e_i_Default");
    final YearsMonthsSelector _yearsMosSelector_3 = (YearsMonthsSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_y_m_YearsMonthsSelector__quals__j_e_i_Any_j_e_i_Default");
    final DateTimeSelector _dateTimeSelector_7 = (DateTimeSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeSelector__quals__j_e_i_Any_j_e_i_Default");
    final DateSelector _dateSelector_1 = (DateSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateSelector__quals__j_e_i_Any_j_e_i_Default");
    final NumberSelector _numberSelector_5 = (NumberSelector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_n_NumberSelector__quals__j_e_i_Any_j_e_i_Default");
    final TypedValueComponentSelector instance = new TypedValueComponentSelector(_genericSelector_0, _dateSelector_1, _dayTimeSelector_2, _yearsMosSelector_3, _stringSelector_4, _numberSelector_5, _timeSelector_6, _dateTimeSelector_7);
    registerDependentScopedReference(instance, _timeSelector_6);
    registerDependentScopedReference(instance, _genericSelector_0);
    registerDependentScopedReference(instance, _dayTimeSelector_2);
    registerDependentScopedReference(instance, _stringSelector_4);
    registerDependentScopedReference(instance, _yearsMosSelector_3);
    registerDependentScopedReference(instance, _dateTimeSelector_7);
    registerDependentScopedReference(instance, _dateSelector_1);
    registerDependentScopedReference(instance, _numberSelector_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}