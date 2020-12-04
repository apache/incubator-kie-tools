package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget;

public class Type_factory__o_u_e_l_e_c_w_LayoutDragComponentWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentWidget> { public interface o_u_e_l_e_c_w_LayoutDragComponentWidgetTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentWidget.html") public TextResource getContents(); }
  public Type_factory__o_u_e_l_e_c_w_LayoutDragComponentWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutDragComponentWidget.class, "Type_factory__o_u_e_l_e_c_w_LayoutDragComponentWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutDragComponentWidget.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public LayoutDragComponentWidget createInstance(final ContextManager contextManager) {
    final LayoutDragComponentWidget instance = new LayoutDragComponentWidget();
    setIncompleteInstance(instance);
    final Div LayoutDragComponentWidget_dndcomponent = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutDragComponentWidget_dndcomponent);
    LayoutDragComponentWidget_Div_dndcomponent(instance, LayoutDragComponentWidget_dndcomponent);
    final Event LayoutDragComponentWidget_dragComponentEnd = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DragComponentEndEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, LayoutDragComponentWidget_dragComponentEnd);
    LayoutDragComponentWidget_Event_dragComponentEnd(instance, LayoutDragComponentWidget_dragComponentEnd);
    final Span LayoutDragComponentWidget_title = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutDragComponentWidget_title);
    LayoutDragComponentWidget_Span_title(instance, LayoutDragComponentWidget_title);
    final Span LayoutDragComponentWidget_icon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutDragComponentWidget_icon);
    LayoutDragComponentWidget_Span_icon(instance, LayoutDragComponentWidget_icon);
    o_u_e_l_e_c_w_LayoutDragComponentWidgetTemplateResource templateForLayoutDragComponentWidget = GWT.create(o_u_e_l_e_c_w_LayoutDragComponentWidgetTemplateResource.class);
    Element parentElementForTemplateOfLayoutDragComponentWidget = TemplateUtil.getRootTemplateParentElement(templateForLayoutDragComponentWidget.getContents().getText(), "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentWidget.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutDragComponentWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutDragComponentWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("title", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("dndcomponent", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget", "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentWidget_Span_title(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "title");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget", "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentWidget_Span_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget", "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentWidget_Div_dndcomponent(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dndcomponent");
    templateFieldsMap.put("title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentWidget_Span_title(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentWidget_Span_icon(instance))));
    templateFieldsMap.put("dndcomponent", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentWidget_Div_dndcomponent(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutDragComponentWidget), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutDragComponentWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutDragComponentWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span LayoutDragComponentWidget_Span_icon(LayoutDragComponentWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::icon;
  }-*/;

  native static void LayoutDragComponentWidget_Span_icon(LayoutDragComponentWidget instance, Span value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::icon = value;
  }-*/;

  native static Div LayoutDragComponentWidget_Div_dndcomponent(LayoutDragComponentWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::dndcomponent;
  }-*/;

  native static void LayoutDragComponentWidget_Div_dndcomponent(LayoutDragComponentWidget instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::dndcomponent = value;
  }-*/;

  native static Event LayoutDragComponentWidget_Event_dragComponentEnd(LayoutDragComponentWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::dragComponentEnd;
  }-*/;

  native static void LayoutDragComponentWidget_Event_dragComponentEnd(LayoutDragComponentWidget instance, Event<DragComponentEndEvent> value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::dragComponentEnd = value;
  }-*/;

  native static Span LayoutDragComponentWidget_Span_title(LayoutDragComponentWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::title;
  }-*/;

  native static void LayoutDragComponentWidget_Span_title(LayoutDragComponentWidget instance, Span value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget::title = value;
  }-*/;
}