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
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
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
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView;

public class Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPropertiesView> { public interface o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorPropertiesView.class, "Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorPropertiesView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource) GWT.create(o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public LayoutEditorPropertiesView createInstance(final ContextManager contextManager) {
    final LayoutEditorPropertiesView instance = new LayoutEditorPropertiesView();
    setIncompleteInstance(instance);
    final Anchor LayoutEditorPropertiesView_clearAllAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_clearAllAnchor);
    LayoutEditorPropertiesView_Anchor_clearAllAnchor(instance, LayoutEditorPropertiesView_clearAllAnchor);
    final Div LayoutEditorPropertiesView_partsFieldContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_partsFieldContainer);
    LayoutEditorPropertiesView_Div_partsFieldContainer(instance, LayoutEditorPropertiesView_partsFieldContainer);
    final HTMLSelectElement LayoutEditorPropertiesView_partsSelect = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_partsSelect);
    LayoutEditorPropertiesView_HTMLSelectElement_partsSelect(instance, LayoutEditorPropertiesView_partsSelect);
    final Span LayoutEditorPropertiesView_selectorLabel = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_selectorLabel);
    LayoutEditorPropertiesView_Span_selectorLabel(instance, LayoutEditorPropertiesView_selectorLabel);
    final Span LayoutEditorPropertiesView_propertiesLabel = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_propertiesLabel);
    LayoutEditorPropertiesView_Span_propertiesLabel(instance, LayoutEditorPropertiesView_propertiesLabel);
    final Div LayoutEditorPropertiesView_selectorDiv = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_selectorDiv);
    LayoutEditorPropertiesView_Div_selectorDiv(instance, LayoutEditorPropertiesView_selectorDiv);
    final Div LayoutEditorPropertiesView_elementDiv = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_elementDiv);
    LayoutEditorPropertiesView_Div_elementDiv(instance, LayoutEditorPropertiesView_elementDiv);
    final HTMLOptionElement LayoutEditorPropertiesView_partOption = (HTMLOptionElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutEditorPropertiesView_partOption);
    LayoutEditorPropertiesView_HTMLOptionElement_partOption(instance, LayoutEditorPropertiesView_partOption);
    o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource templateForLayoutEditorPropertiesView = GWT.create(o_u_e_l_e_c_w_LayoutEditorPropertiesViewTemplateResource.class);
    Element parentElementForTemplateOfLayoutEditorPropertiesView = TemplateUtil.getRootTemplateParentElement(templateForLayoutEditorPropertiesView.getContents().getText(), "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutEditorPropertiesView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutEditorPropertiesView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(8);
    dataFieldMetas.put("selectorDiv", new DataFieldMeta());
    dataFieldMetas.put("elementDiv", new DataFieldMeta());
    dataFieldMetas.put("selectorLabel", new DataFieldMeta());
    dataFieldMetas.put("propertiesLabel", new DataFieldMeta());
    dataFieldMetas.put("clearAllAnchor", new DataFieldMeta());
    dataFieldMetas.put("partOption", new DataFieldMeta());
    dataFieldMetas.put("partsSelect", new DataFieldMeta());
    dataFieldMetas.put("partsFieldContainer", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Div_selectorDiv(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selectorDiv");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Div_elementDiv(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "elementDiv");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Span_selectorLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selectorLabel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Span_propertiesLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "propertiesLabel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Anchor_clearAllAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "clearAllAnchor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_HTMLOptionElement_partOption(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "partOption");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_HTMLSelectElement_partsSelect(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "partsSelect");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView", "org/uberfire/ext/layout/editor/client/widgets/LayoutEditorPropertiesView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Div_partsFieldContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "partsFieldContainer");
    templateFieldsMap.put("selectorDiv", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Div_selectorDiv(instance))));
    templateFieldsMap.put("elementDiv", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Div_elementDiv(instance))));
    templateFieldsMap.put("selectorLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Span_selectorLabel(instance))));
    templateFieldsMap.put("propertiesLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Span_propertiesLabel(instance))));
    templateFieldsMap.put("clearAllAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Anchor_clearAllAnchor(instance))));
    templateFieldsMap.put("partOption", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_HTMLOptionElement_partOption(instance))));
    templateFieldsMap.put("partsSelect", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_HTMLSelectElement_partsSelect(instance))));
    templateFieldsMap.put("partsFieldContainer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutEditorPropertiesView_Div_partsFieldContainer(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutEditorPropertiesView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("clearAllAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        LayoutEditorPropertiesView_onClearAll_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutEditorPropertiesView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutEditorPropertiesView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLSelectElement LayoutEditorPropertiesView_HTMLSelectElement_partsSelect(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::partsSelect;
  }-*/;

  native static void LayoutEditorPropertiesView_HTMLSelectElement_partsSelect(LayoutEditorPropertiesView instance, HTMLSelectElement value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::partsSelect = value;
  }-*/;

  native static Div LayoutEditorPropertiesView_Div_partsFieldContainer(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::partsFieldContainer;
  }-*/;

  native static void LayoutEditorPropertiesView_Div_partsFieldContainer(LayoutEditorPropertiesView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::partsFieldContainer = value;
  }-*/;

  native static Anchor LayoutEditorPropertiesView_Anchor_clearAllAnchor(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::clearAllAnchor;
  }-*/;

  native static void LayoutEditorPropertiesView_Anchor_clearAllAnchor(LayoutEditorPropertiesView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::clearAllAnchor = value;
  }-*/;

  native static Span LayoutEditorPropertiesView_Span_selectorLabel(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::selectorLabel;
  }-*/;

  native static void LayoutEditorPropertiesView_Span_selectorLabel(LayoutEditorPropertiesView instance, Span value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::selectorLabel = value;
  }-*/;

  native static Div LayoutEditorPropertiesView_Div_elementDiv(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::elementDiv;
  }-*/;

  native static void LayoutEditorPropertiesView_Div_elementDiv(LayoutEditorPropertiesView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::elementDiv = value;
  }-*/;

  native static HTMLOptionElement LayoutEditorPropertiesView_HTMLOptionElement_partOption(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::partOption;
  }-*/;

  native static void LayoutEditorPropertiesView_HTMLOptionElement_partOption(LayoutEditorPropertiesView instance, HTMLOptionElement value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::partOption = value;
  }-*/;

  native static Div LayoutEditorPropertiesView_Div_selectorDiv(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::selectorDiv;
  }-*/;

  native static void LayoutEditorPropertiesView_Div_selectorDiv(LayoutEditorPropertiesView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::selectorDiv = value;
  }-*/;

  native static Span LayoutEditorPropertiesView_Span_propertiesLabel(LayoutEditorPropertiesView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::propertiesLabel;
  }-*/;

  native static void LayoutEditorPropertiesView_Span_propertiesLabel(LayoutEditorPropertiesView instance, Span value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::propertiesLabel = value;
  }-*/;

  public native static void LayoutEditorPropertiesView_onClearAll_ClickEvent(LayoutEditorPropertiesView instance, ClickEvent a0) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesView::onClearAll(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}