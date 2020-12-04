package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoTextTooltip;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasDefinitionTooltip;
import org.kie.workbench.common.stunner.core.client.components.views.CanvasTooltip;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public class Type_factory__o_k_w_c_s_c_c_c_v_CanvasDefinitionTooltip__quals__j_e_i_Any_j_e_i_Default extends Factory<CanvasDefinitionTooltip> { public Type_factory__o_k_w_c_s_c_c_c_v_CanvasDefinitionTooltip__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CanvasDefinitionTooltip.class, "Type_factory__o_k_w_c_s_c_c_c_v_CanvasDefinitionTooltip__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CanvasDefinitionTooltip.class, Object.class, CanvasTooltip.class });
  }

  public CanvasDefinitionTooltip createInstance(final ContextManager contextManager) {
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionsCacheRegistry _registry_1 = (DefinitionsCacheRegistry) contextManager.getInstance("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final CanvasTooltip<String> _textTooltip_2 = (LienzoTextTooltip) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_v_LienzoTextTooltip__quals__j_e_i_Any_j_e_i_Default");
    final CanvasDefinitionTooltip instance = new CanvasDefinitionTooltip(_definitionManager_0, _registry_1, _textTooltip_2);
    registerDependentScopedReference(instance, _textTooltip_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}