package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.preferences.client.store.PreferenceBeanStoreClientImpl;
import org.uberfire.preferences.shared.bean.BasePreference;
import org.uberfire.preferences.shared.bean.BasePreferenceBean;
import org.uberfire.preferences.shared.bean.Preference;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMock;
import org.uberfire.preferences.shared.bean.mock.PortablePreferenceMockBeanGeneratedImpl;

public class Type_factory__o_u_p_s_b_m_PortablePreferenceMockBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PortablePreferenceMockBeanGeneratedImpl> { public Type_factory__o_u_p_s_b_m_PortablePreferenceMockBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PortablePreferenceMockBeanGeneratedImpl.class, "Type_factory__o_u_p_s_b_m_PortablePreferenceMockBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PortablePreferenceMockBeanGeneratedImpl.class, PortablePreferenceMock.class, Object.class, BasePreference.class, Preference.class, BasePreferenceBean.class });
  }

  public PortablePreferenceMockBeanGeneratedImpl createInstance(final ContextManager contextManager) {
    final PreferenceBeanStore _store_0 = (PreferenceBeanStoreClientImpl) contextManager.getInstance("Type_factory__o_u_p_c_s_PreferenceBeanStoreClientImpl__quals__j_e_i_Any_j_e_i_Default");
    final PortablePreferenceMockBeanGeneratedImpl instance = new PortablePreferenceMockBeanGeneratedImpl(_store_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}