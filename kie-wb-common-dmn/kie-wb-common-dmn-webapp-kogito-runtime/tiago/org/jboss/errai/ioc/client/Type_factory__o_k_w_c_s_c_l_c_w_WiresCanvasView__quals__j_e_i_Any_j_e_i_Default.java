package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresLayer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas.CanvasView;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasView;

public class Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvasView__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresCanvasView> { public Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvasView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WiresCanvasView.class, "Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvasView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WiresCanvasView.class, LienzoCanvasView.class, AbstractCanvasView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, CanvasView.class, ProvidesResize.class, RequiresResize.class });
  }

  public WiresCanvasView createInstance(final ContextManager contextManager) {
    final WiresLayer _layer_0 = (WiresLayer) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_w_WiresLayer__quals__j_e_i_Any_j_e_i_Default");
    final WiresCanvasView instance = new WiresCanvasView(_layer_0);
    registerDependentScopedReference(instance, _layer_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final WiresCanvasView instance) {
    instance.init();
  }
}