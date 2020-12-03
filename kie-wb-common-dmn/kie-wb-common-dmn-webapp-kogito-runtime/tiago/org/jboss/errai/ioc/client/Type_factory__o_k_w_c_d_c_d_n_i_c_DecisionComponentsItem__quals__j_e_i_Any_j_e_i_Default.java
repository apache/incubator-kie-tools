package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItem;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItem.View;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItemView;

public class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItem__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponentsItem> { public Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionComponentsItem.class, "Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionComponentsItem.class, Object.class });
  }

  public DecisionComponentsItem createInstance(final ContextManager contextManager) {
    final View _view_0 = (DecisionComponentsItemView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsItemView__quals__j_e_i_Any_j_e_i_Default");
    final DecisionComponentsItem instance = new DecisionComponentsItem(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DecisionComponentsItem instance) {
    instance.init();
  }
}