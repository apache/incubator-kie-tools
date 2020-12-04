package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.marshalling.DMNMarshallerImportsHelper;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNIncludedNodeFactory;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentService;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsClientHelper> { public Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNMarshallerImportsClientHelper.class, "Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNMarshallerImportsClientHelper.class, Object.class, DMNMarshallerImportsHelper.class });
  }

  public DMNMarshallerImportsClientHelper createInstance(final ContextManager contextManager) {
    final Promises _promises_2 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsService _dmnImportsService_0 = (DMNMarshallerImportsService) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsService__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsContentService _dmnImportsContentService_1 = (DMNMarshallerImportsContentServiceKogitoImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_i_DMNMarshallerImportsContentServiceKogitoImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNIncludedNodeFactory _includedModelFactory_3 = (DMNIncludedNodeFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_i_DMNIncludedNodeFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsClientHelper instance = new DMNMarshallerImportsClientHelper(_dmnImportsService_0, _dmnImportsContentService_1, _promises_2, _includedModelFactory_3);
    registerDependentScopedReference(instance, _promises_2);
    registerDependentScopedReference(instance, _dmnImportsContentService_1);
    registerDependentScopedReference(instance, _includedModelFactory_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}