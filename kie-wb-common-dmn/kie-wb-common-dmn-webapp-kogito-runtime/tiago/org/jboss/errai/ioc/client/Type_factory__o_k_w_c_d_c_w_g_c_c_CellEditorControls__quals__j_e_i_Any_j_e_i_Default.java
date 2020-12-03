package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView.Presenter;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsViewImpl;

public class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControls__quals__j_e_i_Any_j_e_i_Default extends Factory<CellEditorControls> { public Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControls__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CellEditorControls.class, "Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControls__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CellEditorControls.class, Object.class, Presenter.class });
  }

  public CellEditorControls createInstance(final ContextManager contextManager) {
    final CellEditorControlsView _view_0 = (CellEditorControlsViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlsViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final CellEditorControls instance = new CellEditorControls(_view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}