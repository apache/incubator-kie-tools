package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent;

public class Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<NotificationsObserver> { public Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NotificationsObserver.class, "Type_factory__o_k_w_c_s_c_w_n_NotificationsObserver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NotificationsObserver.class, Object.class });
  }

  public NotificationsObserver createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final NotificationsObserver instance = new NotificationsObserver(_translationService_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onGraphCommandExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent", new AbstractCDIEventCallback<CanvasCommandExecutedEvent>() {
      public void fireEvent(final CanvasCommandExecutedEvent event) {
        NotificationsObserver_onGraphCommandExecuted_CanvasCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasCommandUndoneEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent", new AbstractCDIEventCallback<CanvasCommandUndoneEvent>() {
      public void fireEvent(final CanvasCommandUndoneEvent event) {
        NotificationsObserver_onCanvasCommandUndoneEvent_CanvasCommandUndoneEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasValidationSuccessEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent", new AbstractCDIEventCallback<CanvasValidationSuccessEvent>() {
      public void fireEvent(final CanvasValidationSuccessEvent event) {
        NotificationsObserver_onCanvasValidationSuccessEvent_CanvasValidationSuccessEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasValidationFailEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent", new AbstractCDIEventCallback<CanvasValidationFailEvent>() {
      public void fireEvent(final CanvasValidationFailEvent event) {
        NotificationsObserver_onCanvasValidationFailEvent_CanvasValidationFailEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((NotificationsObserver) instance, contextManager);
  }

  public void destroyInstanceHelper(final NotificationsObserver instance, final ContextManager contextManager) {
    instance.destroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "onGraphCommandExecutedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasCommandUndoneEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasValidationSuccessEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasValidationFailEventSubscription", Subscription.class)).remove();
  }

  public native static void NotificationsObserver_onCanvasCommandUndoneEvent_CanvasCommandUndoneEvent(NotificationsObserver instance, CanvasCommandUndoneEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver::onCanvasCommandUndoneEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandUndoneEvent;)(a0);
  }-*/;

  public native static void NotificationsObserver_onCanvasValidationSuccessEvent_CanvasValidationSuccessEvent(NotificationsObserver instance, CanvasValidationSuccessEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver::onCanvasValidationSuccessEvent(Lorg/kie/workbench/common/stunner/core/client/validation/canvas/CanvasValidationSuccessEvent;)(a0);
  }-*/;

  public native static void NotificationsObserver_onGraphCommandExecuted_CanvasCommandExecutedEvent(NotificationsObserver instance, CanvasCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver::onGraphCommandExecuted(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandExecutedEvent;)(a0);
  }-*/;

  public native static void NotificationsObserver_onCanvasValidationFailEvent_CanvasValidationFailEvent(NotificationsObserver instance, CanvasValidationFailEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver::onCanvasValidationFailEvent(Lorg/kie/workbench/common/stunner/core/client/validation/canvas/CanvasValidationFailEvent;)(a0);
  }-*/;
}