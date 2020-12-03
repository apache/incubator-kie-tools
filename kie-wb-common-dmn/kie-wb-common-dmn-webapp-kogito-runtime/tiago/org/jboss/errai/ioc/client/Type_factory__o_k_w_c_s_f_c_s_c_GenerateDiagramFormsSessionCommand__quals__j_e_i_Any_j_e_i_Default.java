package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.forms.client.gen.ClientFormGenerationManager;
import org.kie.workbench.common.stunner.forms.client.session.command.GenerateDiagramFormsSessionCommand;

public class Type_factory__o_k_w_c_s_f_c_s_c_GenerateDiagramFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<GenerateDiagramFormsSessionCommand> { public Type_factory__o_k_w_c_s_f_c_s_c_GenerateDiagramFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GenerateDiagramFormsSessionCommand.class, "Type_factory__o_k_w_c_s_f_c_s_c_GenerateDiagramFormsSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GenerateDiagramFormsSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public GenerateDiagramFormsSessionCommand createInstance(final ContextManager contextManager) {
    final ClientFormGenerationManager _formGenerationManager_0 = (ClientFormGenerationManager) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_g_ClientFormGenerationManager__quals__j_e_i_Any_j_e_i_Default");
    final GenerateDiagramFormsSessionCommand instance = new GenerateDiagramFormsSessionCommand(_formGenerationManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}