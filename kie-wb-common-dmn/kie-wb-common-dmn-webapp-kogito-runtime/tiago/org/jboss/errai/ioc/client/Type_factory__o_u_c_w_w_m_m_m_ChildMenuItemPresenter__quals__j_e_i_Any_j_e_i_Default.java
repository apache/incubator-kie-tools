package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.ChildMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanHide;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.Selectable;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.ChildMenuItemPresenter.View;

public class Type_factory__o_u_c_w_w_m_m_m_ChildMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildMenuItemPresenter> { public Type_factory__o_u_c_w_w_m_m_m_ChildMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ChildMenuItemPresenter.class, "Type_factory__o_u_c_w_w_m_m_m_ChildMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ChildMenuItemPresenter.class, Object.class, BaseMenuItemPresenter.class, Selectable.class, CanBeDisabled.class, CanHide.class });
  }

  public ChildMenuItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (ChildMenuItemView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_m_m_ChildMenuItemView__quals__j_e_i_Any_j_e_i_Default");
    final ChildMenuItemPresenter instance = new ChildMenuItemPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ChildMenuItemPresenter instance) {
    instance.init();
  }
}