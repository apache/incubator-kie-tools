package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateProcessFormsSessionCommand;

public class Type_factory__o_k_w_c_s_f_c_s_c_GenerateProcessFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<GenerateProcessFormsSessionCommand> { public Type_factory__o_k_w_c_s_f_c_s_c_GenerateProcessFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GenerateProcessFormsSessionCommand.class, "Type_factory__o_k_w_c_s_f_c_s_c_GenerateProcessFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GenerateProcessFormsSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public GenerateProcessFormsSessionCommand createInstance(final ContextManager contextManager) {
    final ClientFormGenerationManager _formGenerationManager_0 = (ClientFormGenerationManager) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default");
    final GenerateProcessFormsSessionCommand instance = new GenerateProcessFormsSessionCommand(_formGenerationManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}