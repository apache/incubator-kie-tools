package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.builtin.DisposerProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

public class Type_factory__o_j_e_i_c_a_b_DisposerProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DisposerProvider> { public Type_factory__o_j_e_i_c_a_b_DisposerProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DisposerProvider.class, "Type_factory__o_j_e_i_c_a_b_DisposerProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DisposerProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public DisposerProvider createInstance(final ContextManager contextManager) {
    final DisposerProvider instance = new DisposerProvider();
    setIncompleteInstance(instance);
    final SyncBeanManager DisposerProvider_beanManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DisposerProvider_beanManager);
    DisposerProvider_SyncBeanManager_beanManager(instance, DisposerProvider_beanManager);
    setIncompleteInstance(null);
    return instance;
  }

  native static SyncBeanManager DisposerProvider_SyncBeanManager_beanManager(DisposerProvider instance) /*-{
    return instance.@org.jboss.errai.ioc.client.api.builtin.DisposerProvider::beanManager;
  }-*/;

  native static void DisposerProvider_SyncBeanManager_beanManager(DisposerProvider instance, SyncBeanManager value) /*-{
    instance.@org.jboss.errai.ioc.client.api.builtin.DisposerProvider::beanManager = value;
  }-*/;
}