package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages.View;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessagesView;

public class Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default extends Factory<FlashMessages> { public Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FlashMessages.class, "Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FlashMessages.class, Object.class });
  }

  public FlashMessages createInstance(final ContextManager contextManager) {
    final View _view_0 = (FlashMessagesView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_c_m_FlashMessagesView__quals__j_e_i_Any_j_e_i_Default");
    final FlashMessages instance = new FlashMessages(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onFlashMessageEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage", new AbstractCDIEventCallback<FlashMessage>() {
      public void fireEvent(final FlashMessage event) {
        instance.onFlashMessageEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FlashMessages) instance, contextManager);
  }

  public void destroyInstanceHelper(final FlashMessages instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onFlashMessageEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final FlashMessages instance) {
    FlashMessages_init(instance);
  }

  public native static void FlashMessages_init(FlashMessages instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages::init()();
  }-*/;
}