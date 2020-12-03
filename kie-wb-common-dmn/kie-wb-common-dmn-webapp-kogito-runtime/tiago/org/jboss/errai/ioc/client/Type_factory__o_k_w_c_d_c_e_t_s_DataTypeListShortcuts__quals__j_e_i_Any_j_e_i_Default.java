package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcuts;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcuts.View;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcutsView;

public class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcuts__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListShortcuts> { public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcuts__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeListShortcuts.class, "Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcuts__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeListShortcuts.class, Object.class });
  }

  public DataTypeListShortcuts createInstance(final ContextManager contextManager) {
    final View _view_0 = (DataTypeListShortcutsView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcutsView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeUtils _dataTypeUtils_1 = (DataTypeUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeUtils__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeListShortcuts instance = new DataTypeListShortcuts(_view_0, _dataTypeUtils_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dataTypeUtils_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeListShortcuts instance) {
    instance.init();
  }
}