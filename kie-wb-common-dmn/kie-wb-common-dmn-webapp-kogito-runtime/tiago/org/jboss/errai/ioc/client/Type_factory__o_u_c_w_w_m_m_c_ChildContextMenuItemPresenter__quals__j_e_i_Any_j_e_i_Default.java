package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.menu.megamenu.contextmenuitem.ChildContextMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.Selectable;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.contextmenuitem.ChildContextMenuItemPresenter.View;

public class Type_factory__o_u_c_w_w_m_m_c_ChildContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ChildContextMenuItemPresenter> { public Type_factory__o_u_c_w_w_m_m_c_ChildContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ChildContextMenuItemPresenter.class, "Type_factory__o_u_c_w_w_m_m_c_ChildContextMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ChildContextMenuItemPresenter.class, Object.class, BaseMenuItemPresenter.class, Selectable.class, CanBeDisabled.class });
  }

  public ChildContextMenuItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (ChildContextMenuItemView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_m_c_ChildContextMenuItemView__quals__j_e_i_Any_j_e_i_Default");
    final ChildContextMenuItemPresenter instance = new ChildContextMenuItemPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ChildContextMenuItemPresenter instance) {
    instance.init();
  }
}