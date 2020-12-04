package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLDivElement;
import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessages;
import org.kie.workbench.common.dmn.client.editors.common.page.DMNPage;
import org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent;
import org.kie.workbench.common.dmn.client.editors.types.DataTypesPage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManager;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.uberfire.client.views.pfly.multipage.PageImpl;
import org.uberfire.client.workbench.widgets.multipage.Page;

public class Type_factory__o_k_w_c_d_c_e_t_DataTypesPage__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypesPage> { public Type_factory__o_k_w_c_d_c_e_t_DataTypesPage__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypesPage.class, "Type_factory__o_k_w_c_d_c_e_t_DataTypesPage__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypesPage.class, DMNPage.class, PageImpl.class, Object.class, Page.class });
  }

  public DataTypesPage createInstance(final ContextManager contextManager) {
    final DataTypeManagerStackStore _stackIndex_5 = (DataTypeManagerStackStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionUtils _itemDefinitionUtils_1 = (ItemDefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ItemDefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeSearchBar _searchBar_7 = (DataTypeSearchBar) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _dmnGraphUtils_8 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStore _dataTypeStore_3 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final ItemDefinitionStore _definitionStore_2 = (ItemDefinitionStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeList _treeList_0 = (DataTypeList) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeShortcuts _dataTypeShortcuts_10 = (DataTypeShortcuts) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_9 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FlashMessages _flashMessages_6 = (FlashMessages) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_c_m_FlashMessages__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _pageView_11 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeManager _dataTypeManager_4 = (DataTypeManager) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManager__quals__j_e_i_Any_j_e_i_Default");
    final DataTypesPage instance = new DataTypesPage(_treeList_0, _itemDefinitionUtils_1, _definitionStore_2, _dataTypeStore_3, _dataTypeManager_4, _stackIndex_5, _flashMessages_6, _searchBar_7, _dmnGraphUtils_8, _translationService_9, _dataTypeShortcuts_10, _pageView_11);
    registerDependentScopedReference(instance, _itemDefinitionUtils_1);
    registerDependentScopedReference(instance, _dmnGraphUtils_8);
    registerDependentScopedReference(instance, _translationService_9);
    registerDependentScopedReference(instance, _flashMessages_6);
    registerDependentScopedReference(instance, _pageView_11);
    registerDependentScopedReference(instance, _dataTypeManager_4);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onRefreshDataTypesListWithNewItemDefinitionsSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent", new AbstractCDIEventCallback<RefreshDataTypesListEvent>() {
      public void fireEvent(final RefreshDataTypesListEvent event) {
        instance.onRefreshDataTypesListWithNewItemDefinitions(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.common.events.RefreshDataTypesListEvent []";
      }
    }));
    thisInstance.setReference(instance, "onDataTypePageNavTabActiveEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent", new AbstractCDIEventCallback<DataTypePageTabActiveEvent>() {
      public void fireEvent(final DataTypePageTabActiveEvent event) {
        instance.onDataTypePageNavTabActiveEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.editors.types.DataTypePageTabActiveEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypesPage) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypesPage instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onRefreshDataTypesListWithNewItemDefinitionsSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onDataTypePageNavTabActiveEventSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final DataTypesPage instance) {
    instance.init();
  }
}