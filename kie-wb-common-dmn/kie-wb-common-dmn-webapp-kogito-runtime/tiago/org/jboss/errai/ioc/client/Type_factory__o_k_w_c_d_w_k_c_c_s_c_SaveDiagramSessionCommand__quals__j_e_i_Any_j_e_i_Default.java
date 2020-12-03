package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.command.SaveDiagramSessionCommand;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.command.ClientSessionCommand;

public class Type_factory__o_k_w_c_d_w_k_c_c_s_c_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SaveDiagramSessionCommand> { public Type_factory__o_k_w_c_d_w_k_c_c_s_c_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SaveDiagramSessionCommand.class, "Type_factory__o_k_w_c_d_w_k_c_c_s_c_SaveDiagramSessionCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SaveDiagramSessionCommand.class, AbstractClientSessionCommand.class, Object.class, ClientSessionCommand.class, SessionAware.class });
  }

  public SaveDiagramSessionCommand createInstance(final ContextManager contextManager) {
    final SaveDiagramSessionCommand instance = new SaveDiagramSessionCommand();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}