package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.services.shared.preferences.config.WorkbenchPreferenceScopeTypes;
import org.uberfire.preferences.client.scope.ClientUsernameProvider;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.UsernameProvider;

public class Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeTypes__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPreferenceScopeTypes> { public Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeTypes__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchPreferenceScopeTypes.class, "Type_factory__o_k_w_c_s_s_p_c_WorkbenchPreferenceScopeTypes__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchPreferenceScopeTypes.class, Object.class, PreferenceScopeTypes.class });
  }

  public WorkbenchPreferenceScopeTypes createInstance(final ContextManager contextManager) {
    final UsernameProvider _usernameProvider_0 = (ClientUsernameProvider) contextManager.getInstance("Type_factory__o_u_p_c_s_ClientUsernameProvider__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchPreferenceScopeTypes instance = new WorkbenchPreferenceScopeTypes(_usernameProvider_0);
    registerDependentScopedReference(instance, _usernameProvider_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}