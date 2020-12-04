package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.notifications.FormGenerationNotifier;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateSelectedFormsSessionCommand;

public class Type_factory__o_k_w_c_s_f_c_s_c_GenerateSelectedFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<GenerateSelectedFormsSessionCommand> { public Type_factory__o_k_w_c_s_f_c_s_c_GenerateSelectedFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GenerateSelectedFormsSessionCommand.class, "Type_factory__o_k_w_c_s_f_c_s_c_GenerateSelectedFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GenerateSelectedFormsSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public GenerateSelectedFormsSessionCommand createInstance(final ContextManager contextManager) {
    final ClientFormGenerationManager _formGenerationManager_0 = (ClientFormGenerationManager) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationService _translationService_2 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FormGenerationNotifier _formGenerationNotifier_1 = (FormGenerationNotifier) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_n_FormGenerationNotifier__quals__j_e_i_Any_j_e_i_Default");
    final GenerateSelectedFormsSessionCommand instance = new GenerateSelectedFormsSessionCommand(_formGenerationManager_0, _formGenerationNotifier_1, _translationService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}