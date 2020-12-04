package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.ItemDefinitionDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.common.PropertiesPanelNotifier;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

public class Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionDestroyHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionDestroyHandler> { public Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionDestroyHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionDestroyHandler.class, "Type_factory__o_k_w_c_d_c_e_t_p_h_ItemDefinitionDestroyHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionDestroyHandler.class, Object.class });
  }

  public ItemDefinitionDestroyHandler createInstance(final ContextManager contextManager) {
    final ItemDefinitionStore _itemDefinitionStore_0 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final PropertiesPanelNotifier _panelNotifier_2 = (PropertiesPanelNotifier) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_h_c_PropertiesPanelNotifier__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _dmnGraphUtils_1 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionDestroyHandler instance = new ItemDefinitionDestroyHandler(_itemDefinitionStore_0, _dmnGraphUtils_1, _panelNotifier_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}