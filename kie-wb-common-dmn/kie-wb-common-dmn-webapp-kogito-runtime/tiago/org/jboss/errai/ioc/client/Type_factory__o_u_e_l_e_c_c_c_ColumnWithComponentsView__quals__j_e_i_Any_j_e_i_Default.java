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
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponents.View;
import org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;

public class Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default extends Factory<ColumnWithComponentsView> { public interface o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ColumnWithComponentsView.class, "Type_factory__o_u_e_l_e_c_c_c_ColumnWithComponentsView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ColumnWithComponentsView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource) GWT.create(o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public ColumnWithComponentsView createInstance(final ContextManager contextManager) {
    final ColumnWithComponentsView instance = new ColumnWithComponentsView();
    setIncompleteInstance(instance);
    final Document ColumnWithComponentsView_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ColumnWithComponentsView_document);
    ColumnWithComponentsView_Document_document(instance, ColumnWithComponentsView_document);
    final Div ColumnWithComponentsView_colWithComponents = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ColumnWithComponentsView_colWithComponents);
    ColumnWithComponentsView_Div_colWithComponents(instance, ColumnWithComponentsView_colWithComponents);
    final Div ColumnWithComponentsView_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ColumnWithComponentsView_content);
    ColumnWithComponentsView_Div_content(instance, ColumnWithComponentsView_content);
    final Div ColumnWithComponentsView_row = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ColumnWithComponentsView_row);
    ColumnWithComponentsView_Div_row(instance, ColumnWithComponentsView_row);
    final Div ColumnWithComponentsView_innerCol = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ColumnWithComponentsView_innerCol);
    ColumnWithComponentsView_Div_innerCol(instance, ColumnWithComponentsView_innerCol);
    final Div ColumnWithComponentsView_left = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ColumnWithComponentsView_left);
    ColumnWithComponentsView_Div_left(instance, ColumnWithComponentsView_left);
    final Div ColumnWithComponentsView_right = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ColumnWithComponentsView_right);
    ColumnWithComponentsView_Div_right(instance, ColumnWithComponentsView_right);
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
    o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource templateForColumnWithComponentsView = GWT.create(o_u_e_l_e_c_c_c_ColumnWithComponentsViewTemplateResource.class);
    Element parentElementForTemplateOfColumnWithComponentsView = TemplateUtil.getRootTemplateParentElement(templateForColumnWithComponentsView.getContents().getText(), "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfColumnWithComponentsView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfColumnWithComponentsView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("colWithComponents", new DataFieldMeta());
    dataFieldMetas.put("row", new DataFieldMeta());
    dataFieldMetas.put("content", new DataFieldMeta());
    dataFieldMetas.put("left", new DataFieldMeta());
    dataFieldMetas.put("right", new DataFieldMeta());
    dataFieldMetas.put("inner-col-colwithComponents", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView", "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_colWithComponents(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "colWithComponents");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView", "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_row(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "row");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView", "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView", "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_left(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "left");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView", "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_right(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "right");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView", "org/uberfire/ext/layout/editor/client/components/columns/ColumnWithComponentsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_innerCol(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "inner-col-colwithComponents");
    templateFieldsMap.put("colWithComponents", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_colWithComponents(instance))));
    templateFieldsMap.put("row", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_row(instance))));
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_content(instance))));
    templateFieldsMap.put("left", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_left(instance))));
    templateFieldsMap.put("right", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_right(instance))));
    templateFieldsMap.put("inner-col-colwithComponents", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ColumnWithComponentsView_Div_innerCol(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfColumnWithComponentsView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ColumnWithComponentsView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ColumnWithComponentsView instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "dockSelectEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "resizeEventObserverSubscription", Subscription.class)).remove();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div ColumnWithComponentsView_Div_content(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::content;
  }-*/;

  native static void ColumnWithComponentsView_Div_content(ColumnWithComponentsView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::content = value;
  }-*/;

  native static Div ColumnWithComponentsView_Div_innerCol(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::innerCol;
  }-*/;

  native static void ColumnWithComponentsView_Div_innerCol(ColumnWithComponentsView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::innerCol = value;
  }-*/;

  native static Div ColumnWithComponentsView_Div_left(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::left;
  }-*/;

  native static void ColumnWithComponentsView_Div_left(ColumnWithComponentsView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::left = value;
  }-*/;

  native static Div ColumnWithComponentsView_Div_right(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::right;
  }-*/;

  native static void ColumnWithComponentsView_Div_right(ColumnWithComponentsView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::right = value;
  }-*/;

  native static Div ColumnWithComponentsView_Div_row(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::row;
  }-*/;

  native static void ColumnWithComponentsView_Div_row(ColumnWithComponentsView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::row = value;
  }-*/;

  native static Div ColumnWithComponentsView_Div_colWithComponents(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::colWithComponents;
  }-*/;

  native static void ColumnWithComponentsView_Div_colWithComponents(ColumnWithComponentsView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::colWithComponents = value;
  }-*/;

  native static Document ColumnWithComponentsView_Document_document(ColumnWithComponentsView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::document;
  }-*/;

  native static void ColumnWithComponentsView_Document_document(ColumnWithComponentsView instance, Document value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.columns.ColumnWithComponentsView::document = value;
  }-*/;
}