package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.project.preferences.GAVPreferencesBeanGeneratedImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.kogito.webapp.base.shared.PreferenceScopeResolutionStrategyMock;
import org.uberfire.preferences.client.store.PreferenceBeanStoreClientImpl;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferenceBean;
import org.uberfire.preferences.shared.bean.Preference;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;

public class Type_factory__o_g_c_s_p_p_GAVPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GAVPreferencesBeanGeneratedImpl> { public Type_factory__o_g_c_s_p_p_GAVPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GAVPreferencesBeanGeneratedImpl.class, "Type_factory__o_g_c_s_p_p_GAVPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GAVPreferencesBeanGeneratedImpl.class, GAVPreferences.class, Object.class, BasePreference.class, Preference.class, BasePreferenceBean.class });
  }

  public GAVPreferencesBeanGeneratedImpl createInstance(final ContextManager contextManager) {
    final PreferenceBeanStore _store_0 = (PreferenceBeanStoreClientImpl) contextManager.getInstance("Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default");
    final PreferenceScopeResolutionStrategy _resolutionStrategy_1 = (PreferenceScopeResolutionStrategyMock) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_s_PreferenceScopeResolutionStrategyMock__quals__j_e_i_Any_o_u_a_Customizable");
    final GAVPreferencesBeanGeneratedImpl instance = new GAVPreferencesBeanGeneratedImpl(_store_0, _resolutionStrategy_1);
    registerDependentScopedReference(instance, _resolutionStrategy_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}