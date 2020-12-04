package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteItemWidgetViewImpl> { public interface o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionPaletteItemWidgetViewImpl.class, "Type_factory__o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionPaletteItemWidgetViewImpl.class, Object.class, DefinitionPaletteItemWidgetView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource) GWT.create(o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public DefinitionPaletteItemWidgetViewImpl createInstance(final ContextManager contextManager) {
    final DefinitionPaletteItemWidgetViewImpl instance = new DefinitionPaletteItemWidgetViewImpl();
    setIncompleteInstance(instance);
    final Span DefinitionPaletteItemWidgetViewImpl_icon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteItemWidgetViewImpl_icon);
    DefinitionPaletteItemWidgetViewImpl_Span_icon(instance, DefinitionPaletteItemWidgetViewImpl_icon);
    final DOMGlyphRenderers DefinitionPaletteItemWidgetViewImpl_domGlyphRenderers = (DOMGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    DefinitionPaletteItemWidgetViewImpl_DOMGlyphRenderers_domGlyphRenderers(instance, DefinitionPaletteItemWidgetViewImpl_domGlyphRenderers);
    final Anchor DefinitionPaletteItemWidgetViewImpl_itemAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteItemWidgetViewImpl_itemAnchor);
    DefinitionPaletteItemWidgetViewImpl_Anchor_itemAnchor(instance, DefinitionPaletteItemWidgetViewImpl_itemAnchor);
    final Span DefinitionPaletteItemWidgetViewImpl_name = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteItemWidgetViewImpl_name);
    DefinitionPaletteItemWidgetViewImpl_Span_name(instance, DefinitionPaletteItemWidgetViewImpl_name);
    o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource templateForDefinitionPaletteItemWidgetViewImpl = GWT.create(o_k_w_c_s_c_w_p_c_i_DefinitionPaletteItemWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfDefinitionPaletteItemWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDefinitionPaletteItemWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteItemWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteItemWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("itemAnchor", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("name", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteItemWidgetViewImpl_Anchor_itemAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "itemAnchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteItemWidgetViewImpl_Span_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/items/DefinitionPaletteItemWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteItemWidgetViewImpl_Span_name(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "name");
    templateFieldsMap.put("itemAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteItemWidgetViewImpl_Anchor_itemAnchor(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteItemWidgetViewImpl_Span_icon(instance))));
    templateFieldsMap.put("name", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteItemWidgetViewImpl_Span_name(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteItemWidgetViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("itemAnchor"), new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        instance.onMouseDown(event);
      }
    }, MouseDownEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefinitionPaletteItemWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefinitionPaletteItemWidgetViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static DOMGlyphRenderers DefinitionPaletteItemWidgetViewImpl_DOMGlyphRenderers_domGlyphRenderers(DefinitionPaletteItemWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::domGlyphRenderers;
  }-*/;

  native static void DefinitionPaletteItemWidgetViewImpl_DOMGlyphRenderers_domGlyphRenderers(DefinitionPaletteItemWidgetViewImpl instance, DOMGlyphRenderers value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::domGlyphRenderers = value;
  }-*/;

  native static Span DefinitionPaletteItemWidgetViewImpl_Span_icon(DefinitionPaletteItemWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::icon;
  }-*/;

  native static void DefinitionPaletteItemWidgetViewImpl_Span_icon(DefinitionPaletteItemWidgetViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::icon = value;
  }-*/;

  native static Span DefinitionPaletteItemWidgetViewImpl_Span_name(DefinitionPaletteItemWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::name;
  }-*/;

  native static void DefinitionPaletteItemWidgetViewImpl_Span_name(DefinitionPaletteItemWidgetViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::name = value;
  }-*/;

  native static Anchor DefinitionPaletteItemWidgetViewImpl_Anchor_itemAnchor(DefinitionPaletteItemWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::itemAnchor;
  }-*/;

  native static void DefinitionPaletteItemWidgetViewImpl_Anchor_itemAnchor(DefinitionPaletteItemWidgetViewImpl instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.items.DefinitionPaletteItemWidgetViewImpl::itemAnchor = value;
  }-*/;
}