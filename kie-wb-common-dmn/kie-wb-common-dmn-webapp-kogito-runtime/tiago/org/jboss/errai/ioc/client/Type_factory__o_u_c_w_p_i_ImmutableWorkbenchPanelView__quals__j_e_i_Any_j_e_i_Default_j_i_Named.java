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
import org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;

public class Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ImmutableWorkbenchPanelView> { public Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(ImmutableWorkbenchPanelView.class, "Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "ImmutableWorkbenchPanelView", true));
    handle.setAssignableTypes(new Class[] { ImmutableWorkbenchPanelView.class, AbstractWorkbenchPanelView.class, ResizeComposite.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, RequiresResize.class, WorkbenchPanelView.class, UberView.class, HasPresenter.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("ImmutableWorkbenchPanelView") });
  }

  public ImmutableWorkbenchPanelView createInstance(final ContextManager contextManager) {
    final ImmutableWorkbenchPanelView instance = new ImmutableWorkbenchPanelView();
    setIncompleteInstance(instance);
    final StaticFocusedResizePanel ImmutableWorkbenchPanelView_panel = (StaticFocusedResizePanel) contextManager.getInstance("ExtensionProvided_factory__o_u_c_w_w_p_StaticFocusedResizePanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ImmutableWorkbenchPanelView_panel);
    ImmutableWorkbenchPanelView_StaticFocusedResizePanel_panel(instance, ImmutableWorkbenchPanelView_panel);
    final LayoutSelection AbstractWorkbenchPanelView_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    AbstractWorkbenchPanelView_LayoutSelection_layoutSelection(instance, AbstractWorkbenchPanelView_layoutSelection);
    final PanelManagerImpl AbstractWorkbenchPanelView_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    AbstractWorkbenchPanelView_PanelManager_panelManager(instance, AbstractWorkbenchPanelView_panelManager);
    final PlaceManagerImpl ImmutableWorkbenchPanelView_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ImmutableWorkbenchPanelView_PlaceManager_placeManager(instance, ImmutableWorkbenchPanelView_placeManager);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ImmutableWorkbenchPanelView instance) {
    ImmutableWorkbenchPanelView_postConstruct(instance);
  }

  native static PanelManager AbstractWorkbenchPanelView_PanelManager_panelManager(AbstractWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::panelManager;
  }-*/;

  native static void AbstractWorkbenchPanelView_PanelManager_panelManager(AbstractWorkbenchPanelView instance, PanelManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::panelManager = value;
  }-*/;

  native static StaticFocusedResizePanel ImmutableWorkbenchPanelView_StaticFocusedResizePanel_panel(ImmutableWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView::panel;
  }-*/;

  native static void ImmutableWorkbenchPanelView_StaticFocusedResizePanel_panel(ImmutableWorkbenchPanelView instance, StaticFocusedResizePanel value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView::panel = value;
  }-*/;

  native static LayoutSelection AbstractWorkbenchPanelView_LayoutSelection_layoutSelection(AbstractWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::layoutSelection;
  }-*/;

  native static void AbstractWorkbenchPanelView_LayoutSelection_layoutSelection(AbstractWorkbenchPanelView instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelView::layoutSelection = value;
  }-*/;

  native static PlaceManager ImmutableWorkbenchPanelView_PlaceManager_placeManager(ImmutableWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView::placeManager;
  }-*/;

  native static void ImmutableWorkbenchPanelView_PlaceManager_placeManager(ImmutableWorkbenchPanelView instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView::placeManager = value;
  }-*/;

  public native static void ImmutableWorkbenchPanelView_postConstruct(ImmutableWorkbenchPanelView instance) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView::postConstruct()();
  }-*/;
}