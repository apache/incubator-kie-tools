package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.TimeZonesProvider;

public class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNClientServicesProxyImpl> { public Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNClientServicesProxyImpl.class, "Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNClientServicesProxyImpl.class, Object.class, DMNClientServicesProxy.class });
  }

  public DMNClientServicesProxyImpl createInstance(final ContextManager contextManager) {
    final TimeZonesProvider _timeZonesProvider_0 = (TimeZonesProvider) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsClientHelper _kogitoImportsHelper_1 = (DMNMarshallerImportsClientHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default");
    final DMNClientServicesProxyImpl instance = new DMNClientServicesProxyImpl(_timeZonesProvider_0, _kogitoImportsHelper_1);
    registerDependentScopedReference(instance, _kogitoImportsHelper_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}