package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateValueFormatter;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default extends Factory<DateValueFormatter> { public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateValueFormatter.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_d_DateValueFormatter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateValueFormatter.class, Object.class });
  }

  public DateValueFormatter createInstance(final ContextManager contextManager) {
    final DateValueFormatter instance = new DateValueFormatter();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}