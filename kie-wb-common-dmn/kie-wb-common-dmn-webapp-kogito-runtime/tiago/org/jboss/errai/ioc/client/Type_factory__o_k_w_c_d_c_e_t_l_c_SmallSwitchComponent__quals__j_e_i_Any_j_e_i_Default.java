package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponentView;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<SmallSwitchComponent> { public Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SmallSwitchComponent.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SmallSwitchComponent.class, Object.class });
  }

  public SmallSwitchComponent createInstance(final ContextManager contextManager) {
    final View _view_0 = (SmallSwitchComponentView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_c_SmallSwitchComponentView__quals__j_e_i_Any_j_e_i_Default");
    final SmallSwitchComponent instance = new SmallSwitchComponent(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}