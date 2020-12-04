package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.appformer.client.stateControl.registry.Registry;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManagerImpl;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultEditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ManagedSession;
import org.kie.workbench.common.stunner.core.command.Command;

public class Type_factory__o_k_w_c_s_c_c_s_i_DefaultEditorSession__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultEditorSession> { public Type_factory__o_k_w_c_s_c_c_s_i_DefaultEditorSession__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultEditorSession.class, "Type_factory__o_k_w_c_s_c_c_s_i_DefaultEditorSession__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultEditorSession.class, EditorSession.class, AbstractSession.class, Object.class, ClientSession.class });
  }

  public DefaultEditorSession createInstance(final ContextManager contextManager) {
    final ManagedSession _session_0 = (ManagedSession) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_i_ManagedSession__quals__j_e_i_Any_j_e_i_Default");
    final Registry<Command<AbstractCanvasHandler, CanvasViolation>> _commandRegistry_3 = (Registry) contextManager.getInstance("Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandManager<AbstractCanvasHandler> _canvasCommandManager_1 = (CanvasCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_2 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<RegisterChangedEvent> _registerChangedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RegisterChangedEvent.class }, new Annotation[] { });
    final DefaultEditorSession instance = new DefaultEditorSession(_session_0, _canvasCommandManager_1, _sessionCommandManager_2, _commandRegistry_3, _registerChangedEvent_4);
    registerDependentScopedReference(instance, _session_0);
    registerDependentScopedReference(instance, _commandRegistry_3);
    registerDependentScopedReference(instance, _canvasCommandManager_1);
    registerDependentScopedReference(instance, _registerChangedEvent_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DefaultEditorSession instance) {
    instance.constructInstance();
  }
}