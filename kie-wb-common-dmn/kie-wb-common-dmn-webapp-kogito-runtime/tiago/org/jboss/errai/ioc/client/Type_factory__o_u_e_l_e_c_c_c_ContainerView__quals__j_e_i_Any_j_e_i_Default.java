package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
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
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.container.Container.View;
import org.uberfire.ext.layout.editor.client.components.container.ContainerView;
import org.uberfire.ext.layout.editor.client.infra.ContainerResizeEvent;

public class Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default extends Factory<ContainerView> { public interface o_u_e_l_e_c_c_c_ContainerViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/components/container/ContainerView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/components/container/ContainerView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ContainerView.class, "Type_factory__o_u_e_l_e_c_c_c_ContainerView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ContainerView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_c_c_ContainerViewTemplateResource) GWT.create(o_u_e_l_e_c_c_c_ContainerViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public ContainerView createInstance(final ContextManager contextManager) {
    final ContainerView instance = new ContainerView();
    setIncompleteInstance(instance);
    final Div ContainerView_layout = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ContainerView_layout);
    ContainerView_Div_layout(instance, ContainerView_layout);
    final PlaceManagerImpl ContainerView_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ContainerView_PlaceManager_placeManager(instance, ContainerView_placeManager);
    final Div ContainerView_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ContainerView_container);
    ContainerView_Div_container(instance, ContainerView_container);
    final Event ContainerView_resizeEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ContainerResizeEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, ContainerView_resizeEvent);
    ContainerView_Event_resizeEvent(instance, ContainerView_resizeEvent);
    final Div ContainerView_header = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ContainerView_header);
    ContainerView_Div_header(instance, ContainerView_header);
    o_u_e_l_e_c_c_c_ContainerViewTemplateResource templateForContainerView = GWT.create(o_u_e_l_e_c_c_c_ContainerViewTemplateResource.class);
    Element parentElementForTemplateOfContainerView = TemplateUtil.getRootTemplateParentElement(templateForContainerView.getContents().getText(), "org/uberfire/ext/layout/editor/client/components/container/ContainerView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/components/container/ContainerView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfContainerView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfContainerView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("header", new DataFieldMeta());
    dataFieldMetas.put("layout", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.container.ContainerView", "org/uberfire/ext/layout/editor/client/components/container/ContainerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ContainerView_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.container.ContainerView", "org/uberfire/ext/layout/editor/client/components/container/ContainerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ContainerView_Div_header(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "header");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.container.ContainerView", "org/uberfire/ext/layout/editor/client/components/container/ContainerView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ContainerView_Div_layout(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "layout");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ContainerView_Div_container(instance))));
    templateFieldsMap.put("header", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ContainerView_Div_header(instance))));
    templateFieldsMap.put("layout", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ContainerView_Div_layout(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfContainerView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ContainerView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ContainerView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div ContainerView_Div_layout(ContainerView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::layout;
  }-*/;

  native static void ContainerView_Div_layout(ContainerView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::layout = value;
  }-*/;

  native static Div ContainerView_Div_header(ContainerView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::header;
  }-*/;

  native static void ContainerView_Div_header(ContainerView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::header = value;
  }-*/;

  native static Div ContainerView_Div_container(ContainerView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::container;
  }-*/;

  native static void ContainerView_Div_container(ContainerView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::container = value;
  }-*/;

  native static Event ContainerView_Event_resizeEvent(ContainerView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::resizeEvent;
  }-*/;

  native static void ContainerView_Event_resizeEvent(ContainerView instance, Event<ContainerResizeEvent> value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::resizeEvent = value;
  }-*/;

  native static PlaceManager ContainerView_PlaceManager_placeManager(ContainerView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::placeManager;
  }-*/;

  native static void ContainerView_PlaceManager_placeManager(ContainerView instance, PlaceManager value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.container.ContainerView::placeManager = value;
  }-*/;
}