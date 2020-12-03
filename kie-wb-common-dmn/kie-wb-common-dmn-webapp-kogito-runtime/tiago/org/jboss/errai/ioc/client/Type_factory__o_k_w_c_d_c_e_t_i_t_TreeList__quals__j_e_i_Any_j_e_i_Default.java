package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeList;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeList.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListView;

public class Type_factory__o_k_w_c_d_c_e_t_i_t_TreeList__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeList> { public Type_factory__o_k_w_c_d_c_e_t_i_t_TreeList__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeList.class, "Type_factory__o_k_w_c_d_c_e_t_i_t_TreeList__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeList.class, Object.class });
  }

  public TreeList createInstance(final ContextManager contextManager) {
    final View _view_0 = (TreeListView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_i_t_TreeListView__quals__j_e_i_Any_j_e_i_Default");
    final TreeList instance = new TreeList(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}