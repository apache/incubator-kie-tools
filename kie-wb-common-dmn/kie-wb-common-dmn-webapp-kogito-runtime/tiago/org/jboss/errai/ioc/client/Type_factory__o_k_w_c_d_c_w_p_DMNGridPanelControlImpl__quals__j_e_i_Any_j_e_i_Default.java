package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelControl;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanelControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;

public class Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridPanelControlImpl> { public Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNGridPanelControlImpl.class, "Type_factory__o_k_w_c_d_c_w_p_DMNGridPanelControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGridPanelControlImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, DMNGridPanelControl.class, SessionAware.class });
  }

  public DMNGridPanelControlImpl createInstance(final ContextManager contextManager) {
    final DMNGridPanelControlImpl instance = new DMNGridPanelControlImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}