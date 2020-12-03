package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItemView;

public class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItem__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListItem> { public Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeListItem.class, "Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeListItem.class, Object.class });
  }

  public TreeListItem createInstance(final ContextManager contextManager) {
    final View _view_0 = (TreeListItemView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListItemView__quals__j_e_i_Any_j_e_i_Default");
    final TreeListItem instance = new TreeListItem(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final TreeListItem instance) {
    TreeListItem_setup(instance);
  }

  public native static void TreeListItem_setup(TreeListItem instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem::setup()();
  }-*/;
}