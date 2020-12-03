package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
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
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar.View;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSearchBarView> { public interface o_k_w_c_d_c_e_t_s_DataTypeSearchBarViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/search/DataTypeSearchBarView.html") public TextResource getContents(); }
  private class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeSearchBarView implements Proxy<DataTypeSearchBarView> {
    private final ProxyHelper<DataTypeSearchBarView> proxyHelper = new ProxyHelperImpl<DataTypeSearchBarView>("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null);
    }

    public void initProxyProperties(final DataTypeSearchBarView instance) {

    }

    public DataTypeSearchBarView asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeSearchBarView instance) {
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

    @Override public void setupSearchBar() {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setupSearchBar();
      } else {
        super.setupSearchBar();
      }
    }

    @Override public void init(DataTypeSearchBar presenter) {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(presenter);
      } else {
        super.init(presenter);
      }
    }

    @Override public void onSearchBarCloseButton(ClickEvent e) {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onSearchBarCloseButton(e);
      } else {
        super.onSearchBarCloseButton(e);
      }
    }

    @Override public void onSearchBarKeyUpEvent(KeyUpEvent event) {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onSearchBarKeyUpEvent(event);
      } else {
        super.onSearchBarKeyUpEvent(event);
      }
    }

    @Override public void onSearchBarKeyDownEvent(KeyDownEvent e) {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onSearchBarKeyDownEvent(e);
      } else {
        super.onSearchBarKeyDownEvent(e);
      }
    }

    @Override public void onSearchBarChangeEvent(ChangeEvent e) {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onSearchBarChangeEvent(e);
      } else {
        super.onSearchBarChangeEvent(e);
      }
    }

    @Override public void resetSearchBar() {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resetSearchBar();
      } else {
        super.resetSearchBar();
      }
    }

    @Override public void showSearchResults(List results) {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.showSearchResults(results);
      } else {
        super.showSearchResults(results);
      }
    }

    @Override public void refreshItemsPosition() {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refreshItemsPosition();
      } else {
        super.refreshItemsPosition();
      }
    }

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeSearchBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeSearchBarView.class, "Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeSearchBarView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeSearchBarView.\"] .kie-data-type-search {\n  width: 350px;\n  position: relative;\n}\n[data-i18n-prefix=\"DataTypeSearchBarView.\"] .kie-data-type-search .form-control {\n  padding: 4px 30px 4px 7px;\n  height: 30px;\n}\n[data-i18n-prefix=\"DataTypeSearchBarView.\"] .kie-data-type-search span,\n[data-i18n-prefix=\"DataTypeSearchBarView.\"] .kie-data-type-search button {\n  background: transparent;\n  border: none;\n  color: #999;\n  position: absolute;\n  top: 0;\n  right: 0;\n  padding: 9px;\n}\n[data-i18n-prefix=\"DataTypeListView.\"].kie-search-engine-enabled [data-i18n-prefix=\"DNDListComponentView.\"] .kie-dnd-drag-area .kie-dnd-draggable:hover .kie-dnd-grip {\n  display: none;\n}\n[data-i18n-prefix=\"DataTypeListView.\"].kie-search-engine-enabled [data-i18n-prefix=\"DataTypeListItemView.\"].list-group-item .list-view-pf-main-info [data-type-field=\"arrow-button\"] {\n  pointer-events: none;\n  cursor: default;\n  text-decoration: none;\n}\n[data-i18n-prefix=\"DataTypeListView.\"].kie-search-engine-enabled [data-i18n-prefix=\"DataTypeListItemView.\"].list-group-item .list-view-pf-main-info [data-type-field=\"arrow-button\"]:before {\n  opacity: 0.25;\n}\n\n");
  }

  public DataTypeSearchBarView createInstance(final ContextManager contextManager) {
    final HTMLElement _searchIcon_1 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    final HTMLButtonElement _closeSearch_2 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLInputElement _searchBar_0 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final DataTypeSearchBarView instance = new DataTypeSearchBarView(_searchBar_0, _searchIcon_1, _closeSearch_2, _translationService_3);
    registerDependentScopedReference(instance, _searchIcon_1);
    registerDependentScopedReference(instance, _closeSearch_2);
    registerDependentScopedReference(instance, _translationService_3);
    registerDependentScopedReference(instance, _searchBar_0);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_s_DataTypeSearchBarViewTemplateResource templateForDataTypeSearchBarView = GWT.create(o_k_w_c_d_c_e_t_s_DataTypeSearchBarViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeSearchBarView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeSearchBarView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/search/DataTypeSearchBarView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/search/DataTypeSearchBarView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeSearchBarView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeSearchBarView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("search-bar", new DataFieldMeta());
    dataFieldMetas.put("search-icon", new DataFieldMeta());
    dataFieldMetas.put("close-search", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView", "org/kie/workbench/common/dmn/client/editors/types/search/DataTypeSearchBarView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSearchBarView_HTMLInputElement_searchBar(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "search-bar");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView", "org/kie/workbench/common/dmn/client/editors/types/search/DataTypeSearchBarView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSearchBarView_HTMLElement_searchIcon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "search-icon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView", "org/kie/workbench/common/dmn/client/editors/types/search/DataTypeSearchBarView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSearchBarView_HTMLButtonElement_closeSearch(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "close-search");
    templateFieldsMap.put("search-bar", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSearchBarView_HTMLInputElement_searchBar(instance))));
    templateFieldsMap.put("search-icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSearchBarView_HTMLElement_searchIcon(instance))));
    templateFieldsMap.put("close-search", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeSearchBarView_HTMLButtonElement_closeSearch(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeSearchBarView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("search-bar"), new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        instance.onSearchBarChangeEvent(event);
      }
    }, ChangeEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("search-bar"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        instance.onSearchBarKeyDownEvent(event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("close-search"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onSearchBarCloseButton(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("search-bar"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        instance.onSearchBarKeyUpEvent(event);
      }
    }, KeyUpEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeSearchBarView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeSearchBarView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DataTypeSearchBarView instance) {
    instance.setupSearchBar();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView ([elemental2.dom.HTMLInputElement, elemental2.dom.HTMLElement, elemental2.dom.HTMLButtonElement, org.jboss.errai.ui.client.local.spi.TranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeSearchBarView> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static HTMLInputElement DataTypeSearchBarView_HTMLInputElement_searchBar(DataTypeSearchBarView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView::searchBar;
  }-*/;

  native static void DataTypeSearchBarView_HTMLInputElement_searchBar(DataTypeSearchBarView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView::searchBar = value;
  }-*/;

  native static HTMLButtonElement DataTypeSearchBarView_HTMLButtonElement_closeSearch(DataTypeSearchBarView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView::closeSearch;
  }-*/;

  native static void DataTypeSearchBarView_HTMLButtonElement_closeSearch(DataTypeSearchBarView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView::closeSearch = value;
  }-*/;

  native static HTMLElement DataTypeSearchBarView_HTMLElement_searchIcon(DataTypeSearchBarView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView::searchIcon;
  }-*/;

  native static void DataTypeSearchBarView_HTMLElement_searchIcon(DataTypeSearchBarView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView::searchIcon = value;
  }-*/;
}