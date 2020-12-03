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
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.views.pfly.menu.PartContextMenusView;
import org.uberfire.client.workbench.widgets.menu.PartContextMenusPresenter.View;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_v_p_m_PartContextMenusView__quals__j_e_i_Any_j_e_i_Default extends Factory<PartContextMenusView> { public Type_factory__o_u_c_v_p_m_PartContextMenusView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PartContextMenusView.class, "Type_factory__o_u_c_v_p_m_PartContextMenusView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PartContextMenusView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class });
  }

  public PartContextMenusView createInstance(final ContextManager contextManager) {
    final PartContextMenusView instance = new PartContextMenusView();
    setIncompleteInstance(instance);
    final DefaultAuthorizationManager PartContextMenusView_authzManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    PartContextMenusView_AuthorizationManager_authzManager(instance, PartContextMenusView_authzManager);
    final User PartContextMenusView_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PartContextMenusView_identity);
    PartContextMenusView_User_identity(instance, PartContextMenusView_identity);
    setIncompleteInstance(null);
    return instance;
  }

  native static User PartContextMenusView_User_identity(PartContextMenusView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.PartContextMenusView::identity;
  }-*/;

  native static void PartContextMenusView_User_identity(PartContextMenusView instance, User value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.PartContextMenusView::identity = value;
  }-*/;

  native static AuthorizationManager PartContextMenusView_AuthorizationManager_authzManager(PartContextMenusView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.PartContextMenusView::authzManager;
  }-*/;

  native static void PartContextMenusView_AuthorizationManager_authzManager(PartContextMenusView instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.PartContextMenusView::authzManager = value;
  }-*/;
}