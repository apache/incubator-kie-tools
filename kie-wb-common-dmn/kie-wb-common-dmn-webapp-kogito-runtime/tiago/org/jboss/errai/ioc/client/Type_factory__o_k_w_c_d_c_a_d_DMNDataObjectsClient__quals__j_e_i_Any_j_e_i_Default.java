package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.api.DMNServiceClient;
import org.kie.workbench.common.dmn.client.api.dataobjects.DMNDataObjectsClient;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;

public class Type_factory__o_k_w_c_d_c_a_d_DMNDataObjectsClient__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDataObjectsClient> { public Type_factory__o_k_w_c_d_c_a_d_DMNDataObjectsClient__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDataObjectsClient.class, "Type_factory__o_k_w_c_d_c_a_d_DMNDataObjectsClient__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDataObjectsClient.class, DMNServiceClient.class, Object.class });
  }

  public DMNDataObjectsClient createInstance(final ContextManager contextManager) {
    final DMNClientServicesProxy _clientServicesProxy_0 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNDataObjectsClient instance = new DMNDataObjectsClient(_clientServicesProxy_0);
    registerDependentScopedReference(instance, _clientServicesProxy_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}