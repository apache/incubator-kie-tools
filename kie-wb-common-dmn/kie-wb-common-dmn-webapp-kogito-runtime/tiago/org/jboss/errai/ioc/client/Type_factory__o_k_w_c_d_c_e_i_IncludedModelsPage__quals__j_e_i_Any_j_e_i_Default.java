package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLDivElement;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.common.page.DMNPage;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPage;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPagePresenter;
import org.kie.workbench.common.dmn.client.editors.included.IncludedModelsPageState;
import org.kie.workbench.common.dmn.client.editors.included.common.IncludedModelsPageStateProvider;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.widgets.multipage.Page;

public class Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPage__quals__j_e_i_Any_j_e_i_Default extends Factory<IncludedModelsPage> { public Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPage__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IncludedModelsPage.class, "Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPage__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IncludedModelsPage.class, DMNPage.class, PageImpl.class, Object.class, Page.class });
  }

  public IncludedModelsPage createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _pageView_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final IncludedModelsPageState _pageState_4 = (IncludedModelsPageState) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPageState__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPageStateProvider _stateProvider_5 = (IncludedModelsPageStateProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_i_IncludedModelsPageStateProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final FlashMessages _flashMessages_2 = (FlashMessages) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPagePresenter _includedModelsPresenter_3 = (IncludedModelsPagePresenter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_i_IncludedModelsPagePresenter__quals__j_e_i_Any_j_e_i_Default");
    final IncludedModelsPage instance = new IncludedModelsPage(_pageView_0, _translationService_1, _flashMessages_2, _includedModelsPresenter_3, _pageState_4, _stateProvider_5);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _pageView_0);
    registerDependentScopedReference(instance, _flashMessages_2);
    registerDependentScopedReference(instance, _includedModelsPresenter_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}