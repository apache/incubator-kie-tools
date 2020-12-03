package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.canvas.ScrollableLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.canvas.StunnerLienzoBoundsPanel;

public class Type_factory__o_k_w_c_s_c_w_c_ScrollableLienzoPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<ScrollableLienzoPanel> { public Type_factory__o_k_w_c_s_c_w_c_ScrollableLienzoPanel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ScrollableLienzoPanel.class, "Type_factory__o_k_w_c_s_c_w_c_ScrollableLienzoPanel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ScrollableLienzoPanel.class, Object.class });
  }

  public ScrollableLienzoPanel createInstance(final ContextManager contextManager) {
    final StunnerLienzoBoundsPanel _panel_0 = (StunnerLienzoBoundsPanel) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default");
    final ScrollableLienzoPanel instance = new ScrollableLienzoPanel(_panel_0);
    registerDependentScopedReference(instance, _panel_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ScrollableLienzoPanel instance) {
    instance.init();
  }
}