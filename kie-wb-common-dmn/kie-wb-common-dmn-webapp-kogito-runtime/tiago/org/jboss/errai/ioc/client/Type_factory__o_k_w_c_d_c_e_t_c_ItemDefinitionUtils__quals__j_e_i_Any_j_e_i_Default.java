package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

public class Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionUtils> { public Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionUtils.class, "Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionUtils.class, Object.class });
  }

  public ItemDefinitionUtils createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUtils instance = new ItemDefinitionUtils(_dmnGraphUtils_0);
    registerDependentScopedReference(instance, _dmnGraphUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}