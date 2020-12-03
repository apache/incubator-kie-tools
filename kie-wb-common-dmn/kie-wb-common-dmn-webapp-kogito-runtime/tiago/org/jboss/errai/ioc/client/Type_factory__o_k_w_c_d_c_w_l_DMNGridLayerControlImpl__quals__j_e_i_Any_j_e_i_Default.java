package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayerControl;
import org.kie.workbench.common.dmn.client.widgets.layer.DMNGridLayerControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;

public class Type_factory__o_k_w_c_d_c_w_l_DMNGridLayerControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridLayerControlImpl> { public Type_factory__o_k_w_c_d_c_w_l_DMNGridLayerControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNGridLayerControlImpl.class, "Type_factory__o_k_w_c_d_c_w_l_DMNGridLayerControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGridLayerControlImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, DMNGridLayerControl.class, SessionAware.class });
  }

  public DMNGridLayerControlImpl createInstance(final ContextManager contextManager) {
    final DMNGridLayerControlImpl instance = new DMNGridLayerControlImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}