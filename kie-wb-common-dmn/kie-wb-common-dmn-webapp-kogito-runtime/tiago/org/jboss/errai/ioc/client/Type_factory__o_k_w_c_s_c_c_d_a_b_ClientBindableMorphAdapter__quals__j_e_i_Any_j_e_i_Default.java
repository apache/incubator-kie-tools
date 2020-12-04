package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.definition.adapter.binding.ClientBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AbstractMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.BindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManagerImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableMorphAdapter__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientBindableMorphAdapter> { public Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableMorphAdapter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientBindableMorphAdapter.class, "Type_factory__o_k_w_c_s_c_c_d_a_b_ClientBindableMorphAdapter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientBindableMorphAdapter.class, BindableMorphAdapter.class, AbstractMorphAdapter.class, Object.class, MorphAdapter.class, Adapter.class });
  }

  public ClientBindableMorphAdapter createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_1 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _beanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final CloneManager _cloneManager_3 = (CloneManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_2 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ClientBindableMorphAdapter instance = new ClientBindableMorphAdapter(_beanManager_0, _factoryManager_1, _definitionUtils_2, _cloneManager_3);
    registerDependentScopedReference(instance, _beanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientBindableMorphAdapter instance) {
    instance.init();
  }
}