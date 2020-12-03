package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer.View;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorerView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.tree.Tree;
import org.uberfire.ext.widgets.core.client.tree.TreeItem;

public class Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorerView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeExplorerView> { public Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorerView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeExplorerView.class, "Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorerView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeExplorerView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class, UberView.class, HasPresenter.class });
  }

  public TreeExplorerView createInstance(final ContextManager contextManager) {
    final Tree<TreeItem> _tree_0 = (Tree) contextManager.getInstance("ExtensionProvided_factory__o_u_e_w_c_c_t_Tree__quals__j_e_i_Any_j_e_i_Default");
    final TreeExplorerView instance = new TreeExplorerView(_tree_0);
    registerDependentScopedReference(instance, _tree_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}