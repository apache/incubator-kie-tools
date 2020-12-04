package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ClearSessionCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_ClearSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ClearSessionCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_ClearSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClearSessionCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_ClearSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClearSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public ClearSessionCommand createInstance(final ContextManager contextManager) {
    final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> _canvasCommandFactoryInstance_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CanvasCommandFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final Event<ClearSessionCommandExecutedEvent> _commandExecutedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ClearSessionCommandExecutedEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_1 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final ClearSessionCommand instance = new ClearSessionCommand(_canvasCommandFactoryInstance_0, _sessionCommandManager_1, _commandExecutedEvent_2, _definitionUtils_3);
    registerDependentScopedReference(instance, _canvasCommandFactoryInstance_0);
    registerDependentScopedReference(instance, _commandExecutedEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}