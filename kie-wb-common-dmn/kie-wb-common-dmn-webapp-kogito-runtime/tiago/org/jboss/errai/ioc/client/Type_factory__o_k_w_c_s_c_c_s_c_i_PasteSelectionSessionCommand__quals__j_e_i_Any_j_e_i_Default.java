package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_PasteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<PasteSelectionSessionCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_PasteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PasteSelectionSessionCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_PasteSelectionSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PasteSelectionSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public PasteSelectionSessionCommand createInstance(final ContextManager contextManager) {
    final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> _canvasCommandFactoryInstance_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CanvasCommandFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SessionManager _sessionManager_4 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_0 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasSelectionEvent> _selectionEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final PasteSelectionSessionCommand instance = new PasteSelectionSessionCommand(_sessionCommandManager_0, _canvasCommandFactoryInstance_1, _selectionEvent_2, _definitionUtils_3, _sessionManager_4);
    registerDependentScopedReference(instance, _canvasCommandFactoryInstance_1);
    registerDependentScopedReference(instance, _selectionEvent_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCopySelectionCommandExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommandExecutedEvent", new AbstractCDIEventCallback<CopySelectionSessionCommandExecutedEvent>() {
      public void fireEvent(final CopySelectionSessionCommandExecutedEvent event) {
        PasteSelectionSessionCommand_onCopySelectionCommandExecuted_CopySelectionSessionCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.command.impl.CopySelectionSessionCommandExecutedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCutSelectionCommandExecutedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommandExecutedEvent", new AbstractCDIEventCallback<CutSelectionSessionCommandExecutedEvent>() {
      public void fireEvent(final CutSelectionSessionCommandExecutedEvent event) {
        PasteSelectionSessionCommand_onCutSelectionCommandExecuted_CutSelectionSessionCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.session.command.impl.CutSelectionSessionCommandExecutedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PasteSelectionSessionCommand) instance, contextManager);
  }

  public void destroyInstanceHelper(final PasteSelectionSessionCommand instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCopySelectionCommandExecutedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCutSelectionCommandExecutedSubscription", Subscription.class)).remove();
  }

  public native static void PasteSelectionSessionCommand_onCutSelectionCommandExecuted_CutSelectionSessionCommandExecutedEvent(PasteSelectionSessionCommand instance, CutSelectionSessionCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand::onCutSelectionCommandExecuted(Lorg/kie/workbench/common/stunner/core/client/session/command/impl/CutSelectionSessionCommandExecutedEvent;)(a0);
  }-*/;

  public native static void PasteSelectionSessionCommand_onCopySelectionCommandExecuted_CopySelectionSessionCommandExecutedEvent(PasteSelectionSessionCommand instance, CopySelectionSessionCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.session.command.impl.PasteSelectionSessionCommand::onCopySelectionCommandExecuted(Lorg/kie/workbench/common/stunner/core/client/session/command/impl/CopySelectionSessionCommandExecutedEvent;)(a0);
  }-*/;
}