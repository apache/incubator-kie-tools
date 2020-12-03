package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView;
import org.uberfire.client.workbench.widgets.notifications.NotificationManager.View;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_v_p_n_NotificationPopupsManagerView__quals__j_e_i_Any_j_e_i_Default extends Factory<NotificationPopupsManagerView> { public Type_factory__o_u_c_v_p_n_NotificationPopupsManagerView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NotificationPopupsManagerView.class, "Type_factory__o_u_c_v_p_n_NotificationPopupsManagerView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NotificationPopupsManagerView.class, Object.class, View.class });
  }

  public NotificationPopupsManagerView createInstance(final ContextManager contextManager) {
    final NotificationPopupsManagerView instance = new NotificationPopupsManagerView();
    setIncompleteInstance(instance);
    final PlaceManagerImpl NotificationPopupsManagerView_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    NotificationPopupsManagerView_PlaceManager_placeManager(instance, NotificationPopupsManagerView_placeManager);
    final DefaultAuthorizationManager NotificationPopupsManagerView_authorizationManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    NotificationPopupsManagerView_AuthorizationManager_authorizationManager(instance, NotificationPopupsManagerView_authorizationManager);
    final User NotificationPopupsManagerView_user = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, NotificationPopupsManagerView_user);
    NotificationPopupsManagerView_User_user(instance, NotificationPopupsManagerView_user);
    final ActivityBeansCache NotificationPopupsManagerView_activityBeansCache = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    NotificationPopupsManagerView_ActivityBeansCache_activityBeansCache(instance, NotificationPopupsManagerView_activityBeansCache);
    setIncompleteInstance(null);
    return instance;
  }

  native static AuthorizationManager NotificationPopupsManagerView_AuthorizationManager_authorizationManager(NotificationPopupsManagerView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::authorizationManager;
  }-*/;

  native static void NotificationPopupsManagerView_AuthorizationManager_authorizationManager(NotificationPopupsManagerView instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::authorizationManager = value;
  }-*/;

  native static User NotificationPopupsManagerView_User_user(NotificationPopupsManagerView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::user;
  }-*/;

  native static void NotificationPopupsManagerView_User_user(NotificationPopupsManagerView instance, User value) /*-{
    instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::user = value;
  }-*/;

  native static ActivityBeansCache NotificationPopupsManagerView_ActivityBeansCache_activityBeansCache(NotificationPopupsManagerView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::activityBeansCache;
  }-*/;

  native static void NotificationPopupsManagerView_ActivityBeansCache_activityBeansCache(NotificationPopupsManagerView instance, ActivityBeansCache value) /*-{
    instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::activityBeansCache = value;
  }-*/;

  native static PlaceManager NotificationPopupsManagerView_PlaceManager_placeManager(NotificationPopupsManagerView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::placeManager;
  }-*/;

  native static void NotificationPopupsManagerView_PlaceManager_placeManager(NotificationPopupsManagerView instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.views.pfly.notifications.NotificationPopupsManagerView::placeManager = value;
  }-*/;
}