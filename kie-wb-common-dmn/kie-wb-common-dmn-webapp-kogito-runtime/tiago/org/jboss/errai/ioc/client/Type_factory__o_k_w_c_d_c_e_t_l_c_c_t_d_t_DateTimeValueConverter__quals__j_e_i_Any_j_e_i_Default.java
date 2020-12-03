package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time.DateTimeValueConverter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeValueConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<DateTimeValueConverter> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeValueConverter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateTimeValueConverter.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_t_DateTimeValueConverter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateTimeValueConverter.class, Object.class });
  }

  public DateTimeValueConverter createInstance(final ContextManager contextManager) {
    final DateValueFormatter _dateValueFormatter_0 = (DateValueFormatter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default");
    final TimeValueFormatter _timeValueFormatter_1 = (TimeValueFormatter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default");
    final DateTimeValueConverter instance = new DateTimeValueConverter(_dateValueFormatter_0, _timeValueFormatter_1);
    registerDependentScopedReference(instance, _dateValueFormatter_0);
    registerDependentScopedReference(instance, _timeValueFormatter_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}