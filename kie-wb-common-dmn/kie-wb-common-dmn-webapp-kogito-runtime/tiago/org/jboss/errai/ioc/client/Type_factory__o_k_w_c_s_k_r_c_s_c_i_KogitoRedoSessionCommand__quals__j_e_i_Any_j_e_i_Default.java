package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.RedoCommandHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoRedoSessionCommand;

public class Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoRedoSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoRedoSessionCommand> { public Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoRedoSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoRedoSessionCommand.class, "Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoRedoSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoRedoSessionCommand.class, RedoSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public KogitoRedoSessionCommand createInstance(final ContextManager contextManager) {
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_0 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final RedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> _redoCommandHandler_1 = (RedoCommandHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_RedoCommandHandler__quals__j_e_i_Any_j_e_i_Default");
    final KogitoRedoSessionCommand instance = new KogitoRedoSessionCommand(_sessionCommandManager_0, _redoCommandHandler_1);
    registerDependentScopedReference(instance, _redoCommandHandler_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCommandExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent", new AbstractCDIEventCallback<CanvasCommandExecutedEvent>() {
      public void fireEvent(final CanvasCommandExecutedEvent event) {
        RedoSessionCommand_onCommandExecuted_CanvasCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCommandUndoExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent", new AbstractCDIEventCallback<CanvasCommandUndoneEvent>() {
      public void fireEvent(final CanvasCommandUndoneEvent event) {
        RedoSessionCommand_onCommandUndoExecuted_CanvasCommandUndoneEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCurrentRegistryChangedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent", new AbstractCDIEventCallback<CurrentRegistryChangedEvent>() {
      public void fireEvent(final CurrentRegistryChangedEvent event) {
        RedoSessionCommand_onCurrentRegistryChanged_CurrentRegistryChangedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KogitoRedoSessionCommand) instance, contextManager);
  }

  public void destroyInstanceHelper(final KogitoRedoSessionCommand instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCommandExecutedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCommandUndoExecutedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCurrentRegistryChangedSubscription", Subscription.class)).remove();
  }

  public native static void RedoSessionCommand_onCommandUndoExecuted_CanvasCommandUndoneEvent(RedoSessionCommand instance, CanvasCommandUndoneEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand::onCommandUndoExecuted(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandUndoneEvent;)(a0);
  }-*/;

  public native static void RedoSessionCommand_onCommandExecuted_CanvasCommandExecutedEvent(RedoSessionCommand instance, CanvasCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand::onCommandExecuted(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandExecutedEvent;)(a0);
  }-*/;

  public native static void RedoSessionCommand_onCurrentRegistryChanged_CurrentRegistryChangedEvent(RedoSessionCommand instance, CurrentRegistryChangedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.RedoSessionCommand::onCurrentRegistryChanged(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CurrentRegistryChangedEvent;)(a0);
  }-*/;
}