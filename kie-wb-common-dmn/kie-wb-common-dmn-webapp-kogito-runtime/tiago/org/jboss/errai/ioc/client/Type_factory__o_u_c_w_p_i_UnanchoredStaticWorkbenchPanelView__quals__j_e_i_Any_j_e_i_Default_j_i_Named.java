package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;

public class Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<UnanchoredStaticWorkbenchPanelView> { public Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(UnanchoredStaticWorkbenchPanelView.class, "Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "UnanchoredStaticWorkbenchPanelView", true));
    handle.setAssignableTypes(new Class[] { UnanchoredStaticWorkbenchPanelView.class, AbstractWorkbenchPanelView.class, ResizeComposite.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, WorkbenchPanelView.class, UberView.class, HasPresenter.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("UnanchoredStaticWorkbenchPanelView") });
  }

  public UnanchoredStaticWorkbenchPanelView createInstance(final ContextManager contextManager) {
    final UnanchoredStaticWorkbenchPanelView instance = new UnanchoredStaticWorkbenchPanelView();
    setIncompleteInstance(instance);
    final StaticFocusedResizePanel UnanchoredStaticWorkbenchPanelView_panel = (StaticFocusedResizePanel) contextManager.getInstance("ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, UnanchoredStaticWorkbenchPanelView_panel);
    UnanchoredStaticWorkbenchPanelView_StaticFocusedResizePanel_panel(instance, UnanchoredStaticWorkbenchPanelView_panel);
    final PanelManagerImpl AbstractWorkbenchPanelView_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    AbstractWorkbenchPanelView_PanelManager_panelManager(instance, AbstractWorkbenchPanelView_panelManager);
    final PlaceManagerImpl UnanchoredStaticWorkbenchPanelView_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    UnanchoredStaticWorkbenchPanelView_PlaceManager_placeManager(instance, UnanchoredStaticWorkbenchPanelView_placeManager);
    final LayoutSelection AbstractWorkbenchPanelView_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    AbstractWorkbenchPanelView_LayoutSelection_layoutSelection(instance, AbstractWorkbenchPanelView_layoutSelection);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UnanchoredStaticWorkbenchPanelView instance) {
    UnanchoredStaticWorkbenchPanelView_postConstruct(instance);
  }

  native static StaticFocusedResizePanel UnanchoredStaticWorkbenchPanelView_StaticFocusedResizePanel_panel(UnanchoredStaticWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView::panel;
  }-*/;

  native static void UnanchoredStaticWorkbenchPanelView_StaticFocusedResizePanel_panel(UnanchoredStaticWorkbenchPanelView instance, StaticFocusedResizePanel value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView::panel = value;
  }-*/;

  native static PanelManager AbstractWorkbenchPanelView_PanelManager_panelManager(AbstractWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::panelManager;
  }-*/;

  native static void AbstractWorkbenchPanelView_PanelManager_panelManager(AbstractWorkbenchPanelView instance, PanelManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::panelManager = value;
  }-*/;

  native static LayoutSelection AbstractWorkbenchPanelView_LayoutSelection_layoutSelection(AbstractWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::layoutSelection;
  }-*/;

  native static void AbstractWorkbenchPanelView_LayoutSelection_layoutSelection(AbstractWorkbenchPanelView instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::layoutSelection = value;
  }-*/;

  native static PlaceManager UnanchoredStaticWorkbenchPanelView_PlaceManager_placeManager(UnanchoredStaticWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView::placeManager;
  }-*/;

  native static void UnanchoredStaticWorkbenchPanelView_PlaceManager_placeManager(UnanchoredStaticWorkbenchPanelView instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView::placeManager = value;
  }-*/;

  public native static void UnanchoredStaticWorkbenchPanelView_postConstruct(UnanchoredStaticWorkbenchPanelView instance) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView::postConstruct()();
  }-*/;
}