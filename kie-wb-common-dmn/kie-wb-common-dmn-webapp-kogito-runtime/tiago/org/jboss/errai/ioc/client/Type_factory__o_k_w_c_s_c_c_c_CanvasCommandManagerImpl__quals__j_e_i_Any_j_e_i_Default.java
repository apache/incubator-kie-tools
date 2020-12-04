package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.command.CommandManager;

public class Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasCommandManagerImpl> { public Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasCommandManagerImpl.class, "Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasCommandManagerImpl.class, Object.class, CanvasCommandManager.class, CommandManager.class });
  }

  public CanvasCommandManagerImpl createInstance(final ContextManager contextManager) {
    final Event<CanvasCommandAllowedEvent> _isCanvasCommandAllowedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasCommandAllowedEvent.class }, new Annotation[] { });
    final Event<CanvasCommandExecutedEvent> _canvasCommandExecutedEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasCommandExecutedEvent.class }, new Annotation[] { });
    final ClientFactoryManager _clientFactoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasCommandUndoneEvent> _canvasUndoCommandExecutedEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasCommandUndoneEvent.class }, new Annotation[] { });
    final CanvasCommandManagerImpl instance = new CanvasCommandManagerImpl(_clientFactoryManager_0, _isCanvasCommandAllowedEvent_1, _canvasCommandExecutedEvent_2, _canvasUndoCommandExecutedEvent_3);
    registerDependentScopedReference(instance, _isCanvasCommandAllowedEvent_1);
    registerDependentScopedReference(instance, _canvasCommandExecutedEvent_2);
    registerDependentScopedReference(instance, _canvasUndoCommandExecutedEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}