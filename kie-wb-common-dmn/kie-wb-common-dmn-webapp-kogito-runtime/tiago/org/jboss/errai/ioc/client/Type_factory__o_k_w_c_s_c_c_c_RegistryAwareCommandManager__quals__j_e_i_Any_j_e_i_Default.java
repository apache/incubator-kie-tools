package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.command.RegistryAwareCommandManager;

public class Type_factory__o_k_w_c_s_c_c_c_RegistryAwareCommandManager__quals__j_e_i_Any_j_e_i_Default extends Factory<RegistryAwareCommandManager> { public Type_factory__o_k_w_c_s_c_c_c_RegistryAwareCommandManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RegistryAwareCommandManager.class, "Type_factory__o_k_w_c_s_c_c_c_RegistryAwareCommandManager__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RegistryAwareCommandManager.class, Object.class });
  }

  public RegistryAwareCommandManager createInstance(final ContextManager contextManager) {
    final RegistryAwareCommandManager instance = new RegistryAwareCommandManager();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((RegistryAwareCommandManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final RegistryAwareCommandManager instance, final ContextManager contextManager) {
    instance.destroy();
  }
}