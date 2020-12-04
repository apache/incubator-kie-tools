package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.commands.factory.DefaultCanvasCommandFactory;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DMNIncludedModelHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DRGElementHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.ApplicationCommandManager;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_d_c_e_i_i_p_DMNIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNIncludedModelHandler> { public Type_factory__o_k_w_c_d_c_e_i_i_p_DMNIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNIncludedModelHandler.class, "Type_factory__o_k_w_c_d_c_e_i_i_p_DMNIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNIncludedModelHandler.class, Object.class, DRGElementHandler.class });
  }

  public DMNIncludedModelHandler createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DefaultCanvasCommandFactory _canvasCommandFactory_1 = (DefaultCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_f_DefaultCanvasCommandFactory__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final SessionCommandManager<AbstractCanvasHandler> _sessionCommandManager_2 = (ApplicationCommandManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_ApplicationCommandManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNIncludedModelHandler instance = new DMNIncludedModelHandler(_dmnGraphUtils_0, _canvasCommandFactory_1, _sessionCommandManager_2, _definitionUtils_3);
    registerDependentScopedReference(instance, _dmnGraphUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}