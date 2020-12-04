package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelectView;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelect__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSelect> { public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelect__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeSelect.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelect__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeSelect.class, Object.class });
  }

  public DataTypeSelect createInstance(final ContextManager contextManager) {
    final View _view_0 = (DataTypeSelectView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeSelectView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeManager _dataTypeManager_2 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeUtils _dataTypeUtils_1 = (DataTypeUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeUtils__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeSelect instance = new DataTypeSelect(_view_0, _dataTypeUtils_1, _dataTypeManager_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dataTypeManager_2);
    registerDependentScopedReference(instance, _dataTypeUtils_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeSelect instance) {
    DataTypeSelect_setup(instance);
  }

  public native static void DataTypeSelect_setup(DataTypeSelect instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeSelect::setup()();
  }-*/;
}