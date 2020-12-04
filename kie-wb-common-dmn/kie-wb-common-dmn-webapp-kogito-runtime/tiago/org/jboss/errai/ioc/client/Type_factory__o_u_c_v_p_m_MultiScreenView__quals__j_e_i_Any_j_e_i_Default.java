package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.multiscreen.MultiScreenView;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

public class Type_factory__o_u_c_v_p_m_MultiScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiScreenView> { public interface o_u_c_v_p_m_MultiScreenViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_m_MultiScreenView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultiScreenView.class, "Type_factory__o_u_c_v_p_m_MultiScreenView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultiScreenView.class, Object.class, IsElement.class, RequiresResize.class });
  }

  public void init(final Context context) {
    StyleInjector.inject(".multi-screen {\n  background-color: #ffffff;\n  height: 100%;\n}\n.multi-screen .page-content-kie {\n  background-color: #ffffff;\n  padding-bottom: 1em;\n}\n.multi-screen .fixed-header + .page-content-kie {\n  padding-top: 4em;\n}\n.multi-screen .page-content-kie:after {\n  content: \" \";\n  display: table;\n  clear: both;\n}\n.multi-screen .main-container .multi-screen-toolbar-pf {\n  background-color: #ffffff;\n  padding: .8em 0;\n  border: 0;\n}\n.multi-screen .multi-screen-toolbar-pf {\n  padding: .8em 1.5em;\n}\n.multi-screen .multi-screen-toolbar-pf .form-group,\n.multi-screen .multi-screen-toolbar-pf .btn-group {\n  margin-bottom: 0;\n  display: flex;\n}\n.multi-screen .multi-screen-toolbar-pf .form-group {\n  padding-right: 10px;\n}\n.multi-screen .multi-screen-toolbar-pf .form-group:last-of-type {\n  border-right: none;\n}\n.multi-screen .multi-screen-toolbar-pf .toolbar-pf-actions {\n  margin-bottom: 0;\n  display: flex;\n  justify-content: space-between;\n  align-items: center;\n}\n.multi-screen .multi-screen-toolbar-pf .toolbar-pf-action-right .form-group .btn + .btn {\n  margin-left: 10px;\n}\n.multi-screen .multi-screen-toolbar-pf .form-group .btn-link {\n  color: #0088ce;\n  font-size: 1.0em;\n}\n.multi-screen .multi-screen-toolbar-pf .form-group .btn-link .pficon,\n.multi-screen .multi-screen-toolbar-pf .form-group .btn-link .fa {\n  font-size: 1.3em;\n  margin-left: 5px;\n}\n.multi-screen .multi-screen-toolbar-pf .form-group .btn-link .pficon-close:not(:hover) {\n  color: #030303;\n}\n.multi-screen .toolbar-data-title-kie {\n  display: table-cell;\n  float: left;\n  font-size: 1.5em;\n  margin-right: 1em;\n}\n.multi-screen .btn-group button {\n  margin: 0px 4px;\n}\n.multi-screen .btn-group .separator-left {\n  margin-left: 4px !important;\n  padding-left: 8px;\n  border-left: 1px solid #d1d1d1;\n}\n.multi-screen .gwt-container {\n  position: relative;\n  top: 0px;\n  left: 0px;\n  width: 100%;\n  height: 100%;\n}\n\n");
  }

  public MultiScreenView createInstance(final ContextManager contextManager) {
    final MultiScreenView instance = new MultiScreenView();
    setIncompleteInstance(instance);
    final Button MultiScreenView_close = (Button) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultiScreenView_close);
    MultiScreenView_Button_close(instance, MultiScreenView_close);
    final HTMLDocument MultiScreenView_document = (HTMLDocument) contextManager.getInstance("Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultiScreenView_document);
    MultiScreenView_HTMLDocument_document(instance, MultiScreenView_document);
    final ResizeFlowPanel MultiScreenView_content = (ResizeFlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_u_c_w_w_l_ResizeFlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MultiScreenView_content);
    MultiScreenView_ResizeFlowPanel_content(instance, MultiScreenView_content);
    final HTMLDivElement MultiScreenView_title = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultiScreenView_title);
    MultiScreenView_HTMLDivElement_title(instance, MultiScreenView_title);
    final HTMLDivElement MultiScreenView_actions = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultiScreenView_actions);
    MultiScreenView_HTMLDivElement_actions(instance, MultiScreenView_actions);
    final HTMLDivElement MultiScreenView_screen = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultiScreenView_screen);
    MultiScreenView_HTMLDivElement_screen(instance, MultiScreenView_screen);
    final HTMLDivElement MultiScreenView_closeGroup = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MultiScreenView_closeGroup);
    MultiScreenView_HTMLDivElement_closeGroup(instance, MultiScreenView_closeGroup);
    o_u_c_v_p_m_MultiScreenViewTemplateResource templateForMultiScreenView = GWT.create(o_u_c_v_p_m_MultiScreenViewTemplateResource.class);
    Element parentElementForTemplateOfMultiScreenView = TemplateUtil.getRootTemplateParentElement(templateForMultiScreenView.getContents().getText(), "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultiScreenView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultiScreenView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("screen", new DataFieldMeta());
    dataFieldMetas.put("content", new DataFieldMeta());
    dataFieldMetas.put("title", new DataFieldMeta());
    dataFieldMetas.put("actions", new DataFieldMeta());
    dataFieldMetas.put("close", new DataFieldMeta());
    dataFieldMetas.put("close-group", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenView", "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_screen(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "screen");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenView", "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return MultiScreenView_ResizeFlowPanel_content(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "content");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenView", "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_title(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "title");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenView", "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_actions(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "actions");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenView", "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(MultiScreenView_Button_close(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "close");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.multiscreen.MultiScreenView", "org/uberfire/client/views/pfly/multiscreen/MultiScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_closeGroup(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "close-group");
    templateFieldsMap.put("screen", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_screen(instance))));
    templateFieldsMap.put("content", MultiScreenView_ResizeFlowPanel_content(instance).asWidget());
    templateFieldsMap.put("title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_title(instance))));
    templateFieldsMap.put("actions", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_actions(instance))));
    templateFieldsMap.put("close", ElementWrapperWidget.getWidget(MultiScreenView_Button_close(instance).getElement(), null));
    templateFieldsMap.put("close-group", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MultiScreenView_HTMLDivElement_closeGroup(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMultiScreenView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MultiScreenView) instance, contextManager);
  }

  public void destroyInstanceHelper(final MultiScreenView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ResizeFlowPanel MultiScreenView_ResizeFlowPanel_content(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::content;
  }-*/;

  native static void MultiScreenView_ResizeFlowPanel_content(MultiScreenView instance, ResizeFlowPanel value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::content = value;
  }-*/;

  native static HTMLDocument MultiScreenView_HTMLDocument_document(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::document;
  }-*/;

  native static void MultiScreenView_HTMLDocument_document(MultiScreenView instance, HTMLDocument value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::document = value;
  }-*/;

  native static HTMLDivElement MultiScreenView_HTMLDivElement_actions(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::actions;
  }-*/;

  native static void MultiScreenView_HTMLDivElement_actions(MultiScreenView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::actions = value;
  }-*/;

  native static HTMLDivElement MultiScreenView_HTMLDivElement_title(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::title;
  }-*/;

  native static void MultiScreenView_HTMLDivElement_title(MultiScreenView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::title = value;
  }-*/;

  native static HTMLDivElement MultiScreenView_HTMLDivElement_closeGroup(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::closeGroup;
  }-*/;

  native static void MultiScreenView_HTMLDivElement_closeGroup(MultiScreenView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::closeGroup = value;
  }-*/;

  native static Button MultiScreenView_Button_close(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::close;
  }-*/;

  native static void MultiScreenView_Button_close(MultiScreenView instance, Button value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::close = value;
  }-*/;

  native static HTMLDivElement MultiScreenView_HTMLDivElement_screen(MultiScreenView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::screen;
  }-*/;

  native static void MultiScreenView_HTMLDivElement_screen(MultiScreenView instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.client.views.pfly.multiscreen.MultiScreenView::screen = value;
  }-*/;
}