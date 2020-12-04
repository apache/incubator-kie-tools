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
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCard;
import org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_PMMLCard extends Factory<PMMLCardComponentContentView> { public interface o_k_w_c_d_c_e_i_g_PMMLCardComponentContentViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/included/grid/PMMLCardComponentContentView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_PMMLCard() {
    super(new FactoryHandleImpl(PMMLCardComponentContentView.class, "Type_factory__o_k_w_c_d_c_e_i_g_PMMLCardComponentContentView__quals__j_e_i_Any_o_k_w_c_d_c_e_i_g_PMMLCard", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLCardComponentContentView.class, BaseCardComponentContentView.class, Object.class, ContentView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class, org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponent.ContentView.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new PMMLCard() {
        public Class annotationType() {
          return PMMLCard.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCard()";
        }
    } });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"PMMLCardComponentContentView.\"] {\n  text-align: center;\n  margin: 5px auto;\n  max-width: 250px;\n  height: 100%;\n}\n[data-i18n-prefix=\"PMMLCardComponentContentView.\"] p {\n  overflow-wrap: break-word;\n}\n[data-i18n-prefix=\"PMMLCardComponentContentView.\"] [data-field=\"remove-button\"] {\n  margin-top: 15px;\n  padding: 2px 10px;\n}\n[data-i18n-prefix=\"PMMLCardComponentContentView.\"] [data-field=\"remove-button\"]:hover {\n  outline: none;\n}\n\n");
  }

  public PMMLCardComponentContentView createInstance(final ContextManager contextManager) {
    final HTMLButtonElement _removeButton_2 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLParagraphElement _path_0 = (HTMLParagraphElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLParagraphElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _modelCount_1 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final PMMLCardComponentContentView instance = new PMMLCardComponentContentView(_path_0, _modelCount_1, _removeButton_2);
    registerDependentScopedReference(instance, _removeButton_2);
    registerDependentScopedReference(instance, _path_0);
    registerDependentScopedReference(instance, _modelCount_1);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_i_g_PMMLCardComponentContentViewTemplateResource templateForPMMLCardComponentContentView = GWT.create(o_k_w_c_d_c_e_i_g_PMMLCardComponentContentViewTemplateResource.class);
    Element parentElementForTemplateOfPMMLCardComponentContentView = TemplateUtil.getRootTemplateParentElement(templateForPMMLCardComponentContentView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/included/grid/PMMLCardComponentContentView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/included/grid/PMMLCardComponentContentView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPMMLCardComponentContentView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPMMLCardComponentContentView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("path", new DataFieldMeta());
    dataFieldMetas.put("remove-button", new DataFieldMeta());
    dataFieldMetas.put("model-count", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/PMMLCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLParagraphElement_path(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "path");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/PMMLCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLButtonElement_removeButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView", "org/kie/workbench/common/dmn/client/editors/included/grid/PMMLCardComponentContentView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(PMMLCardComponentContentView_HTMLElement_modelCount(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "model-count");
    templateFieldsMap.put("path", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLParagraphElement_path(instance))));
    templateFieldsMap.put("remove-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BaseCardComponentContentView_HTMLButtonElement_removeButton(instance))));
    templateFieldsMap.put("model-count", ElementWrapperWidget.getWidget(TemplateUtil.asElement(PMMLCardComponentContentView_HTMLElement_modelCount(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPMMLCardComponentContentView), templateFieldsMap.values());
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
    destroyInstanceHelper((PMMLCardComponentContentView) instance, contextManager);
  }

  public void destroyInstanceHelper(final PMMLCardComponentContentView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLElement PMMLCardComponentContentView_HTMLElement_modelCount(PMMLCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView::modelCount;
  }-*/;

  native static void PMMLCardComponentContentView_HTMLElement_modelCount(PMMLCardComponentContentView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.PMMLCardComponentContentView::modelCount = value;
  }-*/;

  native static HTMLParagraphElement BaseCardComponentContentView_HTMLParagraphElement_path(BaseCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::path;
  }-*/;

  native static void BaseCardComponentContentView_HTMLParagraphElement_path(BaseCardComponentContentView instance, HTMLParagraphElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::path = value;
  }-*/;

  native static HTMLButtonElement BaseCardComponentContentView_HTMLButtonElement_removeButton(BaseCardComponentContentView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::removeButton;
  }-*/;

  native static void BaseCardComponentContentView_HTMLButtonElement_removeButton(BaseCardComponentContentView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.included.grid.BaseCardComponentContentView::removeButton = value;
  }-*/;
}