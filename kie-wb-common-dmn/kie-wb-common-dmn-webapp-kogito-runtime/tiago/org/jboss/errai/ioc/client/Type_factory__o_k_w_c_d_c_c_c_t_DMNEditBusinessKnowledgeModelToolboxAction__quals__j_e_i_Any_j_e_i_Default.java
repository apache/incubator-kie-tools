package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditBusinessKnowledgeModelToolboxAction;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_d_c_c_c_t_DMNEditBusinessKnowledgeModelToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditBusinessKnowledgeModelToolboxAction> { public Type_factory__o_k_w_c_d_c_c_c_t_DMNEditBusinessKnowledgeModelToolboxAction__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNEditBusinessKnowledgeModelToolboxAction.class, "Type_factory__o_k_w_c_d_c_c_c_t_DMNEditBusinessKnowledgeModelToolboxAction__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditBusinessKnowledgeModelToolboxAction.class, Object.class, ToolboxAction.class });
  }

  public DMNEditBusinessKnowledgeModelToolboxAction createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_1 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final Event<EditExpressionEvent> _editExpressionEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { EditExpressionEvent.class }, new Annotation[] { });
    final DMNEditBusinessKnowledgeModelToolboxAction instance = new DMNEditBusinessKnowledgeModelToolboxAction(_sessionManager_0, _translationService_1, _editExpressionEvent_2);
    registerDependentScopedReference(instance, _editExpressionEvent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}