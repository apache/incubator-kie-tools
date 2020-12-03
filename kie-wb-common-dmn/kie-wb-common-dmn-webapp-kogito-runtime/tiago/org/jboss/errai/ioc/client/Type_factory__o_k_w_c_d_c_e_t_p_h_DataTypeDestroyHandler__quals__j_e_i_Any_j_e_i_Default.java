package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeDestroyHandler;
import org.kie.workbench.common.dmn.client.editors.types.persistence.handlers.DataTypeHandler;

public class Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeDestroyHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeDestroyHandler> { public Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeDestroyHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeDestroyHandler.class, "Type_factory__o_k_w_c_d_c_e_t_p_h_DataTypeDestroyHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeDestroyHandler.class, DataTypeHandler.class, Object.class });
  }

  public DataTypeDestroyHandler createInstance(final ContextManager contextManager) {
    final DataTypeStore _dataTypeStore_0 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager _dataTypeManager_1 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeDestroyHandler instance = new DataTypeDestroyHandler(_dataTypeStore_0, _dataTypeManager_1);
    registerDependentScopedReference(instance, _dataTypeManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}