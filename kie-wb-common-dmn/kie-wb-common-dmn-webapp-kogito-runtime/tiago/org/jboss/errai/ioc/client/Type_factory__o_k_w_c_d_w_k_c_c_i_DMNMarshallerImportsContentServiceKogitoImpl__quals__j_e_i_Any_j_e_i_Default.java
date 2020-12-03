package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerApi;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsContentService;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.included.DMNMarshallerImportsContentServiceKogitoImpl;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_d_w_k_c_c_i_DMNMarshallerImportsContentServiceKogitoImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNMarshallerImportsContentServiceKogitoImpl> { public Type_factory__o_k_w_c_d_w_k_c_c_i_DMNMarshallerImportsContentServiceKogitoImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNMarshallerImportsContentServiceKogitoImpl.class, "Type_factory__o_k_w_c_d_w_k_c_c_i_DMNMarshallerImportsContentServiceKogitoImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNMarshallerImportsContentServiceKogitoImpl.class, Object.class, DMNMarshallerImportsContentService.class });
  }

  public DMNMarshallerImportsContentServiceKogitoImpl createInstance(final ContextManager contextManager) {
    final KogitoResourceContentService _contentService_0 = (KogitoResourceContentService) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default");
    final PMMLEditorMarshallerApi _pmmlEditorMarshallerApi_2 = (PMMLEditorMarshallerApi) contextManager.getInstance("Producer_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerApi__quals__j_e_i_Any_j_e_i_Default");
    final Promises _promises_1 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsContentServiceKogitoImpl instance = new DMNMarshallerImportsContentServiceKogitoImpl(_contentService_0, _promises_1, _pmmlEditorMarshallerApi_2);
    registerDependentScopedReference(instance, _promises_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}