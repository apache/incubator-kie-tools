package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionCardinalityStateHandler;
import org.kie.workbench.common.stunner.client.widgets.views.session.EmptyStateView;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionCardinalityStateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionCardinalityStateHandler> { public Type_factory__o_k_w_c_s_c_w_p_s_i_SessionCardinalityStateHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionCardinalityStateHandler.class, "Type_factory__o_k_w_c_s_c_w_p_s_i_SessionCardinalityStateHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionCardinalityStateHandler.class, Object.class });
  }

  public SessionCardinalityStateHandler createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final EmptyStateView _emptyStateView_1 = (EmptyStateView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_s_EmptyStateView__quals__j_e_i_Any_j_e_i_Default");
    final SessionCardinalityStateHandler instance = new SessionCardinalityStateHandler(_translationService_0, _emptyStateView_1);
    registerDependentScopedReference(instance, _emptyStateView_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCommandExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent", new AbstractCDIEventCallback<CanvasCommandExecutedEvent>() {
      public void fireEvent(final CanvasCommandExecutedEvent event) {
        SessionCardinalityStateHandler_onCommandExecuted_CanvasCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCommandUndoExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent", new AbstractCDIEventCallback<CanvasCommandUndoneEvent>() {
      public void fireEvent(final CanvasCommandUndoneEvent event) {
        SessionCardinalityStateHandler_onCommandUndoExecuted_CanvasCommandUndoneEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SessionCardinalityStateHandler) instance, contextManager);
  }

  public void destroyInstanceHelper(final SessionCardinalityStateHandler instance, final ContextManager contextManager) {
    instance.destroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCommandExecutedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCommandUndoExecutedSubscription", Subscription.class)).remove();
  }

  public native static void SessionCardinalityStateHandler_onCommandUndoExecuted_CanvasCommandUndoneEvent(SessionCardinalityStateHandler instance, CanvasCommandUndoneEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionCardinalityStateHandler::onCommandUndoExecuted(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandUndoneEvent;)(a0);
  }-*/;

  public native static void SessionCardinalityStateHandler_onCommandExecuted_CanvasCommandExecutedEvent(SessionCardinalityStateHandler instance, CanvasCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionCardinalityStateHandler::onCommandExecuted(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandExecutedEvent;)(a0);
  }-*/;
}