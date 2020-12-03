package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.editors.included.imports.persistence.DefinitionsHandler;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

public class Type_factory__o_k_w_c_d_c_e_i_i_p_DefinitionsHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionsHandler> { public Type_factory__o_k_w_c_d_c_e_i_i_p_DefinitionsHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionsHandler.class, "Type_factory__o_k_w_c_d_c_e_i_i_p_DefinitionsHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionsHandler.class, Object.class });
  }

  public DefinitionsHandler createInstance(final ContextManager contextManager) {
    final DMNGraphUtils _dmnGraphUtils_1 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPageStateProviderImpl _stateProvider_0 = (IncludedModelsPageStateProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionsHandler instance = new DefinitionsHandler(_stateProvider_0, _dmnGraphUtils_1);
    registerDependentScopedReference(instance, _dmnGraphUtils_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}