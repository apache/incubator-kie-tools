package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;

public class Type_factory__o_k_w_c_s_c_r_i_DefaultDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDefinitionsCacheRegistry> { public Type_factory__o_k_w_c_s_c_r_i_DefaultDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultDefinitionsCacheRegistry.class, "Type_factory__o_k_w_c_s_c_r_i_DefaultDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultDefinitionsCacheRegistry.class, Object.class });
  }

  public DefaultDefinitionsCacheRegistry createInstance(final ContextManager contextManager) {
    final AdapterManager _adapterManager_1 = (AdapterManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final DefaultDefinitionsCacheRegistry instance = new DefaultDefinitionsCacheRegistry(_factoryManager_0, _adapterManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultDefinitionsCacheRegistry) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultDefinitionsCacheRegistry instance, final ContextManager contextManager) {
    instance.destroy();
  }
}