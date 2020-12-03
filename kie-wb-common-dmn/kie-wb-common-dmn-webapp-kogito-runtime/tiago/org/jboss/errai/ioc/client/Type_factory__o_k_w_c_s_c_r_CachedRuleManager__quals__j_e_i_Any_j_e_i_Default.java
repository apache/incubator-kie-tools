package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.rule.CachedRuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleManagerImpl;

public class Type_factory__o_k_w_c_s_c_r_CachedRuleManager__quals__j_e_i_Any_j_e_i_Default extends Factory<CachedRuleManager> { public Type_factory__o_k_w_c_s_c_r_CachedRuleManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CachedRuleManager.class, "Type_factory__o_k_w_c_s_c_r_CachedRuleManager__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CachedRuleManager.class, Object.class });
  }

  public CachedRuleManager createInstance(final ContextManager contextManager) {
    final RuleManagerImpl _ruleManager_0 = (RuleManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_RuleManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final CachedRuleManager instance = new CachedRuleManager(_ruleManager_0);
    registerDependentScopedReference(instance, _ruleManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CachedRuleManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final CachedRuleManager instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final CachedRuleManager instance) {
    instance.init();
  }
}