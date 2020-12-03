package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.services.shared.preferences.config.WorkbenchPreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.PreferenceScopeFactory;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.impl.PreferenceScopeFactoryImpl;

public class Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategy__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPreferenceScopeResolutionStrategy> { public Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategy__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchPreferenceScopeResolutionStrategy.class, "Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeResolutionStrategy__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchPreferenceScopeResolutionStrategy.class, Object.class, PreferenceScopeResolutionStrategy.class });
  }

  public WorkbenchPreferenceScopeResolutionStrategy createInstance(final ContextManager contextManager) {
    final PreferenceScopeFactory _scopeFactory_0 = (PreferenceScopeFactoryImpl) contextManager.getInstance("Type_factory__o_u_p_s_i_PreferenceScopeFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchPreferenceScopeResolutionStrategy instance = new WorkbenchPreferenceScopeResolutionStrategy(_scopeFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}