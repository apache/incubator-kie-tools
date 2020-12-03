package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.ValidateSessionCommand;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasDiagramValidator;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_ValidateSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidateSessionCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_ValidateSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValidateSessionCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_ValidateSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValidateSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public ValidateSessionCommand createInstance(final ContextManager contextManager) {
    final CanvasDiagramValidator<AbstractCanvasHandler> _validator_0 = (CanvasDiagramValidator) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_v_c_CanvasDiagramValidator__quals__j_e_i_Any_j_e_i_Default");
    final ValidateSessionCommand instance = new ValidateSessionCommand(_validator_0);
    registerDependentScopedReference(instance, _validator_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}