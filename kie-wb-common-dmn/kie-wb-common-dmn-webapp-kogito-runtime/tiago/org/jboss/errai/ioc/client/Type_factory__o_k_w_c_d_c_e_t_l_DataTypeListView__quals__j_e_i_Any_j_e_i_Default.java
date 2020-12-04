package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeListView> { public interface o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeListView implements Proxy<DataTypeListView> {
    private final ProxyHelper<DataTypeListView> proxyHelper = new ProxyHelperImpl<DataTypeListView>("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final DataTypeListView instance) {

    }

    public DataTypeListView asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeListView instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void init(DataTypeList presenter) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public void showOrHideNoCustomItemsMessage() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showOrHideNoCustomItemsMessage();
      } else {
        super.showOrHideNoCustomItemsMessage();
      }
    }

    @Override public void addSubItems(DataType dataType, List listItems) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addSubItems(dataType, listItems);
      } else {
        super.addSubItems(dataType, listItems);
      }
    }

    @Override public void expandAll(ClickEvent e) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.expandAll(e);
      } else {
        super.expandAll(e);
      }
    }

    @Override public void collapseAll(ClickEvent e) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.collapseAll(e);
      } else {
        super.collapseAll(e);
      }
    }

    @Override public void onAddButtonClick(ClickEvent e) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onAddButtonClick(e);
      } else {
        super.onAddButtonClick(e);
      }
    }

    @Override public void onImportDataObjectClick(ClickEvent e) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onImportDataObjectClick(e);
      } else {
        super.onImportDataObjectClick(e);
      }
    }

    @Override public void onReadOnlyMessageCloseButtonClick(ClickEvent e) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onReadOnlyMessageCloseButtonClick(e);
      } else {
        super.onReadOnlyMessageCloseButtonClick(e);
      }
    }

    @Override public void removeItem(DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeItem(dataType);
      } else {
        super.removeItem(dataType);
      }
    }

    @Override public void cleanSubTypes(DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.cleanSubTypes(dataType);
      } else {
        super.cleanSubTypes(dataType);
      }
    }

    @Override public void insertBelow(DataTypeListItem listItem, DataType reference) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.insertBelow(listItem, reference);
      } else {
        super.insertBelow(listItem, reference);
      }
    }

    @Override public void insertAbove(DataTypeListItem listItem, DataType reference) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.insertAbove(listItem, reference);
      } else {
        super.insertAbove(listItem, reference);
      }
    }

    @Override public void showNoDataTypesFound() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showNoDataTypesFound();
      } else {
        super.showNoDataTypesFound();
      }
    }

    @Override public void showReadOnlyMessage(boolean show) {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showReadOnlyMessage(show);
      } else {
        super.showReadOnlyMessage(show);
      }
    }

    @Override public HTMLDivElement getListItems() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        final HTMLDivElement retVal = proxiedInstance.getListItems();
        return retVal;
      } else {
        return super.getListItems();
      }
    }

    @Override public void showImportDataObjectButton() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showImportDataObjectButton();
      } else {
        super.showImportDataObjectButton();
      }
    }

    @Override public void hideImportDataObjectButton() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hideImportDataObjectButton();
      } else {
        super.hideImportDataObjectButton();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeListView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeListView.class, "Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeListView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeListView.\"] {\n  min-width: 600px;\n  padding: 1px 20px 10px;\n  height: calc(100% - 15px);\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .data-type-button {\n  height: 40px;\n  margin-top: 20px;\n  white-space: nowrap;\n  overflow: hidden;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .data-type-button .expand-collapse {\n  float: right;\n  margin: 7px 10px 0 14px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .data-type-button .search-bar-container {\n  float: right;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .data-type-button .action-button button {\n  padding: 4px 10px;\n  margin: 0 5px 0 0;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] ul {\n  padding: 0 15px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] > * {\n  flex: 0 1 auto;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] [data-field=\"list-items\"] {\n  height: calc(100% - 120px);\n  overflow-y: scroll;\n  flex: 1 1 auto;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] [data-field=\"list-items\"] .result {\n  display: inherit;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .alert.opened[data-field=\"read-only-message\"] {\n  margin: 5px 0 20px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .alert.opened[data-field=\"read-only-message\"] .close {\n  margin-right: 15px;\n  outline: none;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder {\n  background-color: #f5f5f5;\n  text-align: center;\n  height: calc(100% - 80px);\n  width: calc(100%);\n  display: flex;\n  align-items: center;\n  justify-content: center;\n  overflow-y: hidden;\n  flex: 1 1 auto;\n  margin-top: 20px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder[data-field=\"no-data-types-found\"] {\n  height: calc(100% - 140px);\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder i.fa-object-group,\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder i.fa-search-minus {\n  color: #9c9c9c;\n  font-size: 4.75em;\n  margin-top: 10px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder .btn.btn-primary {\n  font-size: 1.1em;\n  padding: 6px 12px;\n  margin-top: 5px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder .btn.btn-primary i {\n  margin-right: 5px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"] .kie-data-types-placeholder p {\n  color: #555;\n  margin: 20px 0;\n  max-width: 575px;\n  text-align: center;\n  font-size: 1.1em;\n}\n\n");
  }

  public DataTypeListView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _readOnlyMessageCloseButton_10 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Event<FlashMessage> _flashMessageEvent_14 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FlashMessage.class }, new Annotation[] { });
    final ReadOnlyProvider _readOnlyProvider_16 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final HTMLDivElement _searchBarContainer_5 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _placeholder_4 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _addButton_1 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _dataTypeButton_3 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_15 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLButtonElement _addButtonPlaceholder_2 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _readOnlyMessage_9 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _listItems_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _expandAll_6 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ScrollHelper _scrollHelper_11 = (ScrollHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_c_ScrollHelper__quals__j_e_i_Any_j_e_i_Default");
    final HTMLDivElement _noDataTypesFound_8 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _importDataObjectButton_12 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _collapseAll_7 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ImportDataObjectModal _importDataObjectModal_13 = (ImportDataObjectModal) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModal__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeListView instance = new DataTypeListView(_listItems_0, _addButton_1, _addButtonPlaceholder_2, _dataTypeButton_3, _placeholder_4, _searchBarContainer_5, _expandAll_6, _collapseAll_7, _noDataTypesFound_8, _readOnlyMessage_9, _readOnlyMessageCloseButton_10, _scrollHelper_11, _importDataObjectButton_12, _importDataObjectModal_13, _flashMessageEvent_14, _translationService_15, _readOnlyProvider_16);
    registerDependentScopedReference(instance, _readOnlyMessageCloseButton_10);
    registerDependentScopedReference(instance, _flashMessageEvent_14);
    registerDependentScopedReference(instance, _searchBarContainer_5);
    registerDependentScopedReference(instance, _placeholder_4);
    registerDependentScopedReference(instance, _addButton_1);
    registerDependentScopedReference(instance, _dataTypeButton_3);
    registerDependentScopedReference(instance, _translationService_15);
    registerDependentScopedReference(instance, _addButtonPlaceholder_2);
    registerDependentScopedReference(instance, _readOnlyMessage_9);
    registerDependentScopedReference(instance, _listItems_0);
    registerDependentScopedReference(instance, _expandAll_6);
    registerDependentScopedReference(instance, _noDataTypesFound_8);
    registerDependentScopedReference(instance, _importDataObjectButton_12);
    registerDependentScopedReference(instance, _collapseAll_7);
    registerDependentScopedReference(instance, _importDataObjectModal_13);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource templateForDataTypeListView = GWT.create(o_k_w_c_d_c_e_t_l_DataTypeListViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeListView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeListView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeListView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeListView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(12);
    dataFieldMetas.put("list-items", new DataFieldMeta());
    dataFieldMetas.put("placeholder", new DataFieldMeta());
    dataFieldMetas.put("add-button", new DataFieldMeta());
    dataFieldMetas.put("add-button-placeholder", new DataFieldMeta());
    dataFieldMetas.put("data-type-button", new DataFieldMeta());
    dataFieldMetas.put("search-bar-container", new DataFieldMeta());
    dataFieldMetas.put("expand-all", new DataFieldMeta());
    dataFieldMetas.put("collapse-all", new DataFieldMeta());
    dataFieldMetas.put("no-data-types-found", new DataFieldMeta());
    dataFieldMetas.put("read-only-message", new DataFieldMeta());
    dataFieldMetas.put("read-only-message-close-button", new DataFieldMeta());
    dataFieldMetas.put("import-data-object-button", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_listItems(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "list-items");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_placeholder(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "placeholder");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_addButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "add-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_addButtonPlaceholder(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "add-button-placeholder");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_dataTypeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "data-type-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_searchBarContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "search-bar-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLAnchorElement_expandAll(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "expand-all");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLAnchorElement_collapseAll(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "collapse-all");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_noDataTypesFound(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "no-data-types-found");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_readOnlyMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "read-only-message");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_readOnlyMessageCloseButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "read-only-message-close-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView", "org/kie/workbench/common/dmn/client/editors/types/listview/DataTypeListView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_importDataObjectButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "import-data-object-button");
    templateFieldsMap.put("list-items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_listItems(instance))));
    templateFieldsMap.put("placeholder", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_placeholder(instance))));
    templateFieldsMap.put("add-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_addButton(instance))));
    templateFieldsMap.put("add-button-placeholder", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_addButtonPlaceholder(instance))));
    templateFieldsMap.put("data-type-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_dataTypeButton(instance))));
    templateFieldsMap.put("search-bar-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_searchBarContainer(instance))));
    templateFieldsMap.put("expand-all", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLAnchorElement_expandAll(instance))));
    templateFieldsMap.put("collapse-all", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLAnchorElement_collapseAll(instance))));
    templateFieldsMap.put("no-data-types-found", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_noDataTypesFound(instance))));
    templateFieldsMap.put("read-only-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLDivElement_readOnlyMessage(instance))));
    templateFieldsMap.put("read-only-message-close-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_readOnlyMessageCloseButton(instance))));
    templateFieldsMap.put("import-data-object-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeListView_HTMLButtonElement_importDataObjectButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeListView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("collapse-all"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.collapseAll(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("add-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onAddButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("add-button-placeholder"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onAddButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("import-data-object-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onImportDataObjectClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("read-only-message-close-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onReadOnlyMessageCloseButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("expand-all"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.expandAll(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeListView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeListView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_l_DataTypeListView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView ([elemental2.dom.HTMLDivElement, elemental2.dom.HTMLButtonElement, elemental2.dom.HTMLButtonElement, elemental2.dom.HTMLDivElement, elemental2.dom.HTMLDivElement, elemental2.dom.HTMLDivElement, elemental2.dom.HTMLAnchorElement, elemental2.dom.HTMLAnchorElement, elemental2.dom.HTMLDivElement, elemental2.dom.HTMLDivElement, elemental2.dom.HTMLButtonElement, org.kie.workbench.common.dmn.client.editors.types.common.ScrollHelper, elemental2.dom.HTMLButtonElement, org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal, javax.enterprise.event.Event, org.jboss.errai.ui.client.local.spi.TranslationService, org.kie.workbench.common.stunner.core.client.ReadOnlyProvider])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeListView> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static HTMLDivElement DataTypeListView_HTMLDivElement_listItems(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::listItems;
  }-*/;

  native static void DataTypeListView_HTMLDivElement_listItems(DataTypeListView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::listItems = value;
  }-*/;

  native static HTMLAnchorElement DataTypeListView_HTMLAnchorElement_collapseAll(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::collapseAll;
  }-*/;

  native static void DataTypeListView_HTMLAnchorElement_collapseAll(DataTypeListView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::collapseAll = value;
  }-*/;

  native static HTMLButtonElement DataTypeListView_HTMLButtonElement_addButtonPlaceholder(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::addButtonPlaceholder;
  }-*/;

  native static void DataTypeListView_HTMLButtonElement_addButtonPlaceholder(DataTypeListView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::addButtonPlaceholder = value;
  }-*/;

  native static HTMLDivElement DataTypeListView_HTMLDivElement_searchBarContainer(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::searchBarContainer;
  }-*/;

  native static void DataTypeListView_HTMLDivElement_searchBarContainer(DataTypeListView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::searchBarContainer = value;
  }-*/;

  native static HTMLAnchorElement DataTypeListView_HTMLAnchorElement_expandAll(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::expandAll;
  }-*/;

  native static void DataTypeListView_HTMLAnchorElement_expandAll(DataTypeListView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::expandAll = value;
  }-*/;

  native static HTMLDivElement DataTypeListView_HTMLDivElement_noDataTypesFound(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::noDataTypesFound;
  }-*/;

  native static void DataTypeListView_HTMLDivElement_noDataTypesFound(DataTypeListView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::noDataTypesFound = value;
  }-*/;

  native static HTMLButtonElement DataTypeListView_HTMLButtonElement_importDataObjectButton(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::importDataObjectButton;
  }-*/;

  native static void DataTypeListView_HTMLButtonElement_importDataObjectButton(DataTypeListView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::importDataObjectButton = value;
  }-*/;

  native static HTMLDivElement DataTypeListView_HTMLDivElement_placeholder(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::placeholder;
  }-*/;

  native static void DataTypeListView_HTMLDivElement_placeholder(DataTypeListView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::placeholder = value;
  }-*/;

  native static HTMLButtonElement DataTypeListView_HTMLButtonElement_readOnlyMessageCloseButton(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::readOnlyMessageCloseButton;
  }-*/;

  native static void DataTypeListView_HTMLButtonElement_readOnlyMessageCloseButton(DataTypeListView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::readOnlyMessageCloseButton = value;
  }-*/;

  native static HTMLDivElement DataTypeListView_HTMLDivElement_readOnlyMessage(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::readOnlyMessage;
  }-*/;

  native static void DataTypeListView_HTMLDivElement_readOnlyMessage(DataTypeListView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::readOnlyMessage = value;
  }-*/;

  native static HTMLButtonElement DataTypeListView_HTMLButtonElement_addButton(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::addButton;
  }-*/;

  native static void DataTypeListView_HTMLButtonElement_addButton(DataTypeListView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::addButton = value;
  }-*/;

  native static HTMLDivElement DataTypeListView_HTMLDivElement_dataTypeButton(DataTypeListView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::dataTypeButton;
  }-*/;

  native static void DataTypeListView_HTMLDivElement_dataTypeButton(DataTypeListView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView::dataTypeButton = value;
  }-*/;
}