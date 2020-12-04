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
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
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
import org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponent.ContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCard;
import org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_DMNCard extends Factory<DMNCardComponentContentView> { public interface o_k_w_c_d_c_e_i_g_DMNCardComponentContentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_DMNCard() {
    super(new FactoryHandleImpl(DMNCardComponentContentView.class, "Type_factory__o_k_w_c_d_c_e_i_g_DMNCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_DMNCard", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCardComponentContentView.class, BaseCardComponentContentView.class, Object.class, ContentView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class, org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponent.ContentView.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNCard() {
        public Class annotationType() {
          return DMNCard.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.editors.included.grid.DMNCard()";
        }
    } });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DMNCardComponentContentView.\"] {\n  text-align: center;\n  margin: 5px auto;\n  max-width: 250px;\n  height: 100%;\n}\n[data-i18n-prefix=\"DMNCardComponentContentView.\"] p {\n  overflow-wrap: break-word;\n}\n[data-i18n-prefix=\"DMNCardComponentContentView.\"] [data-field=\"remove-button\"] {\n  margin-top: 15px;\n  padding: 2px 10px;\n}\n[data-i18n-prefix=\"DMNCardComponentContentView.\"] [data-field=\"remove-button\"]:hover {\n  outline: none;\n}\n\n");
  }

  public DMNCardComponentContentView createInstance(final ContextManager contextManager) {
    final HTMLElement _dataTypesCount_1 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLParagraphElement _path_0 = (HTMLParagraphElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLParagraphElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _removeButton_3 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _drgElementsCount_2 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final DMNCardComponentContentView instance = new DMNCardComponentContentView(_path_0, _dataTypesCount_1, _drgElementsCount_2, _removeButton_3);
    registerDependentScopedReference(instance, _dataTypesCount_1);
    registerDependentScopedReference(instance, _path_0);
    registerDependentScopedReference(instance, _removeButton_3);
    registerDependentScopedReference(instance, _drgElementsCount_2);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_i_g_DMNCardComponentContentViewTemplateResource templateForDMNCardComponentContentView = GWT.create(o_k_w_c_d_c_e_i_g_DMNCardComponentContentViewTemplateResource.class);
    Element parentElementForTemplateOfDMNCardComponentContentView = TemplateUtil.getRootTemplateParentElement(templateForDMNCardComponentContentView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNCardComponentContentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNCardComponentContentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("path", new DataFieldMeta());
    dataFieldMetas.put("remove-button", new DataFieldMeta());
    dataFieldMetas.put("data-types-count", new DataFieldMeta());
    dataFieldMetas.put("drg-elements-count", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLParagraphElement_path(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "path");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLButtonElement_removeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNCardComponentContentView_HTMLElement_dataTypesCount(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "data-types-count");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/DMNCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNCardComponentContentView_HTMLElement_drgElementsCount(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "drg-elements-count");
    templateFieldsMap.put("path", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLParagraphElement_path(instance))));
    templateFieldsMap.put("remove-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLButtonElement_removeButton(instance))));
    templateFieldsMap.put("data-types-count", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNCardComponentContentView_HTMLElement_dataTypesCount(instance))));
    templateFieldsMap.put("drg-elements-count", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DMNCardComponentContentView_HTMLElement_drgElementsCount(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDMNCardComponentContentView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("remove-button"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onRemoveButtonClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNCardComponentContentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNCardComponentContentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLParagraphElement BaseCardComponentContentView_HTMLParagraphElement_path(BaseCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::path;
  }-*/;

  native static void BaseCardComponentContentView_HTMLParagraphElement_path(BaseCardComponentContentView instance, HTMLParagraphElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::path = value;
  }-*/;

  native static HTMLElement DMNCardComponentContentView_HTMLElement_dataTypesCount(DMNCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView::dataTypesCount;
  }-*/;

  native static void DMNCardComponentContentView_HTMLElement_dataTypesCount(DMNCardComponentContentView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView::dataTypesCount = value;
  }-*/;

  native static HTMLElement DMNCardComponentContentView_HTMLElement_drgElementsCount(DMNCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView::drgElementsCount;
  }-*/;

  native static void DMNCardComponentContentView_HTMLElement_drgElementsCount(DMNCardComponentContentView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.DMNCardComponentContentView::drgElementsCount = value;
  }-*/;

  native static HTMLButtonElement BaseCardComponentContentView_HTMLButtonElement_removeButton(BaseCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::removeButton;
  }-*/;

  native static void BaseCardComponentContentView_HTMLButtonElement_removeButton(BaseCardComponentContentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::removeButton = value;
  }-*/;
}