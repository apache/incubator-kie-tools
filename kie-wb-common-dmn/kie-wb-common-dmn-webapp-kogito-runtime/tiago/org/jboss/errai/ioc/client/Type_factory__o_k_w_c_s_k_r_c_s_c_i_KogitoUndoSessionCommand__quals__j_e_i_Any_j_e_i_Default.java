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
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoUndoSessionCommand;

public class Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoUndoSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoUndoSessionCommand> { public Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoUndoSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoUndoSessionCommand.class, "Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoUndoSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoUndoSessionCommand.class, UndoSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public KogitoUndoSessionCommand createInstance(final ContextManager contextManager) {
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_0 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final KogitoUndoSessionCommand instance = new KogitoUndoSessionCommand(_sessionCommandManager_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCommandAddedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent", new AbstractCDIEventCallback<RegisterChangedEvent>() {
      public void fireEvent(final RegisterChangedEvent event) {
        UndoSessionCommand_onCommandAdded_RegisterChangedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCurrentRegistryChangedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent", new AbstractCDIEventCallback<CurrentRegistryChangedEvent>() {
      public void fireEvent(final CurrentRegistryChangedEvent event) {
        UndoSessionCommand_onCurrentRegistryChanged_CurrentRegistryChangedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KogitoUndoSessionCommand) instance, contextManager);
  }

  public void destroyInstanceHelper(final KogitoUndoSessionCommand instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCommandAddedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCurrentRegistryChangedSubscription", Subscription.class)).remove();
  }

  public native static void UndoSessionCommand_onCurrentRegistryChanged_CurrentRegistryChangedEvent(UndoSessionCommand instance, CurrentRegistryChangedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand::onCurrentRegistryChanged(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CurrentRegistryChangedEvent;)(a0);
  }-*/;

  public native static void UndoSessionCommand_onCommandAdded_RegisterChangedEvent(UndoSessionCommand instance, RegisterChangedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.UndoSessionCommand::onCommandAdded(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/RegisterChangedEvent;)(a0);
  }-*/;
}