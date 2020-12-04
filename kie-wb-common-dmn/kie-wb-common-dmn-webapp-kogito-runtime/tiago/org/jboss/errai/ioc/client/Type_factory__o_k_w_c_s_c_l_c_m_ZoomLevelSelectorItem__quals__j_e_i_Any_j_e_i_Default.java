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
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem;

public class Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItem__quals__j_e_i_Any_j_e_i_Default extends Factory<ZoomLevelSelectorItem> { public interface o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItemTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ZoomLevelSelectorItem.class, "Type_factory__o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ZoomLevelSelectorItem.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItemTemplateResource) GWT.create(o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItemTemplateResource.class)).getStyle().ensureInjected();
  }

  public ZoomLevelSelectorItem createInstance(final ContextManager contextManager) {
    final ZoomLevelSelectorItem instance = new ZoomLevelSelectorItem();
    setIncompleteInstance(instance);
    final ListItem ZoomLevelSelectorItem_levelItem = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorItem_levelItem);
    ZoomLevelSelectorItem_ListItem_levelItem(instance, ZoomLevelSelectorItem_levelItem);
    final Anchor ZoomLevelSelectorItem_levelItemAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorItem_levelItemAnchor);
    ZoomLevelSelectorItem_Anchor_levelItemAnchor(instance, ZoomLevelSelectorItem_levelItemAnchor);
    final Span ZoomLevelSelectorItem_levelItemText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ZoomLevelSelectorItem_levelItemText);
    ZoomLevelSelectorItem_Span_levelItemText(instance, ZoomLevelSelectorItem_levelItemText);
    o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItemTemplateResource templateForZoomLevelSelectorItem = GWT.create(o_k_w_c_s_c_l_c_m_ZoomLevelSelectorItemTemplateResource.class);
    Element parentElementForTemplateOfZoomLevelSelectorItem = TemplateUtil.getRootTemplateParentElement(templateForZoomLevelSelectorItem.getContents().getText(), "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfZoomLevelSelectorItem));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfZoomLevelSelectorItem));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("levelItem", new DataFieldMeta());
    dataFieldMetas.put("levelItemAnchor", new DataFieldMeta());
    dataFieldMetas.put("levelItemText", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorItem_ListItem_levelItem(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "levelItem");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorItem_Anchor_levelItemAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "levelItemAnchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem", "org/kie/workbench/common/stunner/client/lienzo/components/mediators/ZoomLevelSelectorItem.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorItem_Span_levelItemText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "levelItemText");
    templateFieldsMap.put("levelItem", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorItem_ListItem_levelItem(instance))));
    templateFieldsMap.put("levelItemAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorItem_Anchor_levelItemAnchor(instance))));
    templateFieldsMap.put("levelItemText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ZoomLevelSelectorItem_Span_levelItemText(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfZoomLevelSelectorItem), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("levelItemAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        ZoomLevelSelectorItem_onLevelItemClick_ClickEvent(instance, event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ZoomLevelSelectorItem) instance, contextManager);
  }

  public void destroyInstanceHelper(final ZoomLevelSelectorItem instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ListItem ZoomLevelSelectorItem_ListItem_levelItem(ZoomLevelSelectorItem instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::levelItem;
  }-*/;

  native static void ZoomLevelSelectorItem_ListItem_levelItem(ZoomLevelSelectorItem instance, ListItem value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::levelItem = value;
  }-*/;

  native static Anchor ZoomLevelSelectorItem_Anchor_levelItemAnchor(ZoomLevelSelectorItem instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::levelItemAnchor;
  }-*/;

  native static void ZoomLevelSelectorItem_Anchor_levelItemAnchor(ZoomLevelSelectorItem instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::levelItemAnchor = value;
  }-*/;

  native static Span ZoomLevelSelectorItem_Span_levelItemText(ZoomLevelSelectorItem instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::levelItemText;
  }-*/;

  native static void ZoomLevelSelectorItem_Span_levelItemText(ZoomLevelSelectorItem instance, Span value) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::levelItemText = value;
  }-*/;

  public native static void ZoomLevelSelectorItem_onLevelItemClick_ClickEvent(ZoomLevelSelectorItem instance, ClickEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.components.mediators.ZoomLevelSelectorItem::onLevelItemClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;
}