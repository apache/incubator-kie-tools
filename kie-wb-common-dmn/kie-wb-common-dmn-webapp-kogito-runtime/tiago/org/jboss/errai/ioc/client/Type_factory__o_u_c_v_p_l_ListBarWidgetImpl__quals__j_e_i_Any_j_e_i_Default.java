package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.panels.MultiPartWidget;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.listbar.ListbarPreferences;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_v_p_l_ListBarWidgetImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListBarWidgetImpl> { public Type_factory__o_u_c_v_p_l_ListBarWidgetImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListBarWidgetImpl.class, "Type_factory__o_u_c_v_p_l_ListBarWidgetImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListBarWidgetImpl.class, ResizeComposite.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, ListBarWidget.class, MultiPartWidget.class, HasBeforeSelectionHandlers.class, HasSelectionHandlers.class });
  }

  public ListBarWidgetImpl createInstance(final ContextManager contextManager) {
    final ListBarWidgetImpl instance = new ListBarWidgetImpl();
    setIncompleteInstance(instance);
    final Instance ListBarWidgetImpl_optionalListBarPrefs = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { ListbarPreferences.class }, new Annotation[] { });
    registerDependentScopedReference(instance, ListBarWidgetImpl_optionalListBarPrefs);
    ListBarWidgetImpl_Instance_optionalListBarPrefs(instance, ListBarWidgetImpl_optionalListBarPrefs);
    final PanelManagerImpl ListBarWidgetImpl_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ListBarWidgetImpl_PanelManager_panelManager(instance, ListBarWidgetImpl_panelManager);
    final DefaultAuthorizationManager ListBarWidgetImpl_authzManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    ListBarWidgetImpl_AuthorizationManager_authzManager(instance, ListBarWidgetImpl_authzManager);
    final User ListBarWidgetImpl_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ListBarWidgetImpl_identity);
    ListBarWidgetImpl_User_identity(instance, ListBarWidgetImpl_identity);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ListBarWidgetImpl instance) {
    ListBarWidgetImpl_postConstruct(instance);
  }

  native static AuthorizationManager ListBarWidgetImpl_AuthorizationManager_authzManager(ListBarWidgetImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::authzManager;
  }-*/;

  native static void ListBarWidgetImpl_AuthorizationManager_authzManager(ListBarWidgetImpl instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::authzManager = value;
  }-*/;

  native static PanelManager ListBarWidgetImpl_PanelManager_panelManager(ListBarWidgetImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::panelManager;
  }-*/;

  native static void ListBarWidgetImpl_PanelManager_panelManager(ListBarWidgetImpl instance, PanelManager value) /*-{
    instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::panelManager = value;
  }-*/;

  native static Instance ListBarWidgetImpl_Instance_optionalListBarPrefs(ListBarWidgetImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::optionalListBarPrefs;
  }-*/;

  native static void ListBarWidgetImpl_Instance_optionalListBarPrefs(ListBarWidgetImpl instance, Instance<ListbarPreferences> value) /*-{
    instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::optionalListBarPrefs = value;
  }-*/;

  native static User ListBarWidgetImpl_User_identity(ListBarWidgetImpl instance) /*-{
    return instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::identity;
  }-*/;

  native static void ListBarWidgetImpl_User_identity(ListBarWidgetImpl instance, User value) /*-{
    instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::identity = value;
  }-*/;

  public native static void ListBarWidgetImpl_postConstruct(ListBarWidgetImpl instance) /*-{
    instance.@org.uberfire.client.views.pfly.listbar.ListBarWidgetImpl::postConstruct()();
  }-*/;
}