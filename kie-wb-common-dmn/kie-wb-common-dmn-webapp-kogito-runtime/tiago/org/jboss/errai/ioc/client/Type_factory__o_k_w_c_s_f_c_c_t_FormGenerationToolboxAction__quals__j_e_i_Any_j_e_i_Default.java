package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.forms.client.components.toolbox.FormGenerationToolboxAction;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;

public class Type_factory__o_k_w_c_s_f_c_c_t_FormGenerationToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<FormGenerationToolboxAction> { public Type_factory__o_k_w_c_s_f_c_c_t_FormGenerationToolboxAction__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormGenerationToolboxAction.class, "Type_factory__o_k_w_c_s_f_c_c_t_FormGenerationToolboxAction__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormGenerationToolboxAction.class, Object.class, ToolboxAction.class });
  }

  public FormGenerationToolboxAction createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ClientFormGenerationManager _formGenerationManager_1 = (ClientFormGenerationManager) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default");
    final FormGenerationToolboxAction instance = new FormGenerationToolboxAction(_translationService_0, _formGenerationManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}