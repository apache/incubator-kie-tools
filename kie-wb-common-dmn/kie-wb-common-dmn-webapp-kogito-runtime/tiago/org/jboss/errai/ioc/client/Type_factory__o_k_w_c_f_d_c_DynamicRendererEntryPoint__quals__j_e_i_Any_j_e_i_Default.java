package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.forms.dynamic.client.DynamicRendererEntryPoint;

public class Type_factory__o_k_w_c_f_d_c_DynamicRendererEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicRendererEntryPoint> { public Type_factory__o_k_w_c_f_d_c_DynamicRendererEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DynamicRendererEntryPoint.class, "Type_factory__o_k_w_c_f_d_c_DynamicRendererEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { DynamicRendererEntryPoint.class, Object.class });
  }

  public DynamicRendererEntryPoint createInstance(final ContextManager contextManager) {
    final SyncBeanManager _beanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final DynamicRendererEntryPoint instance = new DynamicRendererEntryPoint(_beanManager_0);
    registerDependentScopedReference(instance, _beanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DynamicRendererEntryPoint instance) {
    instance.init();
  }
}