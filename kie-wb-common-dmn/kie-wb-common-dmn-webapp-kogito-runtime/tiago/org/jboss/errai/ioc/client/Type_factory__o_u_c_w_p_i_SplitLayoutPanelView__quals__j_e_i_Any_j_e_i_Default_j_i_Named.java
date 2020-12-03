package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
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
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.SplitLayoutPanelView;

public class Type_factory__o_u_c_w_p_i_SplitLayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<SplitLayoutPanelView> { public Type_factory__o_u_c_w_p_i_SplitLayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(SplitLayoutPanelView.class, "Type_factory__o_u_c_w_p_i_SplitLayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "SplitLayoutPanelView", true));
    handle.setAssignableTypes(new Class[] { SplitLayoutPanelView.class, Object.class, WorkbenchPanelView.class, UberView.class, IsWidget.class, HasPresenter.class, RequiresResize.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("SplitLayoutPanelView") });
  }

  public SplitLayoutPanelView createInstance(final ContextManager contextManager) {
    final SplitLayoutPanelView instance = new SplitLayoutPanelView();
    setIncompleteInstance(instance);
    final PlaceManagerImpl SplitLayoutPanelView_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    SplitLayoutPanelView_PlaceManager_placeManager(instance, SplitLayoutPanelView_placeManager);
    final LayoutSelection SplitLayoutPanelView_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    SplitLayoutPanelView_LayoutSelection_layoutSelection(instance, SplitLayoutPanelView_layoutSelection);
    setIncompleteInstance(null);
    return instance;
  }

  native static PlaceManager SplitLayoutPanelView_PlaceManager_placeManager(SplitLayoutPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.SplitLayoutPanelView::placeManager;
  }-*/;

  native static void SplitLayoutPanelView_PlaceManager_placeManager(SplitLayoutPanelView instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.SplitLayoutPanelView::placeManager = value;
  }-*/;

  native static LayoutSelection SplitLayoutPanelView_LayoutSelection_layoutSelection(SplitLayoutPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.SplitLayoutPanelView::layoutSelection;
  }-*/;

  native static void SplitLayoutPanelView_LayoutSelection_layoutSelection(SplitLayoutPanelView instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.SplitLayoutPanelView::layoutSelection = value;
  }-*/;
}