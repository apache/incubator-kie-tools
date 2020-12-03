package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScalableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.StunnerLienzoBoundsPanel;

public class Type_factory__o_k_w_c_s_c_w_c_ScalableLienzoPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<ScalableLienzoPanel> { public Type_factory__o_k_w_c_s_c_w_c_ScalableLienzoPanel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ScalableLienzoPanel.class, "Type_factory__o_k_w_c_s_c_w_c_ScalableLienzoPanel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ScalableLienzoPanel.class, Object.class });
  }

  public ScalableLienzoPanel createInstance(final ContextManager contextManager) {
    final StunnerLienzoBoundsPanel _panel_0 = (StunnerLienzoBoundsPanel) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default");
    final ScalableLienzoPanel instance = new ScalableLienzoPanel(_panel_0);
    registerDependentScopedReference(instance, _panel_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ScalableLienzoPanel instance) {
    instance.init();
  }
}