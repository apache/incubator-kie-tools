package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.widgets.layer.MousePanMediatorControl;
import org.kie.workbench.common.dmn.client.widgets.layer.MousePanMediatorControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;

public class Type_factory__o_k_w_c_d_c_w_l_MousePanMediatorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<MousePanMediatorControlImpl> { public Type_factory__o_k_w_c_d_c_w_l_MousePanMediatorControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MousePanMediatorControlImpl.class, "Type_factory__o_k_w_c_d_c_w_l_MousePanMediatorControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MousePanMediatorControlImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, MousePanMediatorControl.class, SessionAware.class });
  }

  public MousePanMediatorControlImpl createInstance(final ContextManager contextManager) {
    final MousePanMediatorControlImpl instance = new MousePanMediatorControlImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}