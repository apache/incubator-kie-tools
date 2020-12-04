package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.profile.DomainProfileManager;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_p_CollapsedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsedPaletteDefinitionBuilder> { public Type_factory__o_k_w_c_s_c_c_c_p_CollapsedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CollapsedPaletteDefinitionBuilder.class, "Type_factory__o_k_w_c_s_c_c_c_p_CollapsedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CollapsedPaletteDefinitionBuilder.class, Object.class });
  }

  public CollapsedPaletteDefinitionBuilder createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final StunnerTranslationService _translationService_3 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DomainProfileManager _profileManager_1 = (DomainProfileManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_p_DomainProfileManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionsCacheRegistry _definitionsRegistry_2 = (DefinitionsCacheRegistry) contextManager.getInstance("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final CollapsedPaletteDefinitionBuilder instance = new CollapsedPaletteDefinitionBuilder(_definitionUtils_0, _profileManager_1, _definitionsRegistry_2, _translationService_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}