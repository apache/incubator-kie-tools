package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.LienzoCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvasView;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactory;
import org.kie.workbench.common.stunner.client.lienzo.wires.WiresManagerFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasDrawnEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.listener.HasCanvasListeners;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;

public class Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvas__quals__j_e_i_Any_j_e_i_Default extends Factory<WiresCanvas> { public Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvas__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WiresCanvas.class, "Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvas__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WiresCanvas.class, LienzoCanvas.class, AbstractCanvas.class, Object.class, Canvas.class, HasEventHandlers.class, HasCanvasListeners.class });
  }

  public WiresCanvas createInstance(final ContextManager contextManager) {
    final WiresCanvasView _view_6 = (WiresCanvasView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_w_WiresCanvasView__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasFocusedEvent> _canvasFocusedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasFocusedEvent.class }, new Annotation[] { });
    final Event<CanvasClearEvent> _canvasClearEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasClearEvent.class }, new Annotation[] { });
    final Event<CanvasShapeAddedEvent> _canvasShapeAddedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasShapeAddedEvent.class }, new Annotation[] { });
    final WiresManagerFactory _wiresManagerFactory_5 = (WiresManagerFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_w_WiresManagerFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasDrawnEvent> _canvasDrawnEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasDrawnEvent.class }, new Annotation[] { });
    final Event<CanvasShapeRemovedEvent> _canvasShapeRemovedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasShapeRemovedEvent.class }, new Annotation[] { });
    final WiresCanvas instance = new WiresCanvas(_canvasClearEvent_0, _canvasShapeAddedEvent_1, _canvasShapeRemovedEvent_2, _canvasDrawnEvent_3, _canvasFocusedEvent_4, _wiresManagerFactory_5, _view_6);
    registerDependentScopedReference(instance, _view_6);
    registerDependentScopedReference(instance, _canvasFocusedEvent_4);
    registerDependentScopedReference(instance, _canvasClearEvent_0);
    registerDependentScopedReference(instance, _canvasShapeAddedEvent_1);
    registerDependentScopedReference(instance, _canvasDrawnEvent_3);
    registerDependentScopedReference(instance, _canvasShapeRemovedEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}