package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.appformer.client.stateControl.registry.Registry;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.session.DMNEditorSession;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.session.RegistryProvider;
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

public class Type_factory__o_k_w_c_d_c_s_DMNEditorSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNEditorSession> { public Type_factory__o_k_w_c_d_c_s_DMNEditorSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNEditorSession.class, "Type_factory__o_k_w_c_d_c_s_DMNEditorSession__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditorSession.class, DefaultEditorSession.class, EditorSession.class, AbstractSession.class, Object.class, ClientSession.class, DMNSession.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNEditorSession createInstance(final ContextManager contextManager) {
    final ManagedSession _session_0 = (ManagedSession) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_i_ManagedSession__quals__j_e_i_Any_j_e_i_Default");
    final RegistryProvider _registryProvider_5 = (RegistryProvider) contextManager.getInstance("Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default");
    final Registry<Command<AbstractCanvasHandler, CanvasViolation>> _commandRegistry_3 = (Registry) contextManager.getInstance("Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default");
    final Event<RegisterChangedEvent> _registerChangedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { RegisterChangedEvent.class }, new Annotation[] { });
    final CanvasCommandManager<AbstractCanvasHandler> _canvasCommandManager_1 = (CanvasCommandManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_CanvasCommandManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_2 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditorSession instance = new DMNEditorSession(_session_0, _canvasCommandManager_1, _sessionCommandManager_2, _commandRegistry_3, _registerChangedEvent_4, _registryProvider_5);
    registerDependentScopedReference(instance, _session_0);
    registerDependentScopedReference(instance, _commandRegistry_3);
    registerDependentScopedReference(instance, _registerChangedEvent_4);
    registerDependentScopedReference(instance, _canvasCommandManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNEditorSession instance) {
    instance.constructInstance();
  }
}