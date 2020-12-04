package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;

public class Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationDRDsFactory> { public Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDocumentationDRDsFactory.class, "Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDocumentationDRDsFactory.class, Object.class });
  }

  public DMNDocumentationDRDsFactory createInstance(final ContextManager contextManager) {
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final BoxedExpressionHelper _expressionHelper_1 = (BoxedExpressionHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationDRDsFactory instance = new DMNDocumentationDRDsFactory(_sessionManager_0, _expressionHelper_1);
    registerDependentScopedReference(instance, _expressionHelper_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}