package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.JSEntryPoint;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.client.plugin.RuntimePluginsServiceProxyClientImpl;
import org.uberfire.client.workbench.Workbench;

public class Type_factory__o_u_c_JSEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<JSEntryPoint> { public Type_factory__o_u_c_JSEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(JSEntryPoint.class, "Type_factory__o_u_c_JSEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { JSEntryPoint.class, Object.class });
  }

  public JSEntryPoint createInstance(final ContextManager contextManager) {
    final JSEntryPoint instance = new JSEntryPoint();
    setIncompleteInstance(instance);
    final RuntimePluginsServiceProxyClientImpl JSEntryPoint_runtimePluginsService = (RuntimePluginsServiceProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_p_RuntimePluginsServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, JSEntryPoint_runtimePluginsService);
    JSEntryPoint_RuntimePluginsServiceProxy_runtimePluginsService(instance, JSEntryPoint_runtimePluginsService);
    final Workbench JSEntryPoint_workbench = (Workbench) contextManager.getInstance("Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default");
    JSEntryPoint_Workbench_workbench(instance, JSEntryPoint_workbench);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final JSEntryPoint instance) {
    instance.init();
  }

  native static Workbench JSEntryPoint_Workbench_workbench(JSEntryPoint instance) /*-{
    return instance.@org.uberfire.client.JSEntryPoint::workbench;
  }-*/;

  native static void JSEntryPoint_Workbench_workbench(JSEntryPoint instance, Workbench value) /*-{
    instance.@org.uberfire.client.JSEntryPoint::workbench = value;
  }-*/;

  native static RuntimePluginsServiceProxy JSEntryPoint_RuntimePluginsServiceProxy_runtimePluginsService(JSEntryPoint instance) /*-{
    return instance.@org.uberfire.client.JSEntryPoint::runtimePluginsService;
  }-*/;

  native static void JSEntryPoint_RuntimePluginsServiceProxy_runtimePluginsService(JSEntryPoint instance, RuntimePluginsServiceProxy value) /*-{
    instance.@org.uberfire.client.JSEntryPoint::runtimePluginsService = value;
  }-*/;
}