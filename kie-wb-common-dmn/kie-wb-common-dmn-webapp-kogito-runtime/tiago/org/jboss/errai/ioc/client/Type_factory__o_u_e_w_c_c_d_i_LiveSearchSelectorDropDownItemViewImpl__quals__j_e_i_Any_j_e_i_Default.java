package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
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
import org.jboss.errai.common.client.dom.ListItem;
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
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchSelectorItemView;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemView;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl;

public class Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchSelectorDropDownItemViewImpl> { public interface o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LiveSearchSelectorDropDownItemViewImpl.class, "Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LiveSearchSelectorDropDownItemViewImpl.class, Object.class, LiveSearchSelectorDropDownItemView.class, LiveSearchSelectorItemView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource) GWT.create(o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public LiveSearchSelectorDropDownItemViewImpl createInstance(final ContextManager contextManager) {
    final LiveSearchSelectorDropDownItemViewImpl instance = new LiveSearchSelectorDropDownItemViewImpl();
    setIncompleteInstance(instance);
    final Anchor LiveSearchSelectorDropDownItemViewImpl_itemAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchSelectorDropDownItemViewImpl_itemAnchor);
    LiveSearchSelectorDropDownItemViewImpl_Anchor_itemAnchor(instance, LiveSearchSelectorDropDownItemViewImpl_itemAnchor);
    final Span LiveSearchSelectorDropDownItemViewImpl_itemIcon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchSelectorDropDownItemViewImpl_itemIcon);
    LiveSearchSelectorDropDownItemViewImpl_Span_itemIcon(instance, LiveSearchSelectorDropDownItemViewImpl_itemIcon);
    final ListItem LiveSearchSelectorDropDownItemViewImpl_item = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchSelectorDropDownItemViewImpl_item);
    LiveSearchSelectorDropDownItemViewImpl_ListItem_item(instance, LiveSearchSelectorDropDownItemViewImpl_item);
    final Span LiveSearchSelectorDropDownItemViewImpl_itemText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchSelectorDropDownItemViewImpl_itemText);
    LiveSearchSelectorDropDownItemViewImpl_Span_itemText(instance, LiveSearchSelectorDropDownItemViewImpl_itemText);
    o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource templateForLiveSearchSelectorDropDownItemViewImpl = GWT.create(o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImplTemplateResource.class);
    Element parentElementForTemplateOfLiveSearchSelectorDropDownItemViewImpl = TemplateUtil.getRootTemplateParentElement(templateForLiveSearchSelectorDropDownItemViewImpl.getContents().getText(), "org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchSelectorDropDownItemViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchSelectorDropDownItemViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("item", new DataFieldMeta());
    dataFieldMetas.put("itemAnchor", new DataFieldMeta());
    dataFieldMetas.put("itemText", new DataFieldMeta());
    dataFieldMetas.put("itemIcon", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_ListItem_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_Anchor_itemAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "itemAnchor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_Span_itemText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "itemText");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/items/LiveSearchSelectorDropDownItemViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_Span_itemIcon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "itemIcon");
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_ListItem_item(instance))));
    templateFieldsMap.put("itemAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_Anchor_itemAnchor(instance))));
    templateFieldsMap.put("itemText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_Span_itemText(instance))));
    templateFieldsMap.put("itemIcon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchSelectorDropDownItemViewImpl_Span_itemIcon(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchSelectorDropDownItemViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("itemAnchor"), new KeyDownHandler() {
      public void onKeyDown(KeyDownEvent event) {
        LiveSearchSelectorDropDownItemViewImpl_onEnterKeyDown_KeyDownEvent(instance, event);
      }
    }, KeyDownEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("itemAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onItemClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LiveSearchSelectorDropDownItemViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final LiveSearchSelectorDropDownItemViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span LiveSearchSelectorDropDownItemViewImpl_Span_itemText(LiveSearchSelectorDropDownItemViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::itemText;
  }-*/;

  native static void LiveSearchSelectorDropDownItemViewImpl_Span_itemText(LiveSearchSelectorDropDownItemViewImpl instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::itemText = value;
  }-*/;

  native static Anchor LiveSearchSelectorDropDownItemViewImpl_Anchor_itemAnchor(LiveSearchSelectorDropDownItemViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::itemAnchor;
  }-*/;

  native static void LiveSearchSelectorDropDownItemViewImpl_Anchor_itemAnchor(LiveSearchSelectorDropDownItemViewImpl instance, Anchor value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::itemAnchor = value;
  }-*/;

  native static Span LiveSearchSelectorDropDownItemViewImpl_Span_itemIcon(LiveSearchSelectorDropDownItemViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::itemIcon;
  }-*/;

  native static void LiveSearchSelectorDropDownItemViewImpl_Span_itemIcon(LiveSearchSelectorDropDownItemViewImpl instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::itemIcon = value;
  }-*/;

  native static ListItem LiveSearchSelectorDropDownItemViewImpl_ListItem_item(LiveSearchSelectorDropDownItemViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::item;
  }-*/;

  native static void LiveSearchSelectorDropDownItemViewImpl_ListItem_item(LiveSearchSelectorDropDownItemViewImpl instance, ListItem value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::item = value;
  }-*/;

  public native static void LiveSearchSelectorDropDownItemViewImpl_onEnterKeyDown_KeyDownEvent(LiveSearchSelectorDropDownItemViewImpl instance, KeyDownEvent a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl::onEnterKeyDown(Lcom/google/gwt/event/dom/client/KeyDownEvent;)(a0);
  }-*/;
}