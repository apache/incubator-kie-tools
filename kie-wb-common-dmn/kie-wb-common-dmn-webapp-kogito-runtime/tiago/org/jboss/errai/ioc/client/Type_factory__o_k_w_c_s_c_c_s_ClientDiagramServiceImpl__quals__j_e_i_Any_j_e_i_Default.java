package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.kie.workbench.common.stunner.core.client.service.AbstractClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramServiceImpl;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDiagramSavedEvent;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.core.service.DiagramService;

public class Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDiagramServiceImpl> { public Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientDiagramServiceImpl.class, "Type_factory__o_k_w_c_s_c_c_s_ClientDiagramServiceImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientDiagramServiceImpl.class, AbstractClientDiagramService.class, Object.class, ClientDiagramService.class });
  }

  public ClientDiagramServiceImpl createInstance(final ContextManager contextManager) {
    final Caller<DiagramLookupService> _diagramLookupServiceCaller_2 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { DiagramLookupService.class }, new Annotation[] { });
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Caller<DiagramService> _diagramServiceCaller_4 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { DiagramService.class }, new Annotation[] { });
    final Event<SessionDiagramSavedEvent> _saveEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SessionDiagramSavedEvent.class }, new Annotation[] { });
    final ShapeManager _shapeManager_0 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientDiagramServiceImpl instance = new ClientDiagramServiceImpl(_shapeManager_0, _sessionManager_1, _diagramLookupServiceCaller_2, _saveEvent_3, _diagramServiceCaller_4);
    registerDependentScopedReference(instance, _diagramLookupServiceCaller_2);
    registerDependentScopedReference(instance, _diagramServiceCaller_4);
    registerDependentScopedReference(instance, _saveEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}