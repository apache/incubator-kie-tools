package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcuts.View;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcutsView;
import org.uberfire.client.mvp.HasPresenter;

public class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcutsView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListShortcutsView> { public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcutsView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeListShortcutsView.class, "Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcutsView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeListShortcutsView.class, Object.class, View.class, HasPresenter.class });
  }

  public DataTypeListShortcutsView createInstance(final ContextManager contextManager) {
    final ScrollHelper _scrollHelper_0 = (ScrollHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeListShortcutsView instance = new DataTypeListShortcutsView(_scrollHelper_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}