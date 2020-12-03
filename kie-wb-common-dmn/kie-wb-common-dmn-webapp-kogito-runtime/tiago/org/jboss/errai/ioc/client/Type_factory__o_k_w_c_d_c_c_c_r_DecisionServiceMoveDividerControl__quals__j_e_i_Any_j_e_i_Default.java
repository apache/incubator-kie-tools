package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.resize.DecisionServiceMoveDividerControl;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager.CommandManagerProvider;

public class Type_factory__o_k_w_c_d_c_c_c_r_DecisionServiceMoveDividerControl__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionServiceMoveDividerControl> { public Type_factory__o_k_w_c_d_c_c_c_r_DecisionServiceMoveDividerControl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionServiceMoveDividerControl.class, "Type_factory__o_k_w_c_d_c_c_c_r_DecisionServiceMoveDividerControl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionServiceMoveDividerControl.class, AbstractCanvasHandlerRegistrationControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, CanvasRegistrationControl.class, RequiresCommandManager.class, CommandManagerProvider.class });
  }

  public DecisionServiceMoveDividerControl createInstance(final ContextManager contextManager) {
    final DefaultCanvasCommandFactory _canvasCommandFactory_0 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DecisionServiceMoveDividerControl instance = new DecisionServiceMoveDividerControl(_canvasCommandFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}