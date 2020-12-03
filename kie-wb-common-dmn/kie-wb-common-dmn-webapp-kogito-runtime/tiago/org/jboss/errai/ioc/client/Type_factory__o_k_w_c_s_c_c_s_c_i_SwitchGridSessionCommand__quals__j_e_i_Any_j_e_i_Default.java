package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;

public class Type_factory__o_k_w_c_s_c_c_s_c_i_SwitchGridSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SwitchGridSessionCommand> { public Type_factory__o_k_w_c_s_c_c_s_c_i_SwitchGridSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SwitchGridSessionCommand.class, "Type_factory__o_k_w_c_s_c_c_s_c_i_SwitchGridSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SwitchGridSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public SwitchGridSessionCommand createInstance(final ContextManager contextManager) {
    final SwitchGridSessionCommand instance = new SwitchGridSessionCommand();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}