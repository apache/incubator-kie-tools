package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem.View;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItem;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.ToolbarItemView;

public class Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItem__quals__j_e_i_Any_j_e_i_Default extends Factory<ToolbarItem> { public Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ToolbarItem.class, "Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ToolbarItem.class, AbstractToolbarItem.class, Object.class, IsWidget.class });
  }

  public ToolbarItem createInstance(final ContextManager contextManager) {
    final View _view_0 = (ToolbarItemView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_t_i_ToolbarItemView__quals__j_e_i_Any_j_e_i_Default");
    final ToolbarItem instance = new ToolbarItem(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ToolbarItem instance) {
    instance.init();
  }
}