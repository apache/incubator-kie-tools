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
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter.View;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemView;

public class Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageItemView> { public interface o_u_e_p_c_a_i_AdminPageItemViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPageItemView.class, "Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPageItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_a_i_AdminPageItemViewTemplateResource) GWT.create(o_u_e_p_c_a_i_AdminPageItemViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public AdminPageItemView createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final AdminPageItemView instance = new AdminPageItemView(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Paragraph AdminPageItemView_counterText = (Paragraph) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Paragraph__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageItemView_counterText);
    AdminPageItemView_Paragraph_counterText(instance, AdminPageItemView_counterText);
    final Div AdminPageItemView_counterContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageItemView_counterContainer);
    AdminPageItemView_Div_counterContainer(instance, AdminPageItemView_counterContainer);
    final Div AdminPageItemView_text = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageItemView_text);
    AdminPageItemView_Div_text(instance, AdminPageItemView_text);
    final Div AdminPageItemView_item = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageItemView_item);
    AdminPageItemView_Div_item(instance, AdminPageItemView_item);
    o_u_e_p_c_a_i_AdminPageItemViewTemplateResource templateForAdminPageItemView = GWT.create(o_u_e_p_c_a_i_AdminPageItemViewTemplateResource.class);
    Element parentElementForTemplateOfAdminPageItemView = TemplateUtil.getRootTemplateParentElement(templateForAdminPageItemView.getContents().getText(), "org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("item", new DataFieldMeta());
    dataFieldMetas.put("item-icon", new DataFieldMeta());
    dataFieldMetas.put("item-text", new DataFieldMeta());
    dataFieldMetas.put("item-counter-container", new DataFieldMeta());
    dataFieldMetas.put("item-counter", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.item.AdminPageItemView", "org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Div_item(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.item.AdminPageItemView", "org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(AdminPageItemView_Element_icon(instance));
      }
    }, dataFieldElements, dataFieldMetas, "item-icon");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.item.AdminPageItemView", "org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Div_text(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item-text");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.item.AdminPageItemView", "org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Div_counterContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item-counter-container");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.item.AdminPageItemView", "org/uberfire/ext/preferences/client/admin/item/AdminPageItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Paragraph_counterText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "item-counter");
    templateFieldsMap.put("item", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Div_item(instance))));
    templateFieldsMap.put("item-icon", ElementWrapperWidget.getWidget(AdminPageItemView_Element_icon(instance)));
    templateFieldsMap.put("item-text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Div_text(instance))));
    templateFieldsMap.put("item-counter-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Div_counterContainer(instance))));
    templateFieldsMap.put("item-counter", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageItemView_Paragraph_counterText(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageItemView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("item"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.enter(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AdminPageItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AdminPageItemView instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(AdminPageItemView_Element_icon(instance)));
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div AdminPageItemView_Div_item(AdminPageItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::item;
  }-*/;

  native static void AdminPageItemView_Div_item(AdminPageItemView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::item = value;
  }-*/;

  native static Div AdminPageItemView_Div_counterContainer(AdminPageItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::counterContainer;
  }-*/;

  native static void AdminPageItemView_Div_counterContainer(AdminPageItemView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::counterContainer = value;
  }-*/;

  native static Paragraph AdminPageItemView_Paragraph_counterText(AdminPageItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::counterText;
  }-*/;

  native static void AdminPageItemView_Paragraph_counterText(AdminPageItemView instance, Paragraph value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::counterText = value;
  }-*/;

  native static Div AdminPageItemView_Div_text(AdminPageItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::text;
  }-*/;

  native static void AdminPageItemView_Div_text(AdminPageItemView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::text = value;
  }-*/;

  native static Element AdminPageItemView_Element_icon(AdminPageItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::icon;
  }-*/;

  native static void AdminPageItemView_Element_icon(AdminPageItemView instance, Element value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.item.AdminPageItemView::icon = value;
  }-*/;
}