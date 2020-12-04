package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionPresenter.View;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionContainer;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;

public class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionPresenterView> { public interface o_k_w_c_s_c_w_p_s_i_SessionPresenterViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionPresenterView.class, "Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPresenterView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionPresenterView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class, RequiresResize.class, ProvidesResize.class });
  }

  public void init(final Context context) {
    StyleInjector.inject(".session-container {\n  position: absolute;\n  overflow: hidden;\n  width: 100%;\n  height: 100%;\n}\n.diagram-container {\n  display: flex;\n  width: 100%;\n  height: 100%;\n}\n.loading-panel {\n  margin: 5px;\n  position: absolute;\n  z-index: 1000;\n  padding: 5px;\n}\n.toolbar-panel {\n  position: relative;\n  z-index: 1;\n  top: 50px;\n  margin: 5px;\n  text-align: center;\n}\n.session-header-container {\n  position: absolute;\n  z-index: 1;\n  left: 0;\n  transition: 0ms linear;\n  -webkit-transition: 0ms linear;\n  /* Safari 3.1 to 6.0 */\n  -moz-user-select: -moz-none;\n  -khtml-user-select: none;\n  -webkit-user-select: none;\n  /* Introduced in IE 10.\n       See http://ie.microsoft.com/testdrive/HTML5/msUserSelect/\n    */\n  -ms-user-select: none;\n  user-select: none;\n}\n.palette-panel {\n  position: relative;\n  float: left;\n  z-index: 1;\n  left: 0;\n  height: 100%;\n  transition: 0ms linear;\n  -webkit-transition: 0ms linear;\n  /* Safari 3.1 to 6.0 */\n  -moz-user-select: -moz-none;\n  -khtml-user-select: none;\n  -webkit-user-select: none;\n  /* Introduced in IE 10.\n       See http://ie.microsoft.com/testdrive/HTML5/msUserSelect/\n    */\n  -ms-user-select: none;\n  user-select: none;\n}\n.canvas-panel {\n  position: relative;\n  float: right;\n  z-index: 0;\n  left: 0;\n  display: flex;\n  width: 100%;\n  height: 100%;\n}\n.kie-session-notification {\n  padding: 1rem 2rem 2rem;\n}\n.kie-session-notification [data-notify=\"title\"] {\n  font-weight: 700;\n  color: rgba(0, 0, 0, 0.75);\n  padding-left: 0.5rem;\n}\n.kie-session-notification [data-notify=\"message\"] {\n  display: block;\n  padding: 0;\n  margin-top: 0.5rem;\n}\n\n");
  }

  public SessionPresenterView createInstance(final ContextManager contextManager) {
    final SessionPresenterView instance = new SessionPresenterView();
    setIncompleteInstance(instance);
    final SessionContainer SessionPresenterView_sessionContainer = (SessionContainer) contextManager.getInstance("ExtensionProvided_factory__o_k_w_c_s_c_w_p_s_i_SessionContainer__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_sessionContainer);
    SessionPresenterView_SessionContainer_sessionContainer(instance, SessionPresenterView_sessionContainer);
    final Label SessionPresenterView_loadingPanel = (Label) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_loadingPanel);
    SessionPresenterView_Label_loadingPanel(instance, SessionPresenterView_loadingPanel);
    final ResizeFlowPanel SessionPresenterView_canvasPanel = (ResizeFlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_u_c_w_w_l_ResizeFlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_canvasPanel);
    SessionPresenterView_ResizeFlowPanel_canvasPanel(instance, SessionPresenterView_canvasPanel);
    final FlowPanel SessionPresenterView_sessionHeaderContainer = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_sessionHeaderContainer);
    SessionPresenterView_FlowPanel_sessionHeaderContainer(instance, SessionPresenterView_sessionHeaderContainer);
    final TranslationService SessionPresenterView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_translationService);
    SessionPresenterView_TranslationService_translationService(instance, SessionPresenterView_translationService);
    final FlowPanel SessionPresenterView_toolbarPanel = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_toolbarPanel);
    SessionPresenterView_FlowPanel_toolbarPanel(instance, SessionPresenterView_toolbarPanel);
    final FlowPanel SessionPresenterView_palettePanel = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, SessionPresenterView_palettePanel);
    SessionPresenterView_FlowPanel_palettePanel(instance, SessionPresenterView_palettePanel);
    thisInstance.setReference(instance, "onCanvasFocusedSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent", new AbstractCDIEventCallback<CanvasFocusedShapeEvent>() {
      public void fireEvent(final CanvasFocusedShapeEvent event) {
        SessionPresenterView_onCanvasFocusedSelectionEvent_CanvasFocusedShapeEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasFocusedShapeEvent []";
      }
    }));
    o_k_w_c_s_c_w_p_s_i_SessionPresenterViewTemplateResource templateForSessionPresenterView = GWT.create(o_k_w_c_s_c_w_p_s_i_SessionPresenterViewTemplateResource.class);
    Element parentElementForTemplateOfSessionPresenterView = TemplateUtil.getRootTemplateParentElement(templateForSessionPresenterView.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSessionPresenterView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSessionPresenterView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("loadingPanel", new DataFieldMeta());
    dataFieldMetas.put("sessionHeaderContainer", new DataFieldMeta());
    dataFieldMetas.put("toolbarPanel", new DataFieldMeta());
    dataFieldMetas.put("canvasPanel", new DataFieldMeta());
    dataFieldMetas.put("palettePanel", new DataFieldMeta());
    dataFieldMetas.put("sessionContainer", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView", "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", new Supplier<Widget>() {
      public Widget get() {
        return SessionPresenterView_Label_loadingPanel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "loadingPanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView", "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", new Supplier<Widget>() {
      public Widget get() {
        return SessionPresenterView_FlowPanel_sessionHeaderContainer(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "sessionHeaderContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView", "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", new Supplier<Widget>() {
      public Widget get() {
        return SessionPresenterView_FlowPanel_toolbarPanel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "toolbarPanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView", "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", new Supplier<Widget>() {
      public Widget get() {
        return SessionPresenterView_ResizeFlowPanel_canvasPanel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "canvasPanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView", "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", new Supplier<Widget>() {
      public Widget get() {
        return SessionPresenterView_FlowPanel_palettePanel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "palettePanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView", "org/kie/workbench/common/stunner/client/widgets/presenters/session/impl/SessionPresenterView.html", new Supplier<Widget>() {
      public Widget get() {
        return SessionPresenterView_SessionContainer_sessionContainer(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "sessionContainer");
    templateFieldsMap.put("loadingPanel", SessionPresenterView_Label_loadingPanel(instance).asWidget());
    templateFieldsMap.put("sessionHeaderContainer", SessionPresenterView_FlowPanel_sessionHeaderContainer(instance).asWidget());
    templateFieldsMap.put("toolbarPanel", SessionPresenterView_FlowPanel_toolbarPanel(instance).asWidget());
    templateFieldsMap.put("canvasPanel", SessionPresenterView_ResizeFlowPanel_canvasPanel(instance).asWidget());
    templateFieldsMap.put("palettePanel", SessionPresenterView_FlowPanel_palettePanel(instance).asWidget());
    templateFieldsMap.put("sessionContainer", SessionPresenterView_SessionContainer_sessionContainer(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSessionPresenterView), templateFieldsMap.values());
    ((Widget) templateFieldsMap.get("sessionContainer")).addDomHandler(new ScrollHandler() {
      public void onScroll(ScrollEvent event) {
        SessionPresenterView_onScroll_ScrollEvent(instance, event);
      }
    }, ScrollEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SessionPresenterView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SessionPresenterView instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasFocusedSelectionEventSubscription", Subscription.class)).remove();
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final SessionPresenterView instance) {
    instance.init();
  }

  native static TranslationService SessionPresenterView_TranslationService_translationService(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::translationService;
  }-*/;

  native static void SessionPresenterView_TranslationService_translationService(SessionPresenterView instance, TranslationService value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::translationService = value;
  }-*/;

  native static Label SessionPresenterView_Label_loadingPanel(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::loadingPanel;
  }-*/;

  native static void SessionPresenterView_Label_loadingPanel(SessionPresenterView instance, Label value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::loadingPanel = value;
  }-*/;

  native static FlowPanel SessionPresenterView_FlowPanel_sessionHeaderContainer(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::sessionHeaderContainer;
  }-*/;

  native static void SessionPresenterView_FlowPanel_sessionHeaderContainer(SessionPresenterView instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::sessionHeaderContainer = value;
  }-*/;

  native static SessionContainer SessionPresenterView_SessionContainer_sessionContainer(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::sessionContainer;
  }-*/;

  native static void SessionPresenterView_SessionContainer_sessionContainer(SessionPresenterView instance, SessionContainer value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::sessionContainer = value;
  }-*/;

  native static FlowPanel SessionPresenterView_FlowPanel_palettePanel(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::palettePanel;
  }-*/;

  native static void SessionPresenterView_FlowPanel_palettePanel(SessionPresenterView instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::palettePanel = value;
  }-*/;

  native static ResizeFlowPanel SessionPresenterView_ResizeFlowPanel_canvasPanel(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::canvasPanel;
  }-*/;

  native static void SessionPresenterView_ResizeFlowPanel_canvasPanel(SessionPresenterView instance, ResizeFlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::canvasPanel = value;
  }-*/;

  native static FlowPanel SessionPresenterView_FlowPanel_toolbarPanel(SessionPresenterView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::toolbarPanel;
  }-*/;

  native static void SessionPresenterView_FlowPanel_toolbarPanel(SessionPresenterView instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::toolbarPanel = value;
  }-*/;

  public native static void SessionPresenterView_onScroll_ScrollEvent(SessionPresenterView instance, ScrollEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::onScroll(Lcom/google/gwt/event/dom/client/ScrollEvent;)(a0);
  }-*/;

  public native static void SessionPresenterView_onCanvasFocusedSelectionEvent_CanvasFocusedShapeEvent(SessionPresenterView instance, CanvasFocusedShapeEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPresenterView::onCanvasFocusedSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasFocusedShapeEvent;)(a0);
  }-*/;
}