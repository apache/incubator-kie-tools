package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.jsbridge.client.AppFormerJsBridge;
import org.uberfire.jsbridge.client.loading.AppFormerJsActivityLoader;

public class Type_factory__o_u_j_c_AppFormerJsBridge__quals__j_e_i_Any_j_e_i_Default extends Factory<AppFormerJsBridge> { public Type_factory__o_u_j_c_AppFormerJsBridge__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppFormerJsBridge.class, "Type_factory__o_u_j_c_AppFormerJsBridge__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppFormerJsBridge.class, Object.class });
  }

  public AppFormerJsBridge createInstance(final ContextManager contextManager) {
    final Workbench _workbench_0 = (Workbench) contextManager.getInstance("Type_factory__o_u_c_w_Workbench__quals__j_e_i_Any_j_e_i_Default");
    final AppFormerJsActivityLoader _appFormerJsLoader_1 = (AppFormerJsActivityLoader) contextManager.getInstance("Type_factory__o_u_j_c_l_AppFormerJsActivityLoader__quals__j_e_i_Any_j_e_i_Default");
    final Promises _promises_2 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final AppFormerJsBridge instance = new AppFormerJsBridge(_workbench_0, _appFormerJsLoader_1, _promises_2);
    registerDependentScopedReference(instance, _promises_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}