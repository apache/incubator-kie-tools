package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.GroupContextMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.GroupContextMenuItemPresenter.View;

public class Type_factory__o_u_c_w_w_m_m_c_GroupContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupContextMenuItemPresenter> { public Type_factory__o_u_c_w_w_m_m_c_GroupContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GroupContextMenuItemPresenter.class, "Type_factory__o_u_c_w_w_m_m_c_GroupContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GroupContextMenuItemPresenter.class, Object.class, BaseMenuItemPresenter.class, HasChildren.class, CanBeDisabled.class });
  }

  public GroupContextMenuItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (GroupContextMenuItemView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_m_c_GroupContextMenuItemView__quals__j_e_i_Any_j_e_i_Default");
    final GroupContextMenuItemPresenter instance = new GroupContextMenuItemPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final GroupContextMenuItemPresenter instance) {
    instance.init();
  }
}