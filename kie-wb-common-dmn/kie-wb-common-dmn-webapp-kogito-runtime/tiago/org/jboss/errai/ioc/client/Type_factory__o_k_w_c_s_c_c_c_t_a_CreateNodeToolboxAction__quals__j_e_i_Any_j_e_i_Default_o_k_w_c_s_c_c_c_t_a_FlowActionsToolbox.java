package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.components.proxies.NodeProxy;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.AbstractToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeToolboxAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.IsToolboxActionDraggable;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_c_c_t_a_CreateNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox extends Factory<CreateNodeToolboxAction> { public Type_factory__o_k_w_c_s_c_c_c_t_a_CreateNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox() {
    super(new FactoryHandleImpl(CreateNodeToolboxAction.class, "Type_factory__o_k_w_c_s_c_c_c_t_a_CreateNodeToolboxAction__quals__j_e_i_Any_j_e_i_Default_o_k_w_c_s_c_c_c_t_a_FlowActionsToolbox", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CreateNodeToolboxAction.class, AbstractToolboxAction.class, Object.class, ToolboxAction.class, IsToolboxActionDraggable.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
  }

  public CreateNodeToolboxAction createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_1 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ClientFactoryManager _clientFactoryManager_3 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final NodeProxy _nodeProxy_4 = (NodeProxy) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_p_NodeProxy__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<GeneralCreateNodeAction> _createNodeActions_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { GeneralCreateNodeAction.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ClientTranslationService _translationService_2 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final CreateNodeToolboxAction instance = new CreateNodeToolboxAction(_createNodeActions_0, _definitionUtils_1, _translationService_2, _clientFactoryManager_3, _nodeProxy_4);
    registerDependentScopedReference(instance, _nodeProxy_4);
    registerDependentScopedReference(instance, _createNodeActions_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CreateNodeToolboxAction) instance, contextManager);
  }

  public void destroyInstanceHelper(final CreateNodeToolboxAction instance, final ContextManager contextManager) {
    instance.destroy();
  }
}