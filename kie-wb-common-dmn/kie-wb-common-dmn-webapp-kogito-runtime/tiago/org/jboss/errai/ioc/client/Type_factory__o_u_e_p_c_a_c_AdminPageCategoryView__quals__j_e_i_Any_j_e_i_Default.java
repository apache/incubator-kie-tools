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
import org.jboss.errai.common.client.dom.Div;
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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter.View;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryView;

public class Type_factory__o_u_e_p_c_a_c_AdminPageCategoryView__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageCategoryView> { public interface o_u_e_p_c_a_c_AdminPageCategoryViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/admin/category/AdminPageCategoryView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/admin/category/AdminPageCategoryView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_a_c_AdminPageCategoryView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPageCategoryView.class, "Type_factory__o_u_e_p_c_a_c_AdminPageCategoryView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPageCategoryView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_a_c_AdminPageCategoryViewTemplateResource) GWT.create(o_u_e_p_c_a_c_AdminPageCategoryViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public AdminPageCategoryView createInstance(final ContextManager contextManager) {
    final AdminPageCategoryView instance = new AdminPageCategoryView();
    setIncompleteInstance(instance);
    final Div AdminPageCategoryView_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageCategoryView_content);
    AdminPageCategoryView_Div_content(instance, AdminPageCategoryView_content);
    o_u_e_p_c_a_c_AdminPageCategoryViewTemplateResource templateForAdminPageCategoryView = GWT.create(o_u_e_p_c_a_c_AdminPageCategoryViewTemplateResource.class);
    Element parentElementForTemplateOfAdminPageCategoryView = TemplateUtil.getRootTemplateParentElement(templateForAdminPageCategoryView.getContents().getText(), "org/uberfire/ext/preferences/client/admin/category/AdminPageCategoryView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/admin/category/AdminPageCategoryView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageCategoryView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageCategoryView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("content", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryView", "org/uberfire/ext/preferences/client/admin/category/AdminPageCategoryView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageCategoryView_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageCategoryView_Div_content(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageCategoryView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AdminPageCategoryView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AdminPageCategoryView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div AdminPageCategoryView_Div_content(AdminPageCategoryView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryView::content;
  }-*/;

  native static void AdminPageCategoryView_Div_content(AdminPageCategoryView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryView::content = value;
  }-*/;
}