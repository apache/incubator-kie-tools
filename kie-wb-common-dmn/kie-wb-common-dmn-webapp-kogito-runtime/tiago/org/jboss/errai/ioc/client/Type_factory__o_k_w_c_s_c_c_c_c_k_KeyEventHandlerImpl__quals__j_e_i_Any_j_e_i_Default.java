package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandlerImpl;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;

public class Type_factory__o_k_w_c_s_c_c_c_c_k_KeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyEventHandlerImpl> { public Type_factory__o_k_w_c_s_c_c_c_c_k_KeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KeyEventHandlerImpl.class, "Type_factory__o_k_w_c_s_c_c_c_c_k_KeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KeyEventHandlerImpl.class, Object.class, KeyEventHandler.class });
  }

  public KeyEventHandlerImpl createInstance(final ContextManager contextManager) {
    final KeyEventHandlerImpl instance = new KeyEventHandlerImpl();
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onKeyUpEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent", new AbstractCDIEventCallback<KeyUpEvent>() {
      public void fireEvent(final KeyUpEvent event) {
        instance.onKeyUpEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent []";
      }
    }));
    thisInstance.setReference(instance, "onKeyDownEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent", new AbstractCDIEventCallback<KeyDownEvent>() {
      public void fireEvent(final KeyDownEvent event) {
        KeyEventHandlerImpl_onKeyDownEvent_KeyDownEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KeyEventHandlerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final KeyEventHandlerImpl instance, final ContextManager contextManager) {
    instance.clear();
    ((Subscription) thisInstance.getReferenceAs(instance, "onKeyUpEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onKeyDownEventSubscription", Subscription.class)).remove();
  }

  public native static void KeyEventHandlerImpl_onKeyDownEvent_KeyDownEvent(KeyEventHandlerImpl instance, KeyDownEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandlerImpl::onKeyDownEvent(Lorg/kie/workbench/common/stunner/core/client/event/keyboard/KeyDownEvent;)(a0);
  }-*/;
}