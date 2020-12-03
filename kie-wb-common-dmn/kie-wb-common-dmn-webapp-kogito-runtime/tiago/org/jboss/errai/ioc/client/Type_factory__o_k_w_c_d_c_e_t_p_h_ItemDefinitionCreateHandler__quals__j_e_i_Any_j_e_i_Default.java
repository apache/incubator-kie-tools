package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionCreateHandler;

public class Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionCreateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionCreateHandler> { public Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionCreateHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionCreateHandler.class, "Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionCreateHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionCreateHandler.class, Object.class });
  }

  public ItemDefinitionCreateHandler createInstance(final ContextManager contextManager) {
    final ItemDefinitionUtils _itemDefinitionUtils_0 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionStore _itemDefinitionStore_1 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionCreateHandler instance = new ItemDefinitionCreateHandler(_itemDefinitionUtils_0, _itemDefinitionStore_1);
    registerDependentScopedReference(instance, _itemDefinitionUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}