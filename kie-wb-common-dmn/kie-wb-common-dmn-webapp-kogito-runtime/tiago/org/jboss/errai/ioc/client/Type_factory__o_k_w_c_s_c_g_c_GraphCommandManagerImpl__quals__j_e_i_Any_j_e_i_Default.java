package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.event.local.CommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.event.local.CommandUndoExecutedEvent;
import org.kie.workbench.common.stunner.core.command.event.local.IsCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;

public class Type_factory__o_k_w_c_s_c_g_c_GraphCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GraphCommandManagerImpl> { public Type_factory__o_k_w_c_s_c_g_c_GraphCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GraphCommandManagerImpl.class, "Type_factory__o_k_w_c_s_c_g_c_GraphCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GraphCommandManagerImpl.class, Object.class, GraphCommandManager.class, CommandManager.class });
  }

  public GraphCommandManagerImpl createInstance(final ContextManager contextManager) {
    final Event<CommandExecutedEvent> _commandExecutedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CommandExecutedEvent.class }, new Annotation[] { });
    final Event<IsCommandAllowedEvent> _isCommandAllowedEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { IsCommandAllowedEvent.class }, new Annotation[] { });
    final Event<CommandUndoExecutedEvent> _commandUndoExecutedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CommandUndoExecutedEvent.class }, new Annotation[] { });
    final GraphCommandManagerImpl instance = new GraphCommandManagerImpl(_isCommandAllowedEvent_0, _commandExecutedEvent_1, _commandUndoExecutedEvent_2);
    registerDependentScopedReference(instance, _commandExecutedEvent_1);
    registerDependentScopedReference(instance, _isCommandAllowedEvent_0);
    registerDependentScopedReference(instance, _commandUndoExecutedEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}