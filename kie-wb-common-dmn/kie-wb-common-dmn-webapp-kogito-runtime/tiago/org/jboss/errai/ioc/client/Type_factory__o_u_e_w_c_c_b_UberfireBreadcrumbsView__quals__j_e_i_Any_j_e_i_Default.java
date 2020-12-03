package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLOListElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
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
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs.View;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView;

public class Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireBreadcrumbsView> { public interface o_u_e_w_c_c_b_UberfireBreadcrumbsViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/breadcrumbs/UberfireBreadcrumbsView.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/breadcrumbs/UberfireBreadcrumbsView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireBreadcrumbsView.class, "Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UberfireBreadcrumbsView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_b_UberfireBreadcrumbsViewTemplateResource) GWT.create(o_u_e_w_c_c_b_UberfireBreadcrumbsViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public UberfireBreadcrumbsView createInstance(final ContextManager contextManager) {
    final UberfireBreadcrumbsView instance = new UberfireBreadcrumbsView();
    setIncompleteInstance(instance);
    final HTMLOListElement UberfireBreadcrumbsView_breadcrumbs = (HTMLOListElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, UberfireBreadcrumbsView_breadcrumbs);
    UberfireBreadcrumbsView_HTMLOListElement_breadcrumbs(instance, UberfireBreadcrumbsView_breadcrumbs);
    final HTMLDivElement UberfireBreadcrumbsView_breadcrumbsToolbar = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, UberfireBreadcrumbsView_breadcrumbsToolbar);
    UberfireBreadcrumbsView_HTMLDivElement_breadcrumbsToolbar(instance, UberfireBreadcrumbsView_breadcrumbsToolbar);
    final Elemental2DomUtil UberfireBreadcrumbsView_elemental2DomUtil = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, UberfireBreadcrumbsView_elemental2DomUtil);
    UberfireBreadcrumbsView_Elemental2DomUtil_elemental2DomUtil(instance, UberfireBreadcrumbsView_elemental2DomUtil);
    o_u_e_w_c_c_b_UberfireBreadcrumbsViewTemplateResource templateForUberfireBreadcrumbsView = GWT.create(o_u_e_w_c_c_b_UberfireBreadcrumbsViewTemplateResource.class);
    Element parentElementForTemplateOfUberfireBreadcrumbsView = TemplateUtil.getRootTemplateParentElement(templateForUberfireBreadcrumbsView.getContents().getText(), "org/uberfire/ext/widgets/common/client/breadcrumbs/UberfireBreadcrumbsView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/breadcrumbs/UberfireBreadcrumbsView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUberfireBreadcrumbsView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUberfireBreadcrumbsView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("breadcrumbs", new DataFieldMeta());
    dataFieldMetas.put("breadcrumbsToolbar", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView", "org/uberfire/ext/widgets/common/client/breadcrumbs/UberfireBreadcrumbsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(UberfireBreadcrumbsView_HTMLOListElement_breadcrumbs(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "breadcrumbs");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView", "org/uberfire/ext/widgets/common/client/breadcrumbs/UberfireBreadcrumbsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(UberfireBreadcrumbsView_HTMLDivElement_breadcrumbsToolbar(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "breadcrumbsToolbar");
    templateFieldsMap.put("breadcrumbs", ElementWrapperWidget.getWidget(TemplateUtil.asElement(UberfireBreadcrumbsView_HTMLOListElement_breadcrumbs(instance))));
    templateFieldsMap.put("breadcrumbsToolbar", ElementWrapperWidget.getWidget(TemplateUtil.asElement(UberfireBreadcrumbsView_HTMLDivElement_breadcrumbsToolbar(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfUberfireBreadcrumbsView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((UberfireBreadcrumbsView) instance, contextManager);
  }

  public void destroyInstanceHelper(final UberfireBreadcrumbsView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLOListElement UberfireBreadcrumbsView_HTMLOListElement_breadcrumbs(UberfireBreadcrumbsView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView::breadcrumbs;
  }-*/;

  native static void UberfireBreadcrumbsView_HTMLOListElement_breadcrumbs(UberfireBreadcrumbsView instance, HTMLOListElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView::breadcrumbs = value;
  }-*/;

  native static Elemental2DomUtil UberfireBreadcrumbsView_Elemental2DomUtil_elemental2DomUtil(UberfireBreadcrumbsView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView::elemental2DomUtil;
  }-*/;

  native static void UberfireBreadcrumbsView_Elemental2DomUtil_elemental2DomUtil(UberfireBreadcrumbsView instance, Elemental2DomUtil value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView::elemental2DomUtil = value;
  }-*/;

  native static HTMLDivElement UberfireBreadcrumbsView_HTMLDivElement_breadcrumbsToolbar(UberfireBreadcrumbsView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView::breadcrumbsToolbar;
  }-*/;

  native static void UberfireBreadcrumbsView_HTMLDivElement_breadcrumbsToolbar(UberfireBreadcrumbsView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView::breadcrumbsToolbar = value;
  }-*/;
}