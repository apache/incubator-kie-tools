package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.command.CommandRequestLifecycle;
import org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle;

public class Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default extends Factory<MouseRequestLifecycle> { public Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MouseRequestLifecycle.class, "Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MouseRequestLifecycle.class, Object.class, CommandRequestLifecycle.class });
  }

  public MouseRequestLifecycle createInstance(final ContextManager contextManager) {
    final MouseRequestLifecycle instance = new MouseRequestLifecycle();
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onMouseDownSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent", new AbstractCDIEventCallback<CanvasMouseDownEvent>() {
      public void fireEvent(final CanvasMouseDownEvent event) {
        MouseRequestLifecycle_onMouseDown_CanvasMouseDownEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent []";
      }
    }));
    thisInstance.setReference(instance, "onMouseUpSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent", new AbstractCDIEventCallback<CanvasMouseUpEvent>() {
      public void fireEvent(final CanvasMouseUpEvent event) {
        MouseRequestLifecycle_onMouseUp_CanvasMouseUpEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MouseRequestLifecycle) instance, contextManager);
  }

  public void destroyInstanceHelper(final MouseRequestLifecycle instance, final ContextManager contextManager) {
    instance.destroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "onMouseDownSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onMouseUpSubscription", Subscription.class)).remove();
  }

  public native static void MouseRequestLifecycle_onMouseUp_CanvasMouseUpEvent(MouseRequestLifecycle instance, CanvasMouseUpEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle::onMouseUp(Lorg/kie/workbench/common/stunner/core/client/canvas/event/mouse/CanvasMouseUpEvent;)(a0);
  }-*/;

  public native static void MouseRequestLifecycle_onMouseDown_CanvasMouseDownEvent(MouseRequestLifecycle instance, CanvasMouseDownEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle::onMouseDown(Lorg/kie/workbench/common/stunner/core/client/canvas/event/mouse/CanvasMouseDownEvent;)(a0);
  }-*/;
}