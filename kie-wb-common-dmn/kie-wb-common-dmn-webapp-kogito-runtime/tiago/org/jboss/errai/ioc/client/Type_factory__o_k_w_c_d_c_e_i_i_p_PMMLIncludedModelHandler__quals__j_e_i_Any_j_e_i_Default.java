package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DRGElementHandler;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.PMMLIncludedModelHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;

public class Type_factory__o_k_w_c_d_c_e_i_i_p_PMMLIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLIncludedModelHandler> { public Type_factory__o_k_w_c_d_c_e_i_i_p_PMMLIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PMMLIncludedModelHandler.class, "Type_factory__o_k_w_c_d_c_e_i_i_p_PMMLIncludedModelHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLIncludedModelHandler.class, Object.class, DRGElementHandler.class });
  }

  public PMMLIncludedModelHandler createInstance(final ContextManager contextManager) {
    final SessionManager _sessionManager_1 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _dmnGraphUtils_0 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final PMMLIncludedModelHandler instance = new PMMLIncludedModelHandler(_dmnGraphUtils_0, _sessionManager_1);
    registerDependentScopedReference(instance, _dmnGraphUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}