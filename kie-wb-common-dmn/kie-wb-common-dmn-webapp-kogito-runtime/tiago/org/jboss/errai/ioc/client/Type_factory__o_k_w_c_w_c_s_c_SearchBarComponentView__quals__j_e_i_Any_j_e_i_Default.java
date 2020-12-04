package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponent.View;
import org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<SearchBarComponentView> { public interface o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SearchBarComponentView.class, "Type_factory__o_k_w_c_w_c_s_c_SearchBarComponentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SearchBarComponentView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find {\n  position: absolute;\n  top: 2px;\n  right: 20px;\n  width: 30px;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find button.btn.btn-link.btn-find {\n  outline: none;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container {\n  top: 37px;\n  right: -17px;\n  text-align: left;\n  display: block;\n  width: 350px;\n  border-radius: 3px;\n  box-shadow: 0 3px 3px rgba(0, 0, 0, 0.15);\n  outline: none;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container input {\n  padding: 5px 50px 5px 5px;\n  width: 250px;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container input:focus {\n  border-color: inherit;\n  box-shadow: none;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons {\n  width: 85px;\n  height: 30px;\n  right: 5px;\n  text-align: right;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-nums {\n  position: absolute;\n  top: 11px;\n  right: 100px;\n  height: 20px;\n  color: #8b8d8f;\n  pointer-events: none;\n  font-size: .9em;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons div,\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons span,\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons button {\n  display: inline-block;\n  vertical-align: middle;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons button.btn.btn-link {\n  color: #444;\n  width: 20px;\n  outline: none;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons button.btn.btn-link.btn-find-close {\n  width: 25px;\n}\n[data-i18n-prefix=\"SearchBarComponentView.\"] .kie-toolbar-find .kie-find-pf-dropdown-container .find-pf-buttons .find-splitter {\n  border-left: 1px solid #DDD;\n  height: 20px;\n  margin: 0 1px;\n}\n\n");
  }

  public SearchBarComponentView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _searchButton_0 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLInputElement _inputElement_5 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _closeSearch_4 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _totalOfResults_8 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLElement _currentResult_7 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLButtonElement _searchContainer_1 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _nextElement_3 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _prevElement_2 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_6 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final SearchBarComponentView instance = new SearchBarComponentView(_searchButton_0, _searchContainer_1, _prevElement_2, _nextElement_3, _closeSearch_4, _inputElement_5, _translationService_6, _currentResult_7, _totalOfResults_8);
    registerDependentScopedReference(instance, _searchButton_0);
    registerDependentScopedReference(instance, _inputElement_5);
    registerDependentScopedReference(instance, _closeSearch_4);
    registerDependentScopedReference(instance, _totalOfResults_8);
    registerDependentScopedReference(instance, _currentResult_7);
    registerDependentScopedReference(instance, _searchContainer_1);
    registerDependentScopedReference(instance, _nextElement_3);
    registerDependentScopedReference(instance, _prevElement_2);
    registerDependentScopedReference(instance, _translationService_6);
    setIncompleteInstance(instance);
    o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource templateForSearchBarComponentView = GWT.create(o_k_w_c_w_c_s_c_SearchBarComponentViewTemplateResource.class);
    Element parentElementForTemplateOfSearchBarComponentView = TemplateUtil.getRootTemplateParentElement(templateForSearchBarComponentView.getContents().getText(), "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSearchBarComponentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSearchBarComponentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(8);
    dataFieldMetas.put("search-container", new DataFieldMeta());
    dataFieldMetas.put("search-button", new DataFieldMeta());
    dataFieldMetas.put("prev-element", new DataFieldMeta());
    dataFieldMetas.put("next-element", new DataFieldMeta());
    dataFieldMetas.put("close-search", new DataFieldMeta());
    dataFieldMetas.put("search-input", new DataFieldMeta());
    dataFieldMetas.put("current-result", new DataFieldMeta());
    dataFieldMetas.put("total-of-results", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_searchContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "search-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_searchButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "search-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_prevElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "prev-element");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_nextElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "next-element");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_closeSearch(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "close-search");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLInputElement_inputElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "search-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLElement_currentResult(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "current-result");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView", "org/kie/workbench/common/widgets/client/search/component/SearchBarComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLElement_totalOfResults(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "total-of-results");
    templateFieldsMap.put("search-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_searchContainer(instance))));
    templateFieldsMap.put("search-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_searchButton(instance))));
    templateFieldsMap.put("prev-element", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_prevElement(instance))));
    templateFieldsMap.put("next-element", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_nextElement(instance))));
    templateFieldsMap.put("close-search", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLButtonElement_closeSearch(instance))));
    templateFieldsMap.put("search-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLInputElement_inputElement(instance))));
    templateFieldsMap.put("current-result", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLElement_currentResult(instance))));
    templateFieldsMap.put("total-of-results", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SearchBarComponentView_HTMLElement_totalOfResults(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSearchBarComponentView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("search-input"), new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        instance.onSearchInputKeyPress(event);
      }
    }, KeyUpEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("search-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onSearchButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("close-search"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCloseSearchClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("next-element"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onNextElementClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("prev-element"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onPrevElementClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SearchBarComponentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SearchBarComponentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLButtonElement SearchBarComponentView_HTMLButtonElement_nextElement(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::nextElement;
  }-*/;

  native static void SearchBarComponentView_HTMLButtonElement_nextElement(SearchBarComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::nextElement = value;
  }-*/;

  native static HTMLInputElement SearchBarComponentView_HTMLInputElement_inputElement(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::inputElement;
  }-*/;

  native static void SearchBarComponentView_HTMLInputElement_inputElement(SearchBarComponentView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::inputElement = value;
  }-*/;

  native static HTMLButtonElement SearchBarComponentView_HTMLButtonElement_searchButton(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::searchButton;
  }-*/;

  native static void SearchBarComponentView_HTMLButtonElement_searchButton(SearchBarComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::searchButton = value;
  }-*/;

  native static HTMLElement SearchBarComponentView_HTMLElement_currentResult(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::currentResult;
  }-*/;

  native static void SearchBarComponentView_HTMLElement_currentResult(SearchBarComponentView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::currentResult = value;
  }-*/;

  native static HTMLButtonElement SearchBarComponentView_HTMLButtonElement_searchContainer(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::searchContainer;
  }-*/;

  native static void SearchBarComponentView_HTMLButtonElement_searchContainer(SearchBarComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::searchContainer = value;
  }-*/;

  native static HTMLButtonElement SearchBarComponentView_HTMLButtonElement_closeSearch(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::closeSearch;
  }-*/;

  native static void SearchBarComponentView_HTMLButtonElement_closeSearch(SearchBarComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::closeSearch = value;
  }-*/;

  native static HTMLElement SearchBarComponentView_HTMLElement_totalOfResults(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::totalOfResults;
  }-*/;

  native static void SearchBarComponentView_HTMLElement_totalOfResults(SearchBarComponentView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::totalOfResults = value;
  }-*/;

  native static HTMLButtonElement SearchBarComponentView_HTMLButtonElement_prevElement(SearchBarComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::prevElement;
  }-*/;

  native static void SearchBarComponentView_HTMLButtonElement_prevElement(SearchBarComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.search.component.SearchBarComponentView::prevElement = value;
  }-*/;
}