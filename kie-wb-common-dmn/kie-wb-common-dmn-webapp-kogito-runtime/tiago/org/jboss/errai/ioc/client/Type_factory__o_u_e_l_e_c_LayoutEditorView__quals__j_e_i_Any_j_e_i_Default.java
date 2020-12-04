package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter.View;
import org.uberfire.ext.layout.editor.client.LayoutEditorView;

public class Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorView> { public interface o_u_e_l_e_c_LayoutEditorViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/LayoutEditorView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/LayoutEditorView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorView.class, "Type_factory__o_u_e_l_e_c_LayoutEditorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_LayoutEditorViewTemplateResource) GWT.create(o_u_e_l_e_c_LayoutEditorViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public LayoutEditorView createInstance(final ContextManager contextManager) {
    final LayoutEditorView instance = new LayoutEditorView();
    setIncompleteInstance(instance);
    final Div LayoutEditorView_previewDiv = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_previewDiv);
    LayoutEditorView_Div_previewDiv(instance, LayoutEditorView_previewDiv);
    final ListItem LayoutEditorView_previewTab = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_previewTab);
    LayoutEditorView_ListItem_previewTab(instance, LayoutEditorView_previewTab);
    final Anchor LayoutEditorView_designAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_designAnchor);
    LayoutEditorView_Anchor_designAnchor(instance, LayoutEditorView_designAnchor);
    final Anchor LayoutEditorView_previewAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_previewAnchor);
    LayoutEditorView_Anchor_previewAnchor(instance, LayoutEditorView_previewAnchor);
    final Div LayoutEditorView_tabsDiv = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_tabsDiv);
    LayoutEditorView_Div_tabsDiv(instance, LayoutEditorView_tabsDiv);
    final Div LayoutEditorView_designDiv = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_designDiv);
    LayoutEditorView_Div_designDiv(instance, LayoutEditorView_designDiv);
    final ListItem LayoutEditorView_designTab = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_designTab);
    LayoutEditorView_ListItem_designTab(instance, LayoutEditorView_designTab);
    final Div LayoutEditorView_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_container);
    LayoutEditorView_Div_container(instance, LayoutEditorView_container);
    final Div LayoutEditorView_mainDiv = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorView_mainDiv);
    LayoutEditorView_Div_mainDiv(instance, LayoutEditorView_mainDiv);
    o_u_e_l_e_c_LayoutEditorViewTemplateResource templateForLayoutEditorView = GWT.create(o_u_e_l_e_c_LayoutEditorViewTemplateResource.class);
    Element parentElementForTemplateOfLayoutEditorView = TemplateUtil.getRootTemplateParentElement(templateForLayoutEditorView.getContents().getText(), "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/LayoutEditorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutEditorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutEditorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("mainDiv", new DataFieldMeta());
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("tabsDiv", new DataFieldMeta());
    dataFieldMetas.put("designTab", new DataFieldMeta());
    dataFieldMetas.put("previewTab", new DataFieldMeta());
    dataFieldMetas.put("designDiv", new DataFieldMeta());
    dataFieldMetas.put("previewDiv", new DataFieldMeta());
    dataFieldMetas.put("designAnchor", new DataFieldMeta());
    dataFieldMetas.put("previewAnchor", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_mainDiv(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "mainDiv");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_tabsDiv(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "tabsDiv");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_ListItem_designTab(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "designTab");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_ListItem_previewTab(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "previewTab");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_designDiv(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "designDiv");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_previewDiv(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "previewDiv");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Anchor_designAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "designAnchor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.LayoutEditorView", "org/uberfire/ext/layout/editor/client/LayoutEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Anchor_previewAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "previewAnchor");
    templateFieldsMap.put("mainDiv", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_mainDiv(instance))));
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_container(instance))));
    templateFieldsMap.put("tabsDiv", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_tabsDiv(instance))));
    templateFieldsMap.put("designTab", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_ListItem_designTab(instance))));
    templateFieldsMap.put("previewTab", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_ListItem_previewTab(instance))));
    templateFieldsMap.put("designDiv", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_designDiv(instance))));
    templateFieldsMap.put("previewDiv", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Div_previewDiv(instance))));
    templateFieldsMap.put("designAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Anchor_designAnchor(instance))));
    templateFieldsMap.put("previewAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorView_Anchor_previewAnchor(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutEditorView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("designTab"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        LayoutEditorView_designTabClicked_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("previewTab"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        LayoutEditorView_previewTabClicked_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutEditorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutEditorView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Anchor LayoutEditorView_Anchor_previewAnchor(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewAnchor;
  }-*/;

  native static void LayoutEditorView_Anchor_previewAnchor(LayoutEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewAnchor = value;
  }-*/;

  native static ListItem LayoutEditorView_ListItem_designTab(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designTab;
  }-*/;

  native static void LayoutEditorView_ListItem_designTab(LayoutEditorView instance, ListItem value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designTab = value;
  }-*/;

  native static ListItem LayoutEditorView_ListItem_previewTab(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewTab;
  }-*/;

  native static void LayoutEditorView_ListItem_previewTab(LayoutEditorView instance, ListItem value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewTab = value;
  }-*/;

  native static Div LayoutEditorView_Div_mainDiv(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::mainDiv;
  }-*/;

  native static void LayoutEditorView_Div_mainDiv(LayoutEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::mainDiv = value;
  }-*/;

  native static Div LayoutEditorView_Div_previewDiv(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewDiv;
  }-*/;

  native static void LayoutEditorView_Div_previewDiv(LayoutEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewDiv = value;
  }-*/;

  native static Div LayoutEditorView_Div_designDiv(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designDiv;
  }-*/;

  native static void LayoutEditorView_Div_designDiv(LayoutEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designDiv = value;
  }-*/;

  native static Div LayoutEditorView_Div_container(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::container;
  }-*/;

  native static void LayoutEditorView_Div_container(LayoutEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::container = value;
  }-*/;

  native static Div LayoutEditorView_Div_tabsDiv(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::tabsDiv;
  }-*/;

  native static void LayoutEditorView_Div_tabsDiv(LayoutEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::tabsDiv = value;
  }-*/;

  native static Anchor LayoutEditorView_Anchor_designAnchor(LayoutEditorView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designAnchor;
  }-*/;

  native static void LayoutEditorView_Anchor_designAnchor(LayoutEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designAnchor = value;
  }-*/;

  public native static void LayoutEditorView_designTabClicked_ClickEvent(LayoutEditorView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::designTabClicked(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void LayoutEditorView_previewTabClicked_ClickEvent(LayoutEditorView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.LayoutEditorView::previewTabClicked(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}