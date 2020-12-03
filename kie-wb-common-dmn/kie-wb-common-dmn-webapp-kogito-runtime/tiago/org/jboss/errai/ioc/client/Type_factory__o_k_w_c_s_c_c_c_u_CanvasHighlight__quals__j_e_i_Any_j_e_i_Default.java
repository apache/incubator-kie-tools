package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent;

public class Type_factory__o_k_w_c_s_c_c_c_u_CanvasHighlight__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasHighlight> { public Type_factory__o_k_w_c_s_c_c_c_u_CanvasHighlight__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasHighlight.class, "Type_factory__o_k_w_c_s_c_c_c_u_CanvasHighlight__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasHighlight.class, Object.class });
  }

  public CanvasHighlight createInstance(final ContextManager contextManager) {
    final CanvasHighlight instance = new CanvasHighlight();
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasUnhighlightEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent", new AbstractCDIEventCallback<CanvasUnhighlightEvent>() {
      public void fireEvent(final CanvasUnhighlightEvent event) {
        CanvasHighlight_onCanvasUnhighlightEvent_CanvasUnhighlightEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.util.CanvasUnhighlightEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CanvasHighlight) instance, contextManager);
  }

  public void destroyInstanceHelper(final CanvasHighlight instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasUnhighlightEventSubscription", Subscription.class)).remove();
  }

  public native static void CanvasHighlight_onCanvasUnhighlightEvent_CanvasUnhighlightEvent(CanvasHighlight instance, CanvasUnhighlightEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight::onCanvasUnhighlightEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/util/CanvasUnhighlightEvent;)(a0);
  }-*/;
}