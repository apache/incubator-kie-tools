package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.menu.megamenu.menuitem.IconMenuItemView;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.BaseMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.CanBeDisabled;
import org.uberfire.client.workbench.widgets.menu.megamenu.base.Selectable;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.IconMenuItemPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.menuitem.IconMenuItemPresenter.View;

public class Type_factory__o_u_c_w_w_m_m_m_IconMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<IconMenuItemPresenter> { public Type_factory__o_u_c_w_w_m_m_m_IconMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IconMenuItemPresenter.class, "Type_factory__o_u_c_w_w_m_m_m_IconMenuItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IconMenuItemPresenter.class, Object.class, BaseMenuItemPresenter.class, Selectable.class, CanBeDisabled.class });
  }

  public IconMenuItemPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (IconMenuItemView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_m_m_IconMenuItemView__quals__j_e_i_Any_j_e_i_Default");
    final IconMenuItemPresenter instance = new IconMenuItemPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final IconMenuItemPresenter instance) {
    instance.init();
  }
}