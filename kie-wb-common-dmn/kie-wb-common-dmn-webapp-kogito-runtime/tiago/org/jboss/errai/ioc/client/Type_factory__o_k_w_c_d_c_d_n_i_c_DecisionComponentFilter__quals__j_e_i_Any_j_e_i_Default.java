package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentFilter;

public class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentFilter__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentFilter> { public Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentFilter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionComponentFilter.class, "Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentFilter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionComponentFilter.class, Object.class });
  }

  public DecisionComponentFilter createInstance(final ContextManager contextManager) {
    final DecisionComponentFilter instance = new DecisionComponentFilter();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}