package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.CanBeClosedByKeyboard;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls.Editor;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.PopupEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelector;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView.Presenter;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorViewImpl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.popover.AbstractPopoverImpl;

public class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelector> { public Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListSelector.class, "Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListSelector.class, AbstractPopoverImpl.class, Object.class, Editor.class, PopupEditorControls.class, IsElement.class, CanBeClosedByKeyboard.class, Presenter.class });
  }

  public ListSelector createInstance(final ContextManager contextManager) {
    final ListSelectorView _view_0 = (ListSelectorViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ListSelector instance = new ListSelector(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}