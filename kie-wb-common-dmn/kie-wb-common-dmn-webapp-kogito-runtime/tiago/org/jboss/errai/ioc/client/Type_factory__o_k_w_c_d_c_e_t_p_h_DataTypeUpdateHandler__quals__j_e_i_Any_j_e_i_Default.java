package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeUpdateHandler;

public class Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeUpdateHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeUpdateHandler> { public Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeUpdateHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeUpdateHandler.class, "Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeUpdateHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeUpdateHandler.class, DataTypeHandler.class, Object.class });
  }

  public DataTypeUpdateHandler createInstance(final ContextManager contextManager) {
    final ItemDefinitionStore _itemDefinitionStore_0 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager _dataTypeManager_2 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStore _dataTypeStore_1 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeUpdateHandler instance = new DataTypeUpdateHandler(_itemDefinitionStore_0, _dataTypeStore_1, _dataTypeManager_2);
    registerDependentScopedReference(instance, _dataTypeManager_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}