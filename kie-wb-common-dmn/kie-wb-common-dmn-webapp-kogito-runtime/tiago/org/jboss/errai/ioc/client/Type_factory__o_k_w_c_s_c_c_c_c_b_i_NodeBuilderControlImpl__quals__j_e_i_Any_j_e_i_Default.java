package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.BuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.NodeBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.AbstractElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.ElementBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.NodeBuilderControlImpl;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;

public class Type_factory__o_k_w_c_s_c_c_c_c_b_i_NodeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeBuilderControlImpl> { public Type_factory__o_k_w_c_s_c_c_c_c_b_i_NodeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NodeBuilderControlImpl.class, "Type_factory__o_k_w_c_s_c_c_c_c_b_i_NodeBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NodeBuilderControlImpl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, NodeBuilderControl.class, BuilderControl.class, RequiresCommandManager.class });
  }

  public NodeBuilderControlImpl createInstance(final ContextManager contextManager) {
    final AbstractElementBuilderControl _elementBuilderControl_2 = (ElementBuilderControlImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_b_i_ElementBuilderControlImpl__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_c_b_i_Element");
    final ClientDefinitionManager _clientDefinitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final CanvasCommandFactory<AbstractCanvasHandler> _commandFactory_1 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final NodeBuilderControlImpl instance = new NodeBuilderControlImpl(_clientDefinitionManager_0, _commandFactory_1, _elementBuilderControl_2);
    registerDependentScopedReference(instance, _elementBuilderControl_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}