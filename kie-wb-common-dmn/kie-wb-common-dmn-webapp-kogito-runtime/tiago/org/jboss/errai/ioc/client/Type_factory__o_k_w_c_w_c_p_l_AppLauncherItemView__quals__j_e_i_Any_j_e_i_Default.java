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
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView;

public class Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherItemView> { public interface o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html") public TextResource getContents();
  @Source("org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppLauncherItemView.class, "Type_factory__o_k_w_c_w_c_p_l_AppLauncherItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppLauncherItemView.class, Object.class, IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource) GWT.create(o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public AppLauncherItemView createInstance(final ContextManager contextManager) {
    final AppLauncherItemView instance = new AppLauncherItemView();
    setIncompleteInstance(instance);
    final Anchor AppLauncherItemView_anchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AppLauncherItemView_anchor);
    AppLauncherItemView_Anchor_anchor(instance, AppLauncherItemView_anchor);
    final Span AppLauncherItemView_text = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AppLauncherItemView_text);
    AppLauncherItemView_Span_text(instance, AppLauncherItemView_text);
    final Span AppLauncherItemView_icon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AppLauncherItemView_icon);
    AppLauncherItemView_Span_icon(instance, AppLauncherItemView_icon);
    o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource templateForAppLauncherItemView = GWT.create(o_k_w_c_w_c_p_l_AppLauncherItemViewTemplateResource.class);
    Element parentElementForTemplateOfAppLauncherItemView = TemplateUtil.getRootTemplateParentElement(templateForAppLauncherItemView.getContents().getText(), "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAppLauncherItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAppLauncherItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("btn", new DataFieldMeta());
    dataFieldMetas.put("fa", new DataFieldMeta());
    dataFieldMetas.put("text", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView", "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherItemView_Anchor_anchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "btn");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView", "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherItemView_Span_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "fa");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView", "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherItemView_Span_text(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "text");
    templateFieldsMap.put("btn", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherItemView_Anchor_anchor(instance))));
    templateFieldsMap.put("fa", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherItemView_Span_icon(instance))));
    templateFieldsMap.put("text", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherItemView_Span_text(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAppLauncherItemView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AppLauncherItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AppLauncherItemView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Anchor AppLauncherItemView_Anchor_anchor(AppLauncherItemView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView::anchor;
  }-*/;

  native static void AppLauncherItemView_Anchor_anchor(AppLauncherItemView instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView::anchor = value;
  }-*/;

  native static Span AppLauncherItemView_Span_icon(AppLauncherItemView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView::icon;
  }-*/;

  native static void AppLauncherItemView_Span_icon(AppLauncherItemView instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView::icon = value;
  }-*/;

  native static Span AppLauncherItemView_Span_text(AppLauncherItemView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView::text;
  }-*/;

  native static void AppLauncherItemView_Span_text(AppLauncherItemView instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherItemView::text = value;
  }-*/;
}