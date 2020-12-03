package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.AbstractAcceptorControl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ConnectionAcceptorControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ConnectionAcceptorControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasHighlight;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;

public class Type_factory__o_k_w_c_s_c_l_c_c_ConnectionAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ConnectionAcceptorControlImpl> { public Type_factory__o_k_w_c_s_c_l_c_c_ConnectionAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConnectionAcceptorControlImpl.class, "Type_factory__o_k_w_c_s_c_l_c_c_ConnectionAcceptorControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConnectionAcceptorControlImpl.class, AbstractAcceptorControl.class, Object.class, CanvasControl.class, RequiresCommandManager.class, ConnectionAcceptorControl.class });
  }

  public ConnectionAcceptorControlImpl createInstance(final ContextManager contextManager) {
    final CanvasHighlight _canvasHighlight_1 = (CanvasHighlight) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_u_CanvasHighlight__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandFactory<AbstractCanvasHandler> _canvasCommandFactory_0 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final ConnectionAcceptorControlImpl instance = new ConnectionAcceptorControlImpl(_canvasCommandFactory_0, _canvasHighlight_1);
    registerDependentScopedReference(instance, _canvasHighlight_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}