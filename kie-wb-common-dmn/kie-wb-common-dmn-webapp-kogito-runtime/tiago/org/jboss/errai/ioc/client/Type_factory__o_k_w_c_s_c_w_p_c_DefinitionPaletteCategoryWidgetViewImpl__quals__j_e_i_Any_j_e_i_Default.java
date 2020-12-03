package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
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
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
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
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteCategoryWidgetViewImpl> { public interface o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionPaletteCategoryWidgetViewImpl.class, "Type_factory__o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionPaletteCategoryWidgetViewImpl.class, Object.class, DefinitionPaletteCategoryWidgetView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource) GWT.create(o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public DefinitionPaletteCategoryWidgetViewImpl createInstance(final ContextManager contextManager) {
    final DefinitionPaletteCategoryWidgetViewImpl instance = new DefinitionPaletteCategoryWidgetViewImpl();
    setIncompleteInstance(instance);
    final DOMGlyphRenderers DefinitionPaletteCategoryWidgetViewImpl_domGlyphRenderers = (DOMGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    DefinitionPaletteCategoryWidgetViewImpl_DOMGlyphRenderers_domGlyphRenderers(instance, DefinitionPaletteCategoryWidgetViewImpl_domGlyphRenderers);
    final Document DefinitionPaletteCategoryWidgetViewImpl_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefinitionPaletteCategoryWidgetViewImpl_document);
    DefinitionPaletteCategoryWidgetViewImpl_Document_document(instance, DefinitionPaletteCategoryWidgetViewImpl_document);
    final Button DefinitionPaletteCategoryWidgetViewImpl_closeCategoryButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteCategoryWidgetViewImpl_closeCategoryButton);
    DefinitionPaletteCategoryWidgetViewImpl_Button_closeCategoryButton(instance, DefinitionPaletteCategoryWidgetViewImpl_closeCategoryButton);
    final Div DefinitionPaletteCategoryWidgetViewImpl_floatingPanel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteCategoryWidgetViewImpl_floatingPanel);
    DefinitionPaletteCategoryWidgetViewImpl_Div_floatingPanel(instance, DefinitionPaletteCategoryWidgetViewImpl_floatingPanel);
    final Button DefinitionPaletteCategoryWidgetViewImpl_categoryIcon = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteCategoryWidgetViewImpl_categoryIcon);
    DefinitionPaletteCategoryWidgetViewImpl_Button_categoryIcon(instance, DefinitionPaletteCategoryWidgetViewImpl_categoryIcon);
    final ListItem DefinitionPaletteCategoryWidgetViewImpl_listGroupItem = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteCategoryWidgetViewImpl_listGroupItem);
    DefinitionPaletteCategoryWidgetViewImpl_ListItem_listGroupItem(instance, DefinitionPaletteCategoryWidgetViewImpl_listGroupItem);
    o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource templateForDefinitionPaletteCategoryWidgetViewImpl = GWT.create(o_k_w_c_s_c_w_p_c_DefinitionPaletteCategoryWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfDefinitionPaletteCategoryWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDefinitionPaletteCategoryWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteCategoryWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteCategoryWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("listGroupItem", new DataFieldMeta());
    dataFieldMetas.put("categoryIcon", new DataFieldMeta());
    dataFieldMetas.put("floatingPanel", new DataFieldMeta());
    dataFieldMetas.put("closeCategoryButton", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_ListItem_listGroupItem(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "listGroupItem");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_Button_categoryIcon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "categoryIcon");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_Div_floatingPanel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "floatingPanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/DefinitionPaletteCategoryWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_Button_closeCategoryButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "closeCategoryButton");
    templateFieldsMap.put("listGroupItem", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_ListItem_listGroupItem(instance))));
    templateFieldsMap.put("categoryIcon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_Button_categoryIcon(instance))));
    templateFieldsMap.put("floatingPanel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_Div_floatingPanel(instance))));
    templateFieldsMap.put("closeCategoryButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteCategoryWidgetViewImpl_Button_closeCategoryButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteCategoryWidgetViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("closeCategoryButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClose(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("floatingPanel"), new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        instance.onFloatingPanelOutEvent(event);
      }
    }, MouseOutEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("categoryIcon"), new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        instance.onMouseOutEvent(event);
      }
    }, MouseOutEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("categoryIcon"), new MouseUpHandler() {
      public void onMouseUp(MouseUpEvent event) {
        instance.onMouseUp(event);
      }
    }, MouseUpEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("categoryIcon"), new MouseMoveHandler() {
      public void onMouseMove(MouseMoveEvent event) {
        instance.onMouseMove(event);
      }
    }, MouseMoveEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("categoryIcon"), new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        instance.onMouseDown(event);
      }
    }, MouseDownEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefinitionPaletteCategoryWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefinitionPaletteCategoryWidgetViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div DefinitionPaletteCategoryWidgetViewImpl_Div_floatingPanel(DefinitionPaletteCategoryWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::floatingPanel;
  }-*/;

  native static void DefinitionPaletteCategoryWidgetViewImpl_Div_floatingPanel(DefinitionPaletteCategoryWidgetViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::floatingPanel = value;
  }-*/;

  native static ListItem DefinitionPaletteCategoryWidgetViewImpl_ListItem_listGroupItem(DefinitionPaletteCategoryWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::listGroupItem;
  }-*/;

  native static void DefinitionPaletteCategoryWidgetViewImpl_ListItem_listGroupItem(DefinitionPaletteCategoryWidgetViewImpl instance, ListItem value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::listGroupItem = value;
  }-*/;

  native static Button DefinitionPaletteCategoryWidgetViewImpl_Button_categoryIcon(DefinitionPaletteCategoryWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::categoryIcon;
  }-*/;

  native static void DefinitionPaletteCategoryWidgetViewImpl_Button_categoryIcon(DefinitionPaletteCategoryWidgetViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::categoryIcon = value;
  }-*/;

  native static Button DefinitionPaletteCategoryWidgetViewImpl_Button_closeCategoryButton(DefinitionPaletteCategoryWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::closeCategoryButton;
  }-*/;

  native static void DefinitionPaletteCategoryWidgetViewImpl_Button_closeCategoryButton(DefinitionPaletteCategoryWidgetViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::closeCategoryButton = value;
  }-*/;

  native static DOMGlyphRenderers DefinitionPaletteCategoryWidgetViewImpl_DOMGlyphRenderers_domGlyphRenderers(DefinitionPaletteCategoryWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::domGlyphRenderers;
  }-*/;

  native static void DefinitionPaletteCategoryWidgetViewImpl_DOMGlyphRenderers_domGlyphRenderers(DefinitionPaletteCategoryWidgetViewImpl instance, DOMGlyphRenderers value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::domGlyphRenderers = value;
  }-*/;

  native static Document DefinitionPaletteCategoryWidgetViewImpl_Document_document(DefinitionPaletteCategoryWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::document;
  }-*/;

  native static void DefinitionPaletteCategoryWidgetViewImpl_Document_document(DefinitionPaletteCategoryWidgetViewImpl instance, Document value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.DefinitionPaletteCategoryWidgetViewImpl::document = value;
  }-*/;
}