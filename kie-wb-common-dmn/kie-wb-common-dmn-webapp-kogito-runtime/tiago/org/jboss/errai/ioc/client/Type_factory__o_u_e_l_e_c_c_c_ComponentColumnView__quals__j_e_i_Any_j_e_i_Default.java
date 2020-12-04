package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
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
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
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
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumn.View;
import org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;
import org.uberfire.ext.layout.editor.client.infra.DragHelperComponentColumn;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorFocusController;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;

public class Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default extends Factory<ComponentColumnView> { public interface o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ComponentColumnView.class, "Type_factory__o_u_e_l_e_c_c_c_ComponentColumnView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ComponentColumnView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource) GWT.create(o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public ComponentColumnView createInstance(final ContextManager contextManager) {
    final ComponentColumnView instance = new ComponentColumnView();
    setIncompleteInstance(instance);
    final LayoutEditorFocusController ComponentColumnView_layoutEditorFocusController = (LayoutEditorFocusController) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_LayoutEditorFocusController__quals__j_e_i_Any_j_e_i_Default");
    ComponentColumnView_LayoutEditorFocusController_layoutEditorFocusController(instance, ComponentColumnView_layoutEditorFocusController);
    final DragHelperComponentColumn ComponentColumnView_helper = (DragHelperComponentColumn) contextManager.getInstance("Type_factory__o_u_e_l_e_c_i_DragHelperComponentColumn__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ComponentColumnView_helper);
    ComponentColumnView_DragHelperComponentColumn_helper(instance, ComponentColumnView_helper);
    final Div ComponentColumnView_right = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_right);
    ComponentColumnView_Div_right(instance, ComponentColumnView_right);
    final Document ComponentColumnView_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ComponentColumnView_document);
    ComponentColumnView_Document_document(instance, ComponentColumnView_document);
    final Div ComponentColumnView_col = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_col);
    ComponentColumnView_Div_col(instance, ComponentColumnView_col);
    final Div ComponentColumnView_colUp = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_colUp);
    ComponentColumnView_Div_colUp(instance, ComponentColumnView_colUp);
    final Div ComponentColumnView_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_content);
    ComponentColumnView_Div_content(instance, ComponentColumnView_content);
    final Div ComponentColumnView_row = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_row);
    ComponentColumnView_Div_row(instance, ComponentColumnView_row);
    final KebabWidget ComponentColumnView_kebabWidget = (KebabWidget) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_KebabWidget__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ComponentColumnView_kebabWidget);
    ComponentColumnView_KebabWidget_kebabWidget(instance, ComponentColumnView_kebabWidget);
    final Div ComponentColumnView_colDown = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_colDown);
    ComponentColumnView_Div_colDown(instance, ComponentColumnView_colDown);
    final Div ComponentColumnView_left = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_left);
    ComponentColumnView_Div_left(instance, ComponentColumnView_left);
    final Div ComponentColumnView_contentArea = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ComponentColumnView_contentArea);
    ComponentColumnView_Div_contentArea(instance, ComponentColumnView_contentArea);
    thisInstance.setReference(instance, "dockSelectEventSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent", new AbstractCDIEventCallback<UberfireDocksInteractionEvent>() {
      public void fireEvent(final UberfireDocksInteractionEvent event) {
        instance.dockSelectEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent []";
      }
    }));
    thisInstance.setReference(instance, "resizeEventObserverSubscription", CDI.subscribeLocal("org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent", new AbstractCDIEventCallback<ContainerResizeEvent>() {
      public void fireEvent(final ContainerResizeEvent event) {
        instance.resizeEventObserver(event);
      }
      public String toString() {
        return "Observer: org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent []";
      }
    }));
    o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource templateForComponentColumnView = GWT.create(o_u_e_l_e_c_c_c_ComponentColumnViewTemplateResource.class);
    Element parentElementForTemplateOfComponentColumnView = TemplateUtil.getRootTemplateParentElement(templateForComponentColumnView.getContents().getText(), "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfComponentColumnView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfComponentColumnView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(8);
    dataFieldMetas.put("col", new DataFieldMeta());
    dataFieldMetas.put("colUp", new DataFieldMeta());
    dataFieldMetas.put("row", new DataFieldMeta());
    dataFieldMetas.put("colDown", new DataFieldMeta());
    dataFieldMetas.put("left", new DataFieldMeta());
    dataFieldMetas.put("right", new DataFieldMeta());
    dataFieldMetas.put("content-area", new DataFieldMeta());
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_col(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "col");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_colUp(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "colUp");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_row(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "row");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_colDown(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "colDown");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_left(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "left");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_right(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "right");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_contentArea(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content-area");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView", "org/uberfire/ext/layout/editor/client/components/columns/ComponentColumnView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("col", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_col(instance))));
    templateFieldsMap.put("colUp", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_colUp(instance))));
    templateFieldsMap.put("row", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_row(instance))));
    templateFieldsMap.put("colDown", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_colDown(instance))));
    templateFieldsMap.put("left", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_left(instance))));
    templateFieldsMap.put("right", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_right(instance))));
    templateFieldsMap.put("content-area", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_contentArea(instance))));
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ComponentColumnView_Div_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfComponentColumnView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ComponentColumnView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ComponentColumnView instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "dockSelectEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "resizeEventObserverSubscription", Subscription.class)).remove();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div ComponentColumnView_Div_colUp(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::colUp;
  }-*/;

  native static void ComponentColumnView_Div_colUp(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::colUp = value;
  }-*/;

  native static Div ComponentColumnView_Div_right(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::right;
  }-*/;

  native static void ComponentColumnView_Div_right(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::right = value;
  }-*/;

  native static Div ComponentColumnView_Div_colDown(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::colDown;
  }-*/;

  native static void ComponentColumnView_Div_colDown(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::colDown = value;
  }-*/;

  native static Div ComponentColumnView_Div_left(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::left;
  }-*/;

  native static void ComponentColumnView_Div_left(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::left = value;
  }-*/;

  native static Div ComponentColumnView_Div_col(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::col;
  }-*/;

  native static void ComponentColumnView_Div_col(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::col = value;
  }-*/;

  native static Div ComponentColumnView_Div_row(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::row;
  }-*/;

  native static void ComponentColumnView_Div_row(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::row = value;
  }-*/;

  native static Div ComponentColumnView_Div_content(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::content;
  }-*/;

  native static void ComponentColumnView_Div_content(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::content = value;
  }-*/;

  native static LayoutEditorFocusController ComponentColumnView_LayoutEditorFocusController_layoutEditorFocusController(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::layoutEditorFocusController;
  }-*/;

  native static void ComponentColumnView_LayoutEditorFocusController_layoutEditorFocusController(ComponentColumnView instance, LayoutEditorFocusController value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::layoutEditorFocusController = value;
  }-*/;

  native static Div ComponentColumnView_Div_contentArea(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::contentArea;
  }-*/;

  native static void ComponentColumnView_Div_contentArea(ComponentColumnView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::contentArea = value;
  }-*/;

  native static KebabWidget ComponentColumnView_KebabWidget_kebabWidget(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::kebabWidget;
  }-*/;

  native static void ComponentColumnView_KebabWidget_kebabWidget(ComponentColumnView instance, KebabWidget value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::kebabWidget = value;
  }-*/;

  native static Document ComponentColumnView_Document_document(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::document;
  }-*/;

  native static void ComponentColumnView_Document_document(ComponentColumnView instance, Document value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::document = value;
  }-*/;

  native static DragHelperComponentColumn ComponentColumnView_DragHelperComponentColumn_helper(ComponentColumnView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::helper;
  }-*/;

  native static void ComponentColumnView_DragHelperComponentColumn_helper(ComponentColumnView instance, DragHelperComponentColumn value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ComponentColumnView::helper = value;
  }-*/;
}