package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeUtils;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListHighlightHelper;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListHighlightHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListHighlightHelper> { public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListHighlightHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeListHighlightHelper.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListHighlightHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeListHighlightHelper.class, Object.class });
  }

  public DataTypeListHighlightHelper createInstance(final ContextManager contextManager) {
    final DataTypeUtils _dataTypeUtils_0 = (DataTypeUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeUtils__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeListHighlightHelper instance = new DataTypeListHighlightHelper(_dataTypeUtils_0);
    registerDependentScopedReference(instance, _dataTypeUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}