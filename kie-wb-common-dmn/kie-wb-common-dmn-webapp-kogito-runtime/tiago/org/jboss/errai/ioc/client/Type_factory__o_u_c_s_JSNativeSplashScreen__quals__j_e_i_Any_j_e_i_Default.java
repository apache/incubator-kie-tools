package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.client.plugin.RuntimePluginsServiceProxyClientImpl;
import org.uberfire.client.splash.JSNativeSplashScreen;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.WorkbenchServicesProxyClientImpl;

public class Type_factory__o_u_c_s_JSNativeSplashScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<JSNativeSplashScreen> { public Type_factory__o_u_c_s_JSNativeSplashScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(JSNativeSplashScreen.class, "Type_factory__o_u_c_s_JSNativeSplashScreen__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { JSNativeSplashScreen.class, JSNativePlugin.class, Object.class });
  }

  public JSNativeSplashScreen createInstance(final ContextManager contextManager) {
    final JSNativeSplashScreen instance = new JSNativeSplashScreen();
    setIncompleteInstance(instance);
    final RuntimePluginsServiceProxyClientImpl JSNativePlugin_runtimePluginsService = (RuntimePluginsServiceProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_p_RuntimePluginsServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, JSNativePlugin_runtimePluginsService);
    JSNativePlugin_RuntimePluginsServiceProxy_runtimePluginsService(instance, JSNativePlugin_runtimePluginsService);
    final WorkbenchServicesProxyClientImpl JSNativeSplashScreen_wbServices = (WorkbenchServicesProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_w_WorkbenchServicesProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, JSNativeSplashScreen_wbServices);
    JSNativeSplashScreen_WorkbenchServicesProxy_wbServices(instance, JSNativeSplashScreen_wbServices);
    setIncompleteInstance(null);
    return instance;
  }

  native static RuntimePluginsServiceProxy JSNativePlugin_RuntimePluginsServiceProxy_runtimePluginsService(JSNativePlugin instance) /*-{
    return instance.@org.uberfire.client.plugin.JSNativePlugin::runtimePluginsService;
  }-*/;

  native static void JSNativePlugin_RuntimePluginsServiceProxy_runtimePluginsService(JSNativePlugin instance, RuntimePluginsServiceProxy value) /*-{
    instance.@org.uberfire.client.plugin.JSNativePlugin::runtimePluginsService = value;
  }-*/;

  native static WorkbenchServicesProxy JSNativeSplashScreen_WorkbenchServicesProxy_wbServices(JSNativeSplashScreen instance) /*-{
    return instance.@org.uberfire.client.splash.JSNativeSplashScreen::wbServices;
  }-*/;

  native static void JSNativeSplashScreen_WorkbenchServicesProxy_wbServices(JSNativeSplashScreen instance, WorkbenchServicesProxy value) /*-{
    instance.@org.uberfire.client.splash.JSNativeSplashScreen::wbServices = value;
  }-*/;
}