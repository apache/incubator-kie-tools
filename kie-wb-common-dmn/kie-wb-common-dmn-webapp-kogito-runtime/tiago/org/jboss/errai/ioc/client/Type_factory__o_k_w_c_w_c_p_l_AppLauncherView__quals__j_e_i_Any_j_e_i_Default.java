package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView;

public class Type_factory__o_k_w_c_w_c_p_l_AppLauncherView__quals__j_e_i_Any_j_e_i_Default extends Factory<AppLauncherView> { public interface o_k_w_c_w_c_p_l_AppLauncherViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherView.html") public TextResource getContents();
  @Source("org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_w_c_p_l_AppLauncherView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AppLauncherView.class, "Type_factory__o_k_w_c_w_c_p_l_AppLauncherView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppLauncherView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherPresenter.AppLauncherView.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_w_c_p_l_AppLauncherViewTemplateResource) GWT.create(o_k_w_c_w_c_p_l_AppLauncherViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public AppLauncherView createInstance(final ContextManager contextManager) {
    final AppLauncherView instance = new AppLauncherView();
    setIncompleteInstance(instance);
    final Div AppLauncherView_rightColumn = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AppLauncherView_rightColumn);
    AppLauncherView_Div_rightColumn(instance, AppLauncherView_rightColumn);
    final Div AppLauncherView_leftColumn = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AppLauncherView_leftColumn);
    AppLauncherView_Div_leftColumn(instance, AppLauncherView_leftColumn);
    final ManagedInstance AppLauncherView_appLauncherItemViews = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AppLauncherItemView.class }, new Annotation[] { });
    registerDependentScopedReference(instance, AppLauncherView_appLauncherItemViews);
    AppLauncherView_ManagedInstance_appLauncherItemViews(instance, AppLauncherView_appLauncherItemViews);
    o_k_w_c_w_c_p_l_AppLauncherViewTemplateResource templateForAppLauncherView = GWT.create(o_k_w_c_w_c_p_l_AppLauncherViewTemplateResource.class);
    Element parentElementForTemplateOfAppLauncherView = TemplateUtil.getRootTemplateParentElement(templateForAppLauncherView.getContents().getText(), "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAppLauncherView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAppLauncherView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("left-column", new DataFieldMeta());
    dataFieldMetas.put("right-column", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView", "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherView_Div_leftColumn(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "left-column");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView", "org/kie/workbench/common/widgets/client/popups/launcher/AppLauncherView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherView_Div_rightColumn(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "right-column");
    templateFieldsMap.put("left-column", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherView_Div_leftColumn(instance))));
    templateFieldsMap.put("right-column", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AppLauncherView_Div_rightColumn(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAppLauncherView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AppLauncherView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AppLauncherView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static ManagedInstance AppLauncherView_ManagedInstance_appLauncherItemViews(AppLauncherView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView::appLauncherItemViews;
  }-*/;

  native static void AppLauncherView_ManagedInstance_appLauncherItemViews(AppLauncherView instance, ManagedInstance<AppLauncherItemView> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView::appLauncherItemViews = value;
  }-*/;

  native static Div AppLauncherView_Div_rightColumn(AppLauncherView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView::rightColumn;
  }-*/;

  native static void AppLauncherView_Div_rightColumn(AppLauncherView instance, Div value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView::rightColumn = value;
  }-*/;

  native static Div AppLauncherView_Div_leftColumn(AppLauncherView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView::leftColumn;
  }-*/;

  native static void AppLauncherView_Div_leftColumn(AppLauncherView instance, Div value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.launcher.AppLauncherView::leftColumn = value;
  }-*/;
}