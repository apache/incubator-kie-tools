package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.slf4j.Logger;
import org.uberfire.client.plugin.RuntimePluginsServiceProxy;
import org.uberfire.client.plugin.RuntimePluginsServiceProxyClientImpl;

public class Type_factory__o_u_c_p_RuntimePluginsServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<RuntimePluginsServiceProxyClientImpl> { public Type_factory__o_u_c_p_RuntimePluginsServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RuntimePluginsServiceProxyClientImpl.class, "Type_factory__o_u_c_p_RuntimePluginsServiceProxyClientImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RuntimePluginsServiceProxyClientImpl.class, Object.class, RuntimePluginsServiceProxy.class });
  }

  public RuntimePluginsServiceProxyClientImpl createInstance(final ContextManager contextManager) {
    final RuntimePluginsServiceProxyClientImpl instance = new RuntimePluginsServiceProxyClientImpl();
    setIncompleteInstance(instance);
    final Logger RuntimePluginsServiceProxyClientImpl_logger = (Logger) contextManager.getInstance("ExtensionProvided_factory__o_s_Logger__quals__Universal_2");
    registerDependentScopedReference(instance, RuntimePluginsServiceProxyClientImpl_logger);
    RuntimePluginsServiceProxyClientImpl_Logger_logger(instance, RuntimePluginsServiceProxyClientImpl_logger);
    setIncompleteInstance(null);
    return instance;
  }

  native static Logger RuntimePluginsServiceProxyClientImpl_Logger_logger(RuntimePluginsServiceProxyClientImpl instance) /*-{
    return instance.@org.uberfire.client.plugin.RuntimePluginsServiceProxyClientImpl::logger;
  }-*/;

  native static void RuntimePluginsServiceProxyClientImpl_Logger_logger(RuntimePluginsServiceProxyClientImpl instance, Logger value) /*-{
    instance.@org.uberfire.client.plugin.RuntimePluginsServiceProxyClientImpl::logger = value;
  }-*/;
}