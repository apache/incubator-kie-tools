package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.views.pfly.menu.UserMenu;
import org.uberfire.client.views.pfly.menu.UserMenu.UserMenuView;
import org.uberfire.client.views.pfly.menu.UserMenuViewImpl;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;

public class Type_factory__o_u_c_v_p_m_UserMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<UserMenu> { public Type_factory__o_u_c_v_p_m_UserMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UserMenu.class, "Type_factory__o_u_c_v_p_m_UserMenu__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UserMenu.class, Object.class, CustomMenuBuilder.class, HasMenus.class });
  }

  public UserMenu createInstance(final ContextManager contextManager) {
    final UserMenu instance = new UserMenu();
    setIncompleteInstance(instance);
    final UserMenuViewImpl UserMenu_userMenuView = (UserMenuViewImpl) contextManager.getInstance("Type_factory__o_u_c_v_p_m_UserMenuViewImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, UserMenu_userMenuView);
    UserMenu_UserMenuView_userMenuView(instance, UserMenu_userMenuView);
    final User UserMenu_user = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, UserMenu_user);
    UserMenu_User_user(instance, UserMenu_user);
    final DefaultAuthorizationManager UserMenu_authzManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    UserMenu_AuthorizationManager_authzManager(instance, UserMenu_authzManager);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UserMenu instance) {
    UserMenu_setup(instance);
  }

  native static User UserMenu_User_user(UserMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.UserMenu::user;
  }-*/;

  native static void UserMenu_User_user(UserMenu instance, User value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.UserMenu::user = value;
  }-*/;

  native static UserMenuView UserMenu_UserMenuView_userMenuView(UserMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.UserMenu::userMenuView;
  }-*/;

  native static void UserMenu_UserMenuView_userMenuView(UserMenu instance, UserMenuView value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.UserMenu::userMenuView = value;
  }-*/;

  native static AuthorizationManager UserMenu_AuthorizationManager_authzManager(UserMenu instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.UserMenu::authzManager;
  }-*/;

  native static void UserMenu_AuthorizationManager_authzManager(UserMenu instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.UserMenu::authzManager = value;
  }-*/;

  public native static void UserMenu_setup(UserMenu instance) /*-{
    instance.@org.uberfire.client.views.pfly.menu.UserMenu::setup()();
  }-*/;
}