package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeValueFormatter> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimeValueFormatter.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeValueFormatter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimeValueFormatter.class, Object.class });
  }

  public TimeValueFormatter createInstance(final ContextManager contextManager) {
    final TimeZoneProvider _timeZoneProvider_0 = (TimeZoneProvider) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default");
    final TimeValueFormatter instance = new TimeValueFormatter(_timeZoneProvider_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}