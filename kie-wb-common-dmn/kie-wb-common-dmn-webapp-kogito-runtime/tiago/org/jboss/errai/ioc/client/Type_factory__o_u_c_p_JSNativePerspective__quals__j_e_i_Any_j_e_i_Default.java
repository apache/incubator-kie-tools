package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.perspective.JSNativePerspective;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.WorkbenchServicesProxyClientImpl;

public class Type_factory__o_u_c_p_JSNativePerspective__quals__j_e_i_Any_j_e_i_Default extends Factory<JSNativePerspective> { public Type_factory__o_u_c_p_JSNativePerspective__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(JSNativePerspective.class, "Type_factory__o_u_c_p_JSNativePerspective__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { JSNativePerspective.class, Object.class });
  }

  public JSNativePerspective createInstance(final ContextManager contextManager) {
    final JSNativePerspective instance = new JSNativePerspective();
    setIncompleteInstance(instance);
    final PanelManagerImpl JSNativePerspective_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    JSNativePerspective_PanelManager_panelManager(instance, JSNativePerspective_panelManager);
    final WorkbenchServicesProxyClientImpl JSNativePerspective_wbServices = (WorkbenchServicesProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, JSNativePerspective_wbServices);
    JSNativePerspective_WorkbenchServicesProxy_wbServices(instance, JSNativePerspective_wbServices);
    final PlaceManagerImpl JSNativePerspective_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    JSNativePerspective_PlaceManager_placeManager(instance, JSNativePerspective_placeManager);
    final ActivityManagerImpl JSNativePerspective_activityManager = (ActivityManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    JSNativePerspective_ActivityManager_activityManager(instance, JSNativePerspective_activityManager);
    setIncompleteInstance(null);
    return instance;
  }

  native static ActivityManager JSNativePerspective_ActivityManager_activityManager(JSNativePerspective instance) /*-{
    return instance.@org.uberfire.client.perspective.JSNativePerspective::activityManager;
  }-*/;

  native static void JSNativePerspective_ActivityManager_activityManager(JSNativePerspective instance, ActivityManager value) /*-{
    instance.@org.uberfire.client.perspective.JSNativePerspective::activityManager = value;
  }-*/;

  native static WorkbenchServicesProxy JSNativePerspective_WorkbenchServicesProxy_wbServices(JSNativePerspective instance) /*-{
    return instance.@org.uberfire.client.perspective.JSNativePerspective::wbServices;
  }-*/;

  native static void JSNativePerspective_WorkbenchServicesProxy_wbServices(JSNativePerspective instance, WorkbenchServicesProxy value) /*-{
    instance.@org.uberfire.client.perspective.JSNativePerspective::wbServices = value;
  }-*/;

  native static PlaceManager JSNativePerspective_PlaceManager_placeManager(JSNativePerspective instance) /*-{
    return instance.@org.uberfire.client.perspective.JSNativePerspective::placeManager;
  }-*/;

  native static void JSNativePerspective_PlaceManager_placeManager(JSNativePerspective instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.perspective.JSNativePerspective::placeManager = value;
  }-*/;

  native static PanelManager JSNativePerspective_PanelManager_panelManager(JSNativePerspective instance) /*-{
    return instance.@org.uberfire.client.perspective.JSNativePerspective::panelManager;
  }-*/;

  native static void JSNativePerspective_PanelManager_panelManager(JSNativePerspective instance, PanelManager value) /*-{
    instance.@org.uberfire.client.perspective.JSNativePerspective::panelManager = value;
  }-*/;
}