package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
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
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponent.View;
import org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_c_f_CardFrameComponentView__quals__j_e_i_Any_j_e_i_Default extends Factory<CardFrameComponentView> { public interface o_k_w_c_w_c_c_f_CardFrameComponentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_c_f_CardFrameComponentView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CardFrameComponentView.class, "Type_factory__o_k_w_c_w_c_c_f_CardFrameComponentView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CardFrameComponentView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"CardFrameComponentView.\"] h2 {\n  cursor: pointer;\n  white-space: nowrap;\n  overflow: hidden;\n  text-overflow: ellipsis;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] h2.read-only {\n  cursor: default;\n  pointer-events: none;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"title-text\"] {\n  height: 25px;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] {\n  text-align: center;\n  margin-top: 15px;\n  height: 25px;\n  position: relative;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] [data-field=\"input-close-button\"] {\n  background: none;\n  border: none;\n  color: #ADADAD;\n  position: absolute;\n  right: 62px;\n  top: 0;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] [data-field=\"input-close-button\"] i {\n  font-size: 1.3em;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] label,\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] button {\n  display: inline-block;\n  vertical-align: middle;\n  outline: none;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] label {\n  margin: 0;\n  width: calc(100% - 65px);\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] button {\n  height: 26px;\n  width: 26px;\n  padding: 1px 0 0;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] button i {\n  font-size: 1.25em;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] button:hover {\n  outline: none;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] [data-field=\"edit-mode\"] [data-field=\"title-input\"] {\n  width: 100%;\n  padding: 2px 25px 2px 5px;\n  outline: none;\n  border: 1px solid #DDD;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] .card-pf-view {\n  overflow: hidden;\n  min-width: 130px;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] .card-pf-view.card-pf-view-select:hover {\n  box-shadow: 0 1px 5px rgba(70, 159, 204, 0.75);\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] .card-pf-view .card-pf-top-element span.card-pf-icon-circle {\n  font-size: 35px;\n  line-height: 55px;\n  width: 60px;\n  height: 60px;\n}\n[data-i18n-prefix=\"CardFrameComponentView.\"] .col-xs-12.col-lg-3 {\n  width: 100%;\n}\n@media (min-width: 1250px) {\n  [data-i18n-prefix=\"CardFrameComponentView.\"] .col-xs-12.col-lg-3 {\n    width: 50%;\n  }\n}\n@media (min-width: 1500px) {\n  [data-i18n-prefix=\"CardFrameComponentView.\"] .col-xs-12.col-lg-3 {\n    width: 33.3%;\n  }\n}\n@media (min-width: 1700px) {\n  [data-i18n-prefix=\"CardFrameComponentView.\"] .col-xs-12.col-lg-3 {\n    width: 25%;\n  }\n}\n\n");
  }

  public CardFrameComponentView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _okButton_6 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _inputCloseButton_4 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _icon_1 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLInputElement _titleInput_3 = (HTMLInputElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLInputElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _view_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLHeadingElement _titleText_2 = (HTMLHeadingElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLHeadingElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _editMode_5 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _content_8 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _closeButton_7 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final CardFrameComponentView instance = new CardFrameComponentView(_view_0, _icon_1, _titleText_2, _titleInput_3, _inputCloseButton_4, _editMode_5, _okButton_6, _closeButton_7, _content_8);
    registerDependentScopedReference(instance, _okButton_6);
    registerDependentScopedReference(instance, _inputCloseButton_4);
    registerDependentScopedReference(instance, _icon_1);
    registerDependentScopedReference(instance, _titleInput_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _titleText_2);
    registerDependentScopedReference(instance, _editMode_5);
    registerDependentScopedReference(instance, _content_8);
    registerDependentScopedReference(instance, _closeButton_7);
    setIncompleteInstance(instance);
    o_k_w_c_w_c_c_f_CardFrameComponentViewTemplateResource templateForCardFrameComponentView = GWT.create(o_k_w_c_w_c_c_f_CardFrameComponentViewTemplateResource.class);
    Element parentElementForTemplateOfCardFrameComponentView = TemplateUtil.getRootTemplateParentElement(templateForCardFrameComponentView.getContents().getText(), "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCardFrameComponentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCardFrameComponentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("view", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("title-text", new DataFieldMeta());
    dataFieldMetas.put("title-input", new DataFieldMeta());
    dataFieldMetas.put("input-close-button", new DataFieldMeta());
    dataFieldMetas.put("edit-mode", new DataFieldMeta());
    dataFieldMetas.put("ok-button", new DataFieldMeta());
    dataFieldMetas.put("close-button", new DataFieldMeta());
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLDivElement_view(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "view");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLElement_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLHeadingElement_titleText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "title-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLInputElement_titleInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "title-input");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLButtonElement_inputCloseButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "input-close-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLDivElement_editMode(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "edit-mode");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLButtonElement_okButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "ok-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLButtonElement_closeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "close-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView", "org/kie/workbench/common/widgets/client/cards/frame/CardFrameComponentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLDivElement_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("view", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLDivElement_view(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLElement_icon(instance))));
    templateFieldsMap.put("title-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLHeadingElement_titleText(instance))));
    templateFieldsMap.put("title-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLInputElement_titleInput(instance))));
    templateFieldsMap.put("input-close-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLButtonElement_inputCloseButton(instance))));
    templateFieldsMap.put("edit-mode", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLDivElement_editMode(instance))));
    templateFieldsMap.put("ok-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLButtonElement_okButton(instance))));
    templateFieldsMap.put("close-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLButtonElement_closeButton(instance))));
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CardFrameComponentView_HTMLDivElement_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCardFrameComponentView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("ok-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onOkButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("title-input"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        instance.onTitleInputKeyDownEvent(event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("close-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onCloseButtonClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("title-text"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onTitleTextClick(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("input-close-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onInputCloseButtonClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CardFrameComponentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final CardFrameComponentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLButtonElement CardFrameComponentView_HTMLButtonElement_closeButton(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::closeButton;
  }-*/;

  native static void CardFrameComponentView_HTMLButtonElement_closeButton(CardFrameComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::closeButton = value;
  }-*/;

  native static HTMLInputElement CardFrameComponentView_HTMLInputElement_titleInput(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::titleInput;
  }-*/;

  native static void CardFrameComponentView_HTMLInputElement_titleInput(CardFrameComponentView instance, HTMLInputElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::titleInput = value;
  }-*/;

  native static HTMLDivElement CardFrameComponentView_HTMLDivElement_editMode(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::editMode;
  }-*/;

  native static void CardFrameComponentView_HTMLDivElement_editMode(CardFrameComponentView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::editMode = value;
  }-*/;

  native static HTMLDivElement CardFrameComponentView_HTMLDivElement_content(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::content;
  }-*/;

  native static void CardFrameComponentView_HTMLDivElement_content(CardFrameComponentView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::content = value;
  }-*/;

  native static HTMLDivElement CardFrameComponentView_HTMLDivElement_view(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::view;
  }-*/;

  native static void CardFrameComponentView_HTMLDivElement_view(CardFrameComponentView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::view = value;
  }-*/;

  native static HTMLElement CardFrameComponentView_HTMLElement_icon(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::icon;
  }-*/;

  native static void CardFrameComponentView_HTMLElement_icon(CardFrameComponentView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::icon = value;
  }-*/;

  native static HTMLButtonElement CardFrameComponentView_HTMLButtonElement_okButton(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::okButton;
  }-*/;

  native static void CardFrameComponentView_HTMLButtonElement_okButton(CardFrameComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::okButton = value;
  }-*/;

  native static HTMLHeadingElement CardFrameComponentView_HTMLHeadingElement_titleText(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::titleText;
  }-*/;

  native static void CardFrameComponentView_HTMLHeadingElement_titleText(CardFrameComponentView instance, HTMLHeadingElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::titleText = value;
  }-*/;

  native static HTMLButtonElement CardFrameComponentView_HTMLButtonElement_inputCloseButton(CardFrameComponentView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::inputCloseButton;
  }-*/;

  native static void CardFrameComponentView_HTMLButtonElement_inputCloseButton(CardFrameComponentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.cards.frame.CardFrameComponentView::inputCloseButton = value;
  }-*/;
}