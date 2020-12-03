package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.editor.JSNativeEditor;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.client.plugin.RuntimePluginsServiceProxyClientImpl;

public class Type_factory__o_u_c_e_JSNativeEditor__quals__j_e_i_Any_j_e_i_Default extends Factory<JSNativeEditor> { public Type_factory__o_u_c_e_JSNativeEditor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(JSNativeEditor.class, "Type_factory__o_u_c_e_JSNativeEditor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { JSNativeEditor.class, JSNativePlugin.class, Object.class });
  }

  public JSNativeEditor createInstance(final ContextManager contextManager) {
    final JSNativeEditor instance = new JSNativeEditor();
    setIncompleteInstance(instance);
    final RuntimePluginsServiceProxyClientImpl JSNativePlugin_runtimePluginsService = (RuntimePluginsServiceProxyClientImpl) contextManager.getInstance("Type_factory__o_u_c_p_RuntimePluginsServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, JSNativePlugin_runtimePluginsService);
    JSNativePlugin_RuntimePluginsServiceProxy_runtimePluginsService(instance, JSNativePlugin_runtimePluginsService);
    setIncompleteInstance(null);
    return instance;
  }

  native static RuntimePluginsServiceProxy JSNativePlugin_RuntimePluginsServiceProxy_runtimePluginsService(JSNativePlugin instance) /*-{
    return instance.@org.uberfire.client.plugin.JSNativePlugin::runtimePluginsService;
  }-*/;

  native static void JSNativePlugin_RuntimePluginsServiceProxy_runtimePluginsService(JSNativePlugin instance, RuntimePluginsServiceProxy value) /*-{
    instance.@org.uberfire.client.plugin.JSNativePlugin::runtimePluginsService = value;
  }-*/;
}