package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControls;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;

public class Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CellEditorControlImpl> { public Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CellEditorControlImpl.class, "Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CellEditorControlImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, CellEditorControl.class, SessionAware.class });
  }

  public CellEditorControlImpl createInstance(final ContextManager contextManager) {
    final CellEditorControls _editorControls_0 = (CellEditorControls) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_c_CellEditorControls__quals__j_e_i_Any_j_e_i_Default");
    final CellEditorControlImpl instance = new CellEditorControlImpl(_editorControls_0);
    registerDependentScopedReference(instance, _editorControls_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}