package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.rule.ClientRuleManager;
import org.kie.workbench.common.stunner.core.lookup.domain.CommonDomainLookups;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_l_d_CommonDomainLookups__quals__j_e_i_Any_j_e_i_Default extends Factory<CommonDomainLookups> { public Type_factory__o_k_w_c_s_c_l_d_CommonDomainLookups__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CommonDomainLookups.class, "Type_factory__o_k_w_c_s_c_l_d_CommonDomainLookups__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CommonDomainLookups.class, Object.class });
  }

  public CommonDomainLookups createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionsCacheRegistry _definitionsRegistry_1 = (DefinitionsCacheRegistry) contextManager.getInstance("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final RuleManager _ruleManager_2 = (ClientRuleManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_ClientRuleManager__quals__j_e_i_Any_j_e_i_Default");
    final CommonDomainLookups instance = new CommonDomainLookups(_definitionUtils_0, _definitionsRegistry_1, _ruleManager_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CommonDomainLookups) instance, contextManager);
  }

  public void destroyInstanceHelper(final CommonDomainLookups instance, final ContextManager contextManager) {
    instance.destroy();
  }
}