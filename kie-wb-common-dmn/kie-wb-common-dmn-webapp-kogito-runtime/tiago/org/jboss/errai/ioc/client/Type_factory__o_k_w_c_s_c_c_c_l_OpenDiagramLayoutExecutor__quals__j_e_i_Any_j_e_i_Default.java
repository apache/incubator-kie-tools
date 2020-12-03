package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.graph.processing.layout.LayoutExecutor;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_k_w_c_s_c_c_c_l_OpenDiagramLayoutExecutor__quals__j_e_i_Any_j_e_i_Default extends Factory<OpenDiagramLayoutExecutor> { public Type_factory__o_k_w_c_s_c_c_c_l_OpenDiagramLayoutExecutor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(OpenDiagramLayoutExecutor.class, "Type_factory__o_k_w_c_s_c_c_c_l_OpenDiagramLayoutExecutor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { OpenDiagramLayoutExecutor.class, Object.class, LayoutExecutor.class });
  }

  public OpenDiagramLayoutExecutor createInstance(final ContextManager contextManager) {
    final OpenDiagramLayoutExecutor instance = new OpenDiagramLayoutExecutor();
    setIncompleteInstance(instance);
    final ClientTranslationService OpenDiagramLayoutExecutor_translationService = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    OpenDiagramLayoutExecutor_ClientTranslationService_translationService(instance, OpenDiagramLayoutExecutor_translationService);
    final Event OpenDiagramLayoutExecutor_event = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, OpenDiagramLayoutExecutor_event);
    OpenDiagramLayoutExecutor_Event_event(instance, OpenDiagramLayoutExecutor_event);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event OpenDiagramLayoutExecutor_Event_event(OpenDiagramLayoutExecutor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor::event;
  }-*/;

  native static void OpenDiagramLayoutExecutor_Event_event(OpenDiagramLayoutExecutor instance, Event<NotificationEvent> value) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor::event = value;
  }-*/;

  native static ClientTranslationService OpenDiagramLayoutExecutor_ClientTranslationService_translationService(OpenDiagramLayoutExecutor instance) /*-{
    return instance.@org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor::translationService;
  }-*/;

  native static void OpenDiagramLayoutExecutor_ClientTranslationService_translationService(OpenDiagramLayoutExecutor instance, ClientTranslationService value) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.components.layout.OpenDiagramLayoutExecutor::translationService = value;
  }-*/;
}