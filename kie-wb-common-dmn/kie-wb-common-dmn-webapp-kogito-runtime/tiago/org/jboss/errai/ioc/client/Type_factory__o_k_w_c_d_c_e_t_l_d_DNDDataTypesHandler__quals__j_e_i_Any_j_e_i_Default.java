package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDDataTypesHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

public class Type_factory__o_k_w_c_d_c_e_t_l_d_DNDDataTypesHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DNDDataTypesHandler> { public Type_factory__o_k_w_c_d_c_e_t_l_d_DNDDataTypesHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DNDDataTypesHandler.class, "Type_factory__o_k_w_c_d_c_e_t_l_d_DNDDataTypesHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DNDDataTypesHandler.class, Object.class });
  }

  public DNDDataTypesHandler createInstance(final ContextManager contextManager) {
    final DataTypeStore _dataTypeStore_0 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionStore _itemDefinitionStore_2 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager _dataTypeManager_1 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DNDDataTypesHandler instance = new DNDDataTypesHandler(_dataTypeStore_0, _dataTypeManager_1, _itemDefinitionStore_2);
    registerDependentScopedReference(instance, _dataTypeManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}