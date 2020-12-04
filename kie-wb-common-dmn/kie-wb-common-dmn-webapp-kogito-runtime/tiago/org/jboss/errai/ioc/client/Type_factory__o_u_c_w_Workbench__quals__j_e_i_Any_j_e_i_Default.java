package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.identity.User;
import org.slf4j.Logger;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.VFSServiceProxy;
import org.uberfire.client.workbench.VFSServiceProxyClientImpl;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.WorkbenchCustomStandalonePerspectiveDefinition;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

public class Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default extends Factory<Workbench> { public Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Workbench.class, "Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { Workbench.class, Object.class });
  }

  public Workbench createInstance(final ContextManager contextManager) {
    final Workbench instance = new Workbench();
    setIncompleteInstance(instance);
    final User Workbench_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, Workbench_identity);
    Workbench_User_identity(instance, Workbench_identity);
    final LayoutSelection Workbench_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    Workbench_LayoutSelection_layoutSelection(instance, Workbench_layoutSelection);
    final DefaultPermissionManager Workbench_permissionManager = (DefaultPermissionManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default");
    Workbench_PermissionManager_permissionManager(instance, Workbench_permissionManager);
    final DefaultAuthorizationManager Workbench_authorizationManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    Workbench_AuthorizationManager_authorizationManager(instance, Workbench_authorizationManager);
    final PlaceManagerImpl Workbench_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    Workbench_PlaceManager_placeManager(instance, Workbench_placeManager);
    final SyncBeanManager Workbench_iocManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, Workbench_iocManager);
    Workbench_SyncBeanManager_iocManager(instance, Workbench_iocManager);
    final VFSServiceProxyClientImpl Workbench_vfsService = (VFSServiceProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_w_VFSServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, Workbench_vfsService);
    Workbench_VFSServiceProxy_vfsService(instance, Workbench_vfsService);
    final ManagedInstance Workbench_workbenchCustomStandalonePerspectiveDefinition = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { WorkbenchCustomStandalonePerspectiveDefinition.class }, new Annotation[] { });
    registerDependentScopedReference(instance, Workbench_workbenchCustomStandalonePerspectiveDefinition);
    Workbench_ManagedInstance_workbenchCustomStandalonePerspectiveDefinition(instance, Workbench_workbenchCustomStandalonePerspectiveDefinition);
    final ActivityBeansCache Workbench_activityBeansCache = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    Workbench_ActivityBeansCache_activityBeansCache(instance, Workbench_activityBeansCache);
    final Event Workbench_appReady = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ApplicationReadyEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, Workbench_appReady);
    Workbench_Event_appReady(instance, Workbench_appReady);
    final Logger Workbench_logger = (Logger) contextManager.getInstance("ExtensionProvided_factory__o_s_Logger__quals__Universal");
    registerDependentScopedReference(instance, Workbench_logger);
    Workbench_Logger_logger(instance, Workbench_logger);
    InitVotes.registerOneTimeInitCallback(new Runnable() {
      public void run() {
        Workbench_afterInit(instance);
      }
    });
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final Workbench instance) {
    Workbench_earlyInit(instance);
  }

  native static AuthorizationManager Workbench_AuthorizationManager_authorizationManager(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::authorizationManager;
  }-*/;

  native static void Workbench_AuthorizationManager_authorizationManager(Workbench instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::authorizationManager = value;
  }-*/;

  native static PermissionManager Workbench_PermissionManager_permissionManager(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::permissionManager;
  }-*/;

  native static void Workbench_PermissionManager_permissionManager(Workbench instance, PermissionManager value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::permissionManager = value;
  }-*/;

  native static LayoutSelection Workbench_LayoutSelection_layoutSelection(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::layoutSelection;
  }-*/;

  native static void Workbench_LayoutSelection_layoutSelection(Workbench instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::layoutSelection = value;
  }-*/;

  native static SyncBeanManager Workbench_SyncBeanManager_iocManager(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::iocManager;
  }-*/;

  native static void Workbench_SyncBeanManager_iocManager(Workbench instance, SyncBeanManager value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::iocManager = value;
  }-*/;

  native static Logger Workbench_Logger_logger(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::logger;
  }-*/;

  native static void Workbench_Logger_logger(Workbench instance, Logger value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::logger = value;
  }-*/;

  native static ManagedInstance Workbench_ManagedInstance_workbenchCustomStandalonePerspectiveDefinition(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::workbenchCustomStandalonePerspectiveDefinition;
  }-*/;

  native static void Workbench_ManagedInstance_workbenchCustomStandalonePerspectiveDefinition(Workbench instance, ManagedInstance<WorkbenchCustomStandalonePerspectiveDefinition> value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::workbenchCustomStandalonePerspectiveDefinition = value;
  }-*/;

  native static User Workbench_User_identity(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::identity;
  }-*/;

  native static void Workbench_User_identity(Workbench instance, User value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::identity = value;
  }-*/;

  native static PlaceManager Workbench_PlaceManager_placeManager(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::placeManager;
  }-*/;

  native static void Workbench_PlaceManager_placeManager(Workbench instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::placeManager = value;
  }-*/;

  native static VFSServiceProxy Workbench_VFSServiceProxy_vfsService(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::vfsService;
  }-*/;

  native static void Workbench_VFSServiceProxy_vfsService(Workbench instance, VFSServiceProxy value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::vfsService = value;
  }-*/;

  native static Event Workbench_Event_appReady(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::appReady;
  }-*/;

  native static void Workbench_Event_appReady(Workbench instance, Event<ApplicationReadyEvent> value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::appReady = value;
  }-*/;

  native static ActivityBeansCache Workbench_ActivityBeansCache_activityBeansCache(Workbench instance) /*-{
    return instance.@org.uberfire.client.workbench.Workbench::activityBeansCache;
  }-*/;

  native static void Workbench_ActivityBeansCache_activityBeansCache(Workbench instance, ActivityBeansCache value) /*-{
    instance.@org.uberfire.client.workbench.Workbench::activityBeansCache = value;
  }-*/;

  public native static void Workbench_earlyInit(Workbench instance) /*-{
    instance.@org.uberfire.client.workbench.Workbench::earlyInit()();
  }-*/;

  public native static void Workbench_afterInit(Workbench instance) /*-{
    instance.@org.uberfire.client.workbench.Workbench::afterInit()();
  }-*/;
}