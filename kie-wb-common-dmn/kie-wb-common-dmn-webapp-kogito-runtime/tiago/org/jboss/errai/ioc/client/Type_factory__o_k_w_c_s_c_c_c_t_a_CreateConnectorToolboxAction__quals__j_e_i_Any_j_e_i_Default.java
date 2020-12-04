package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.components.proxies.ConnectorProxy;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateConnectorToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.IsToolboxActionDraggable;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_CreateConnectorToolboxAction__quals__j_e_i_Any_j_e_i_Default extends Factory<CreateConnectorToolboxAction> { public Type_factory__o_k_w_c_s_c_c_c_t_a_CreateConnectorToolboxAction__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CreateConnectorToolboxAction.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_CreateConnectorToolboxAction__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CreateConnectorToolboxAction.class, AbstractToolboxAction.class, Object.class, ToolboxAction.class, IsToolboxActionDraggable.class });
  }

  public CreateConnectorToolboxAction createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_2 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ClientFactoryManager _clientFactoryManager_1 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final ConnectorProxy _connectorProxy_3 = (ConnectorProxy) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_p_ConnectorProxy__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final CreateConnectorToolboxAction instance = new CreateConnectorToolboxAction(_definitionUtils_0, _clientFactoryManager_1, _translationService_2, _connectorProxy_3);
    registerDependentScopedReference(instance, _connectorProxy_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CreateConnectorToolboxAction) instance, contextManager);
  }

  public void destroyInstanceHelper(final CreateConnectorToolboxAction instance, final ContextManager contextManager) {
    instance.destroy();
  }
}