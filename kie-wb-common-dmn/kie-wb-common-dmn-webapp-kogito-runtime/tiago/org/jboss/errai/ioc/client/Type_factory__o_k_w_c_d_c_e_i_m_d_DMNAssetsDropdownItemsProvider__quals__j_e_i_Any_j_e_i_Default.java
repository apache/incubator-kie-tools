package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsIndex;
import org.kie.workbench.common.dmn.client.editors.included.modal.dropdown.DMNAssetsDropdownItemsProvider;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.widgets.client.assets.dropdown.KieAssetsDropdownItemsProvider;

public class Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdownItemsProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNAssetsDropdownItemsProvider> { public Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdownItemsProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNAssetsDropdownItemsProvider.class, "Type_factory__o_k_w_c_d_c_e_i_m_d_DMNAssetsDropdownItemsProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNAssetsDropdownItemsProvider.class, Object.class, KieAssetsDropdownItemsProvider.class });
  }

  public DMNAssetsDropdownItemsProvider createInstance(final ContextManager contextManager) {
    final DMNIncludeModelsClient _client_0 = (DMNIncludeModelsClient) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_i_l_DMNIncludeModelsClient__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _sessionManager_3 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPageState _pageState_1 = (IncludedModelsPageState) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsIndex _modelsIndex_2 = (IncludedModelsIndex) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsIndex__quals__j_e_i_Any_j_e_i_Default");
    final DMNAssetsDropdownItemsProvider instance = new DMNAssetsDropdownItemsProvider(_client_0, _pageState_1, _modelsIndex_2, _sessionManager_3);
    registerDependentScopedReference(instance, _client_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}