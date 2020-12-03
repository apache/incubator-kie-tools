package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.BasicUserCache;
import org.slf4j.Logger;

public class Type_factory__o_j_e_s_s_a_i_BasicUserCache__quals__j_e_i_Any_j_e_i_Default extends Factory<BasicUserCache> { public Type_factory__o_j_e_s_s_a_i_BasicUserCache__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BasicUserCache.class, "Type_factory__o_j_e_s_s_a_i_BasicUserCache__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BasicUserCache.class, Object.class });
  }

  public BasicUserCache createInstance(final ContextManager contextManager) {
    final BasicUserCache instance = new BasicUserCache();
    setIncompleteInstance(instance);
    final Logger BasicUserCache_logger = (Logger) contextManager.getInstance("ExtensionProvided_factory__o_s_Logger__quals__Universal_1");
    registerDependentScopedReference(instance, BasicUserCache_logger);
    BasicUserCache_Logger_logger(instance, BasicUserCache_logger);
    setIncompleteInstance(null);
    return instance;
  }

  native static Logger BasicUserCache_Logger_logger(BasicUserCache instance) /*-{
    return instance.@org.jboss.errai.security.shared.api.identity.BasicUserCache::logger;
  }-*/;

  native static void BasicUserCache_Logger_logger(BasicUserCache instance, Logger value) /*-{
    instance.@org.jboss.errai.security.shared.api.identity.BasicUserCache::logger = value;
  }-*/;
}