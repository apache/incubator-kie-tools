package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.AbstractSelectionAwareSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommandExecutedEvent;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_CutSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<CutSelectionSessionCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_CutSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CutSelectionSessionCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_CutSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CutSelectionSessionCommand.class, AbstractSelectionAwareSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public CutSelectionSessionCommand createInstance(final ContextManager contextManager) {
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<CutSelectionSessionCommandExecutedEvent> _commandExecutedEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CutSelectionSessionCommandExecutedEvent.class }, new Annotation[] { });
    final CutSelectionSessionCommand instance = new CutSelectionSessionCommand(_commandExecutedEvent_0, _sessionManager_1);
    registerDependentScopedReference(instance, _commandExecutedEvent_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        AbstractSelectionAwareSessionCommand_onCanvasSelectionEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasClearSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent", new AbstractCDIEventCallback<CanvasClearSelectionEvent>() {
      public void fireEvent(final CanvasClearSelectionEvent event) {
        AbstractSelectionAwareSessionCommand_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementsClearEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent", new AbstractCDIEventCallback<CanvasElementsClearEvent>() {
      public void fireEvent(final CanvasElementsClearEvent event) {
        AbstractSelectionAwareSessionCommand_onCanvasElementsClearEvent_CanvasElementsClearEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CutSelectionSessionCommand) instance, contextManager);
  }

  public void destroyInstanceHelper(final CutSelectionSessionCommand instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasClearSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementsClearEventSubscription", Subscription.class)).remove();
  }

  public native static void AbstractSelectionAwareSessionCommand_onCanvasSelectionEvent_CanvasSelectionEvent(AbstractSelectionAwareSessionCommand instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.AbstractSelectionAwareSessionCommand::onCanvasSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void AbstractSelectionAwareSessionCommand_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(AbstractSelectionAwareSessionCommand instance, CanvasClearSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.AbstractSelectionAwareSessionCommand::onCanvasClearSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasClearSelectionEvent;)(a0);
  }-*/;

  public native static void AbstractSelectionAwareSessionCommand_onCanvasElementsClearEvent_CanvasElementsClearEvent(AbstractSelectionAwareSessionCommand instance, CanvasElementsClearEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.AbstractSelectionAwareSessionCommand::onCanvasElementsClearEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementsClearEvent;)(a0);
  }-*/;
}