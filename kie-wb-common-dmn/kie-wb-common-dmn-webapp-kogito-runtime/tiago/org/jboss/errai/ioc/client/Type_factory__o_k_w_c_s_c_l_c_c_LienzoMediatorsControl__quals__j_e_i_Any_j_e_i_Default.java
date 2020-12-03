package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMediatorsControl;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoPanelMediators;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent;

public class Type_factory__o_k_w_c_s_c_l_c_c_LienzoMediatorsControl__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoMediatorsControl> { public Type_factory__o_k_w_c_s_c_l_c_c_LienzoMediatorsControl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoMediatorsControl.class, "Type_factory__o_k_w_c_s_c_l_c_c_LienzoMediatorsControl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoMediatorsControl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, MediatorsControl.class, CanvasControl.class });
  }

  public LienzoMediatorsControl createInstance(final ContextManager contextManager) {
    final LienzoPanelMediators _mediators_0 = (LienzoPanelMediators) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_m_LienzoPanelMediators__quals__j_e_i_Any_j_e_i_Default");
    final LienzoMediatorsControl instance = new LienzoMediatorsControl(_mediators_0);
    registerDependentScopedReference(instance, _mediators_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasFocusedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent", new AbstractCDIEventCallback<CanvasFocusedEvent>() {
      public void fireEvent(final CanvasFocusedEvent event) {
        LienzoMediatorsControl_onCanvasFocusedEvent_CanvasFocusedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.CanvasFocusedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasLostFocusEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent", new AbstractCDIEventCallback<CanvasLostFocusEvent>() {
      public void fireEvent(final CanvasLostFocusEvent event) {
        LienzoMediatorsControl_onCanvasLostFocusEvent_CanvasLostFocusEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.CanvasLostFocusEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoMediatorsControl) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoMediatorsControl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasFocusedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasLostFocusEventSubscription", Subscription.class)).remove();
  }

  public native static void LienzoMediatorsControl_onCanvasLostFocusEvent_CanvasLostFocusEvent(LienzoMediatorsControl instance, CanvasLostFocusEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMediatorsControl::onCanvasLostFocusEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/CanvasLostFocusEvent;)(a0);
  }-*/;

  public native static void LienzoMediatorsControl_onCanvasFocusedEvent_CanvasFocusedEvent(LienzoMediatorsControl instance, CanvasFocusedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMediatorsControl::onCanvasFocusedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/CanvasFocusedEvent;)(a0);
  }-*/;
}