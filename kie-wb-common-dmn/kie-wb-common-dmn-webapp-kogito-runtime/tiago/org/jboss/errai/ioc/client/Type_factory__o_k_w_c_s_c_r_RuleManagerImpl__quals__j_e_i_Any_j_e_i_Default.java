package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.rule.RuleManagerImpl;

public class Type_factory__o_k_w_c_s_c_r_RuleManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<RuleManagerImpl> { public Type_factory__o_k_w_c_s_c_r_RuleManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RuleManagerImpl.class, "Type_factory__o_k_w_c_s_c_r_RuleManagerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RuleManagerImpl.class, Object.class });
  }

  public RuleManagerImpl createInstance(final ContextManager contextManager) {
    final RegistryFactory _registryFactory_0 = (ClientRegistryFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final RuleManagerImpl instance = new RuleManagerImpl(_registryFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((RuleManagerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final RuleManagerImpl instance, final ContextManager contextManager) {
    instance.destroy();
  }
}