package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItem;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItem.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListSubItemView;

public class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItem__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeListSubItem> { public Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeListSubItem.class, "Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeListSubItem.class, Object.class });
  }

  public TreeListSubItem createInstance(final ContextManager contextManager) {
    final View _view_0 = (TreeListSubItemView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListSubItemView__quals__j_e_i_Any_j_e_i_Default");
    final TreeListSubItem instance = new TreeListSubItem(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}