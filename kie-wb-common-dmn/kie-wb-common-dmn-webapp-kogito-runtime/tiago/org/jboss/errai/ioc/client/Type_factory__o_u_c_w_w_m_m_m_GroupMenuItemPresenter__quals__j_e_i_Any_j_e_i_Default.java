package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.GroupMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.HasChildren;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.GroupMenuItemPresenter.View;

public class Type_factory__o_u_c_w_w_m_m_m_GroupMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<GroupMenuItemPresenter> { public Type_factory__o_u_c_w_w_m_m_m_GroupMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GroupMenuItemPresenter.class, "Type_factory__o_u_c_w_w_m_m_m_GroupMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GroupMenuItemPresenter.class, Object.class, BaseMenuItemPresenter.class, HasChildren.class });
  }

  public GroupMenuItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (GroupMenuItemView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_m_m_GroupMenuItemView__quals__j_e_i_Any_j_e_i_Default");
    final GroupMenuItemPresenter instance = new GroupMenuItemPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final GroupMenuItemPresenter instance) {
    instance.init();
  }
}