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
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
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
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint.View;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintView__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeConstraintView> { public interface o_k_w_c_d_c_e_t_l_c_DataTypeConstraintViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeConstraintView.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeConstraintView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeConstraintView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DataTypeConstraintView.\"] {\n  padding-top: 10px;\n}\n[data-i18n-prefix=\"DataTypeConstraintView.\"] .fa {\n  vertical-align: middle;\n  padding: 1px 2px 0;\n}\n[data-i18n-prefix=\"DataTypeConstraintView.\"] [data-field=\"constraints-anchor-container\"] {\n  text-decoration: none;\n}\n[data-i18n-prefix=\"DataTypeConstraintView.\"] [data-field=\"constraints-label-text\"],\n[data-i18n-prefix=\"DataTypeConstraintView.\"] [data-field=\"constraints-anchor-text\"] {\n  white-space: nowrap;\n  overflow: hidden;\n  text-overflow: ellipsis;\n  max-width: 200px;\n  display: inline-block;\n  vertical-align: middle;\n}\n\n");
  }

  public DataTypeConstraintView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _constraintsLabelContainer_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _constraintsAnchorText_2 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLElement _constraintsLabelText_3 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLAnchorElement _constraintsAnchorContainer_0 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TranslationService _translationService_5 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HTMLElement _constraintsTooltip_4 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final DataTypeConstraintView instance = new DataTypeConstraintView(_constraintsAnchorContainer_0, _constraintsLabelContainer_1, _constraintsAnchorText_2, _constraintsLabelText_3, _constraintsTooltip_4, _translationService_5);
    registerDependentScopedReference(instance, _constraintsLabelContainer_1);
    registerDependentScopedReference(instance, _constraintsAnchorText_2);
    registerDependentScopedReference(instance, _constraintsLabelText_3);
    registerDependentScopedReference(instance, _constraintsAnchorContainer_0);
    registerDependentScopedReference(instance, _translationService_5);
    registerDependentScopedReference(instance, _constraintsTooltip_4);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_l_c_DataTypeConstraintViewTemplateResource templateForDataTypeConstraintView = GWT.create(o_k_w_c_d_c_e_t_l_c_DataTypeConstraintViewTemplateResource.class);
    Element parentElementForTemplateOfDataTypeConstraintView = TemplateUtil.getRootTemplateParentElement(templateForDataTypeConstraintView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("constraints-anchor-container", new DataFieldMeta());
    dataFieldMetas.put("constraints-label-container", new DataFieldMeta());
    dataFieldMetas.put("constraints-anchor-text", new DataFieldMeta());
    dataFieldMetas.put("constraints-label-text", new DataFieldMeta());
    dataFieldMetas.put("constraints-tooltip", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLAnchorElement_constraintsAnchorContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraints-anchor-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLDivElement_constraintsLabelContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraints-label-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLElement_constraintsAnchorText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraints-anchor-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLElement_constraintsLabelText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraints-label-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView", "org/kie/workbench/common/dmn/client/editors/types/listview/constraint/DataTypeConstraintView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLElement_constraintsTooltip(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "constraints-tooltip");
    templateFieldsMap.put("constraints-anchor-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLAnchorElement_constraintsAnchorContainer(instance))));
    templateFieldsMap.put("constraints-label-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLDivElement_constraintsLabelContainer(instance))));
    templateFieldsMap.put("constraints-anchor-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLElement_constraintsAnchorText(instance))));
    templateFieldsMap.put("constraints-label-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLElement_constraintsLabelText(instance))));
    templateFieldsMap.put("constraints-tooltip", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DataTypeConstraintView_HTMLElement_constraintsTooltip(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDataTypeConstraintView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("constraints-anchor-container"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onConstraintsClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DataTypeConstraintView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DataTypeConstraintView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final DataTypeConstraintView instance) {
    instance.setup();
  }

  native static HTMLAnchorElement DataTypeConstraintView_HTMLAnchorElement_constraintsAnchorContainer(DataTypeConstraintView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsAnchorContainer;
  }-*/;

  native static void DataTypeConstraintView_HTMLAnchorElement_constraintsAnchorContainer(DataTypeConstraintView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsAnchorContainer = value;
  }-*/;

  native static HTMLDivElement DataTypeConstraintView_HTMLDivElement_constraintsLabelContainer(DataTypeConstraintView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsLabelContainer;
  }-*/;

  native static void DataTypeConstraintView_HTMLDivElement_constraintsLabelContainer(DataTypeConstraintView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsLabelContainer = value;
  }-*/;

  native static HTMLElement DataTypeConstraintView_HTMLElement_constraintsTooltip(DataTypeConstraintView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsTooltip;
  }-*/;

  native static void DataTypeConstraintView_HTMLElement_constraintsTooltip(DataTypeConstraintView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsTooltip = value;
  }-*/;

  native static HTMLElement DataTypeConstraintView_HTMLElement_constraintsLabelText(DataTypeConstraintView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsLabelText;
  }-*/;

  native static void DataTypeConstraintView_HTMLElement_constraintsLabelText(DataTypeConstraintView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsLabelText = value;
  }-*/;

  native static HTMLElement DataTypeConstraintView_HTMLElement_constraintsAnchorText(DataTypeConstraintView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsAnchorText;
  }-*/;

  native static void DataTypeConstraintView_HTMLElement_constraintsAnchorText(DataTypeConstraintView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintView::constraintsAnchorText = value;
  }-*/;
}