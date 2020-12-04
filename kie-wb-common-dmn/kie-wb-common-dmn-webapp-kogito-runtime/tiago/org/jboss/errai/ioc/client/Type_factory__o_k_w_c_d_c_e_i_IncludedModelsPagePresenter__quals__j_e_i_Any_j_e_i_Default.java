package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter.View;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageView;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.modal.IncludedModelModal;

public class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPagePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPagePresenter> { public Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPagePresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsPagePresenter.class, "Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPagePresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsPagePresenter.class, Object.class });
  }

  public IncludedModelsPagePresenter createInstance(final ContextManager contextManager) {
    final DMNCardsGridComponent _gridComponent_1 = (DMNCardsGridComponent) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_g_DMNCardsGridComponent__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (IncludedModelsPageView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageView__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelModal _modal_2 = (IncludedModelModal) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_m_IncludedModelModal__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPagePresenter instance = new IncludedModelsPagePresenter(_view_0, _gridComponent_1, _modal_2);
    registerDependentScopedReference(instance, _gridComponent_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _modal_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final IncludedModelsPagePresenter instance) {
    instance.init();
  }
}