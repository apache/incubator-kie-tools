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
import elemental2.dom.HTMLLabelElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModal.View;
import org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeList;
import org.kie.workbench.common.dmn.client.editors.types.imported.treelist.TreeListItem;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default extends Factory<ImportDataObjectModalView> { public interface o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImportDataObjectModalView.class, "Type_factory__o_k_w_c_d_c_e_t_i_ImportDataObjectModalView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImportDataObjectModalView.class, Object.class, View.class, org.uberfire.ext.editor.commons.client.file.popups.elemental2.Elemental2Modal.View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2019 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *     http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n.kie-import-data-object-modal-view-body .alert {\n  max-height: 0;\n  opacity: 0;\n  overflow: hidden;\n  transition: all 0.15s linear;\n  padding: 0 0 0 47px;\n  margin: 5px;\n}\n.kie-import-data-object-modal-view-body .alert.opened {\n  opacity: 1;\n  max-height: 100px;\n  padding: 11px 14px 10px 47px;\n  margin-bottom: 20px;\n  transition: all 0.15s linear;\n}\n.kie-import-data-object-modal-view-body #data-object-items-container {\n  height: 200px;\n  overflow-y: scroll;\n  border: 1px solid #DDD;\n  margin-top: 5px;\n  padding: 10px 0 0 0;\n}\n.kie-import-data-object-modal-view-body [data-field=\"note-label\"] {\n  font-weight: 600;\n}\n.kie-import-data-object-modal-view-body [data-field=\"note-text\"] {\n  font-style: italic;\n}\n.kie-import-data-object-modal-view-body [data-field=\"top-elements-container\"] span {\n  font-weight: 600;\n}\n.kie-import-data-object-modal-view-body [data-field=\"top-elements-container\"] div {\n  float: right;\n}\n.kie-import-data-object-modal-view-body [data-field=\"note-container\"] i {\n  padding-bottom: 20px;\n  padding-left: 5px;\n}\n.kie-import-data-object-modal-view-body [data-field=\"note-container\"] .col-md-1 {\n  width: 20px;\n  padding: 10px 0 0 30px;\n}\n.kie-import-data-object-modal-view-body [data-field=\"note-container\"] .col-md-11 {\n  padding: 15px 0 0 30px;\n  line-height: 0.9em;\n}\n.kie-import-data-object-modal-view-footer {\n  margin-top: -30px;\n  margin-bottom: 10px;\n}\n.kie-import-data-object-modal-view-footer button.btn {\n  width: 60px;\n}\n\n");
  }

  public ImportDataObjectModalView createInstance(final ContextManager contextManager) {
    final ManagedInstance<TreeListItem> _items_8 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeListItem.class }, new Annotation[] { });
    final HTMLButtonElement _buttonImport_9 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLAnchorElement _clearSelection_7 = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLElement _noteText_4 = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
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
    final HTMLDivElement _body_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _warningContainer_11 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _footer_2 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _header_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLDivElement _itemsContainer_6 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final HTMLButtonElement _buttonCancel_10 = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final TreeList _treeList_3 = (TreeList) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_i_t_TreeList__quals__j_e_i_Any_j_e_i_Default");
    final HTMLLabelElement _noteLabel_5 = (HTMLLabelElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ImportDataObjectModalView instance = new ImportDataObjectModalView(_header_0, _body_1, _footer_2, _treeList_3, _noteText_4, _noteLabel_5, _itemsContainer_6, _clearSelection_7, _items_8, _buttonImport_9, _buttonCancel_10, _warningContainer_11);
    registerDependentScopedReference(instance, _items_8);
    registerDependentScopedReference(instance, _buttonImport_9);
    registerDependentScopedReference(instance, _clearSelection_7);
    registerDependentScopedReference(instance, _noteText_4);
    registerDependentScopedReference(instance, _body_1);
    registerDependentScopedReference(instance, _warningContainer_11);
    registerDependentScopedReference(instance, _footer_2);
    registerDependentScopedReference(instance, _header_0);
    registerDependentScopedReference(instance, _itemsContainer_6);
    registerDependentScopedReference(instance, _buttonCancel_10);
    registerDependentScopedReference(instance, _treeList_3);
    registerDependentScopedReference(instance, _noteLabel_5);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource templateForImportDataObjectModalView = GWT.create(o_k_w_c_d_c_e_t_i_ImportDataObjectModalViewTemplateResource.class);
    Element parentElementForTemplateOfImportDataObjectModalView = TemplateUtil.getRootTemplateParentElement(templateForImportDataObjectModalView.getContents().getText(), "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfImportDataObjectModalView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfImportDataObjectModalView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(10);
    dataFieldMetas.put("header", new DataFieldMeta());
    dataFieldMetas.put("body", new DataFieldMeta());
    dataFieldMetas.put("footer", new DataFieldMeta());
    dataFieldMetas.put("note-text", new DataFieldMeta());
    dataFieldMetas.put("note-label", new DataFieldMeta());
    dataFieldMetas.put("items-container", new DataFieldMeta());
    dataFieldMetas.put("clear-selection", new DataFieldMeta());
    dataFieldMetas.put("button-cancel", new DataFieldMeta());
    dataFieldMetas.put("button-import", new DataFieldMeta());
    dataFieldMetas.put("warning-container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_header(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "header");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_body(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "body");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_footer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "footer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLElement_noteText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "note-text");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLLabelElement_noteLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "note-label");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_itemsContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "items-container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLAnchorElement_clearSelection(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "clear-selection");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLButtonElement_buttonCancel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "button-cancel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLButtonElement_buttonImport(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "button-import");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView", "org/kie/workbench/common/dmn/client/editors/types/imported/ImportDataObjectModalView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_warningContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "warning-container");
    templateFieldsMap.put("header", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_header(instance))));
    templateFieldsMap.put("body", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_body(instance))));
    templateFieldsMap.put("footer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_footer(instance))));
    templateFieldsMap.put("note-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLElement_noteText(instance))));
    templateFieldsMap.put("note-label", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLLabelElement_noteLabel(instance))));
    templateFieldsMap.put("items-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_itemsContainer(instance))));
    templateFieldsMap.put("clear-selection", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLAnchorElement_clearSelection(instance))));
    templateFieldsMap.put("button-cancel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLButtonElement_buttonCancel(instance))));
    templateFieldsMap.put("button-import", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLButtonElement_buttonImport(instance))));
    templateFieldsMap.put("warning-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImportDataObjectModalView_HTMLDivElement_warningContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfImportDataObjectModalView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("button-cancel"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ImportDataObjectModalView_onButtonCancelClicked_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("button-import"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ImportDataObjectModalView_onButtonImportClicked_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("clear-selection"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ImportDataObjectModalView_onClearSelectionClicked_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ImportDataObjectModalView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ImportDataObjectModalView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final ImportDataObjectModalView instance) {
    instance.setup();
  }

  native static HTMLElement ImportDataObjectModalView_HTMLElement_noteText(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::noteText;
  }-*/;

  native static void ImportDataObjectModalView_HTMLElement_noteText(ImportDataObjectModalView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::noteText = value;
  }-*/;

  native static HTMLLabelElement ImportDataObjectModalView_HTMLLabelElement_noteLabel(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::noteLabel;
  }-*/;

  native static void ImportDataObjectModalView_HTMLLabelElement_noteLabel(ImportDataObjectModalView instance, HTMLLabelElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::noteLabel = value;
  }-*/;

  native static HTMLButtonElement ImportDataObjectModalView_HTMLButtonElement_buttonImport(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::buttonImport;
  }-*/;

  native static void ImportDataObjectModalView_HTMLButtonElement_buttonImport(ImportDataObjectModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::buttonImport = value;
  }-*/;

  native static HTMLButtonElement ImportDataObjectModalView_HTMLButtonElement_buttonCancel(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::buttonCancel;
  }-*/;

  native static void ImportDataObjectModalView_HTMLButtonElement_buttonCancel(ImportDataObjectModalView instance, HTMLButtonElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::buttonCancel = value;
  }-*/;

  native static HTMLDivElement ImportDataObjectModalView_HTMLDivElement_warningContainer(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::warningContainer;
  }-*/;

  native static void ImportDataObjectModalView_HTMLDivElement_warningContainer(ImportDataObjectModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::warningContainer = value;
  }-*/;

  native static HTMLDivElement ImportDataObjectModalView_HTMLDivElement_header(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::header;
  }-*/;

  native static void ImportDataObjectModalView_HTMLDivElement_header(ImportDataObjectModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::header = value;
  }-*/;

  native static HTMLDivElement ImportDataObjectModalView_HTMLDivElement_itemsContainer(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::itemsContainer;
  }-*/;

  native static void ImportDataObjectModalView_HTMLDivElement_itemsContainer(ImportDataObjectModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::itemsContainer = value;
  }-*/;

  native static HTMLDivElement ImportDataObjectModalView_HTMLDivElement_body(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::body;
  }-*/;

  native static void ImportDataObjectModalView_HTMLDivElement_body(ImportDataObjectModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::body = value;
  }-*/;

  native static HTMLDivElement ImportDataObjectModalView_HTMLDivElement_footer(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::footer;
  }-*/;

  native static void ImportDataObjectModalView_HTMLDivElement_footer(ImportDataObjectModalView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::footer = value;
  }-*/;

  native static HTMLAnchorElement ImportDataObjectModalView_HTMLAnchorElement_clearSelection(ImportDataObjectModalView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::clearSelection;
  }-*/;

  native static void ImportDataObjectModalView_HTMLAnchorElement_clearSelection(ImportDataObjectModalView instance, HTMLAnchorElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::clearSelection = value;
  }-*/;

  public native static void ImportDataObjectModalView_onButtonImportClicked_ClickEvent(ImportDataObjectModalView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::onButtonImportClicked(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void ImportDataObjectModalView_onButtonCancelClicked_ClickEvent(ImportDataObjectModalView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::onButtonCancelClicked(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void ImportDataObjectModalView_onClearSelectionClicked_ClickEvent(ImportDataObjectModalView instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.imported.ImportDataObjectModalView::onClearSelectionClicked(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}