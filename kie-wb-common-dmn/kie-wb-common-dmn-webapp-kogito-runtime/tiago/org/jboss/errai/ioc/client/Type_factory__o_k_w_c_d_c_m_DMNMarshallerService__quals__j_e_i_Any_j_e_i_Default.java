package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactoryImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.DMNMarshallerService;
import org.kie.workbench.common.dmn.client.marshaller.marshall.DMNMarshaller;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.DMNUnmarshaller;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CurrentRegistryChangedEvent;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_d_c_m_DMNMarshallerService__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerService> { public Type_factory__o_k_w_c_d_c_m_DMNMarshallerService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNMarshallerService.class, "Type_factory__o_k_w_c_d_c_m_DMNMarshallerService__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNMarshallerService.class, Object.class });
  }

  public DMNMarshallerService createInstance(final ContextManager contextManager) {
    final DMNDiagramsSession _dmnDiagramsSession_5 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final WorkspaceProjectContext _projectContext_6 = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    final Event<CurrentRegistryChangedEvent> _currentRegistryChangedEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CurrentRegistryChangedEvent.class }, new Annotation[] { });
    final DMNMarshaller _dmnMarshaller_1 = (DMNMarshaller) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_m_DMNMarshaller__quals__j_e_i_Any_j_e_i_Default");
    final DMNUnmarshaller _dmnUnmarshaller_0 = (DMNUnmarshaller) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_u_DMNUnmarshaller__quals__j_e_i_Any_j_e_i_Default");
    final Promises _promises_4 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramFactory _dmnDiagramFactory_2 = (DMNDiagramFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_a_f_DMNDiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_3 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerService instance = new DMNMarshallerService(_dmnUnmarshaller_0, _dmnMarshaller_1, _dmnDiagramFactory_2, _definitionManager_3, _promises_4, _dmnDiagramsSession_5, _projectContext_6, _currentRegistryChangedEvent_7);
    registerDependentScopedReference(instance, _currentRegistryChangedEvent_7);
    registerDependentScopedReference(instance, _promises_4);
    registerDependentScopedReference(instance, _dmnDiagramFactory_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onDiagramSelectedSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected", new AbstractCDIEventCallback<DMNDiagramSelected>() {
      public void fireEvent(final DMNDiagramSelected event) {
        instance.onDiagramSelected(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNMarshallerService) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNMarshallerService instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onDiagramSelectedSubscription", Subscription.class)).remove();
  }
}