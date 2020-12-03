package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenMenuBuilder;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenView;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.panels.MultiPartWidget;

public class Type_factory__o_u_c_v_p_m_MultiScreenPartWidget__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<MultiScreenPartWidget> { public interface o_u_c_v_p_m_MultiScreenPartWidgetTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/multiscreen/MultiScreenPartWidget.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_MultiScreenPartWidget__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(MultiScreenPartWidget.class, "Type_factory__o_u_c_v_p_m_MultiScreenPartWidget__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "MultiScreenPartWidget", true));
    handle.setAssignableTypes(new Class[] { MultiScreenPartWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, MultiPartWidget.class, RequiresResize.class, HasBeforeSelectionHandlers.class, HasSelectionHandlers.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("MultiScreenPartWidget") });
  }

  public MultiScreenPartWidget createInstance(final ContextManager contextManager) {
    final MultiScreenPartWidget instance = new MultiScreenPartWidget();
    setIncompleteInstance(instance);
    final ManagedInstance MultiScreenPartWidget_multiScreenViews = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { MultiScreenView.class }, new Annotation[] { });
    registerDependentScopedReference(instance, MultiScreenPartWidget_multiScreenViews);
    MultiScreenPartWidget_ManagedInstance_multiScreenViews(instance, MultiScreenPartWidget_multiScreenViews);
    final HTMLDivElement MultiScreenPartWidget_content = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultiScreenPartWidget_content);
    MultiScreenPartWidget_HTMLDivElement_content(instance, MultiScreenPartWidget_content);
    final PanelManagerImpl MultiScreenPartWidget_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    MultiScreenPartWidget_PanelManager_panelManager(instance, MultiScreenPartWidget_panelManager);
    final MultiScreenMenuBuilder MultiScreenPartWidget_menuBuilder = (MultiScreenMenuBuilder) contextManager.getInstance("Type_factory__o_u_c_v_p_m_MultiScreenMenuBuilder__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultiScreenPartWidget_menuBuilder);
    MultiScreenPartWidget_MultiScreenMenuBuilder_menuBuilder(instance, MultiScreenPartWidget_menuBuilder);
    o_u_c_v_p_m_MultiScreenPartWidgetTemplateResource templateForMultiScreenPartWidget = GWT.create(o_u_c_v_p_m_MultiScreenPartWidgetTemplateResource.class);
    Element parentElementForTemplateOfMultiScreenPartWidget = TemplateUtil.getRootTemplateParentElement(templateForMultiScreenPartWidget.getContents().getText(), "org/uberfire/client/views/pfly/multiscreen/MultiScreenPartWidget.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/multiscreen/MultiScreenPartWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultiScreenPartWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultiScreenPartWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("parts", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget", "org/uberfire/client/views/pfly/multiscreen/MultiScreenPartWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenPartWidget_HTMLDivElement_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "parts");
    templateFieldsMap.put("parts", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenPartWidget_HTMLDivElement_content(instance))));
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultiScreenPartWidget), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MultiScreenPartWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final MultiScreenPartWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static MultiScreenMenuBuilder MultiScreenPartWidget_MultiScreenMenuBuilder_menuBuilder(MultiScreenPartWidget instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::menuBuilder;
  }-*/;

  native static void MultiScreenPartWidget_MultiScreenMenuBuilder_menuBuilder(MultiScreenPartWidget instance, MultiScreenMenuBuilder value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::menuBuilder = value;
  }-*/;

  native static PanelManager MultiScreenPartWidget_PanelManager_panelManager(MultiScreenPartWidget instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::panelManager;
  }-*/;

  native static void MultiScreenPartWidget_PanelManager_panelManager(MultiScreenPartWidget instance, PanelManager value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::panelManager = value;
  }-*/;

  native static ManagedInstance MultiScreenPartWidget_ManagedInstance_multiScreenViews(MultiScreenPartWidget instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::multiScreenViews;
  }-*/;

  native static void MultiScreenPartWidget_ManagedInstance_multiScreenViews(MultiScreenPartWidget instance, ManagedInstance<MultiScreenView> value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::multiScreenViews = value;
  }-*/;

  native static HTMLDivElement MultiScreenPartWidget_HTMLDivElement_content(MultiScreenPartWidget instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::content;
  }-*/;

  native static void MultiScreenPartWidget_HTMLDivElement_content(MultiScreenPartWidget instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenPartWidget::content = value;
  }-*/;
}