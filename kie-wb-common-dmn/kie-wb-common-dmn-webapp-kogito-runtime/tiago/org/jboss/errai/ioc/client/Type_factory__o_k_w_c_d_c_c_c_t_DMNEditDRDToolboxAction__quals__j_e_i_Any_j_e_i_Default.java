package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNEditDRDToolboxAction;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;

public class Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDRDToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditDRDToolboxAction> { public Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDRDToolboxAction__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNEditDRDToolboxAction.class, "Type_factory__o_k_w_c_d_c_c_c_t_DMNEditDRDToolboxAction__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditDRDToolboxAction.class, Object.class, ToolboxAction.class });
  }

  public DMNEditDRDToolboxAction createInstance(final ContextManager contextManager) {
    final DRDContextMenu _drdContextMenu_0 = (DRDContextMenu) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditDRDToolboxAction instance = new DMNEditDRDToolboxAction(_drdContextMenu_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}