package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter.View;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView;

public class Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorTreePresenter> { public Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreePresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorTreePresenter.class, "Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreePresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorTreePresenter.class, Object.class });
  }

  public DecisionNavigatorTreePresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (DecisionNavigatorTreeView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorTreePresenter instance = new DecisionNavigatorTreePresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DecisionNavigatorTreePresenter instance) {
    DecisionNavigatorTreePresenter_setup(instance);
  }

  public native static void DecisionNavigatorTreePresenter_setup(DecisionNavigatorTreePresenter instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter::setup()();
  }-*/;
}