package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.api.included.legacy.DMNIncludeModelsClient;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentFilter;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponents.View;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsItem;
import org.kie.workbench.common.dmn.client.docks.navigator.included.components.DecisionComponentsView;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;

public class Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponents__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionComponents> { public Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponents__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionComponents.class, "Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponents__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionComponents.class, Object.class });
  }

  public DecisionComponents createInstance(final ContextManager contextManager) {
    final View _view_0 = (DecisionComponentsView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentsView__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DecisionComponentsItem> _itemManagedInstance_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DecisionComponentsItem.class }, new Annotation[] { });
    final DMNGraphUtils _dmnGraphUtils_5 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNIncludeModelsClient _client_1 = (DMNIncludeModelsClient) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_i_l_DMNIncludeModelsClient__quals__j_e_i_Any_j_e_i_Default");
    final DecisionComponentFilter _filter_3 = (DecisionComponentFilter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_i_c_DecisionComponentFilter__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_4 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final DecisionComponents instance = new DecisionComponents(_view_0, _client_1, _itemManagedInstance_2, _filter_3, _dmnDiagramsSession_4, _dmnGraphUtils_5);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _itemManagedInstance_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_5);
    registerDependentScopedReference(instance, _client_1);
    registerDependentScopedReference(instance, _filter_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DecisionComponents instance) {
    instance.init();
  }
}