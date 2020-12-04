package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardsGridComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.DefaultCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponent;
import org.kie.workbench.common.dmn.client.editors.included.grid.empty.DMNCardsEmptyStateView;
import org.kie.workbench.common.widgets.client.cards.CardsGridComponent;

public class Type_factory__o_k_w_c_d_c_e_i_g_DMNCardsGridComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCardsGridComponent> { public Type_factory__o_k_w_c_d_c_e_i_g_DMNCardsGridComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNCardsGridComponent.class, "Type_factory__o_k_w_c_d_c_e_i_g_DMNCardsGridComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCardsGridComponent.class, Object.class });
  }

  public DMNCardsGridComponent createInstance(final ContextManager contextManager) {
    final CardsGridComponent _cardsGridComponent_3 = (CardsGridComponent) contextManager.getInstance("Type_factory__o_k_w_c_w_c_c_CardsGridComponent__quals__j_e_i_Any_j_e_i_Default");
    final DMNCardsEmptyStateView _emptyStateView_5 = (DMNCardsEmptyStateView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_g_e_DMNCardsEmptyStateView__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<PMMLCardComponent> _pmmlCardComponent_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { PMMLCardComponent.class }, new Annotation[] { });
    final ManagedInstance<DMNCardComponent> _dmnCardComponent_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DMNCardComponent.class }, new Annotation[] { });
    final ManagedInstance<DefaultCardComponent> _defaultCardComponent_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefaultCardComponent.class }, new Annotation[] { });
    final IncludedModelsPageState _pageState_4 = (IncludedModelsPageState) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default");
    final DMNCardsGridComponent instance = new DMNCardsGridComponent(_dmnCardComponent_0, _pmmlCardComponent_1, _defaultCardComponent_2, _cardsGridComponent_3, _pageState_4, _emptyStateView_5);
    registerDependentScopedReference(instance, _cardsGridComponent_3);
    registerDependentScopedReference(instance, _emptyStateView_5);
    registerDependentScopedReference(instance, _pmmlCardComponent_1);
    registerDependentScopedReference(instance, _dmnCardComponent_0);
    registerDependentScopedReference(instance, _defaultCardComponent_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNCardsGridComponent instance) {
    instance.init();
  }
}