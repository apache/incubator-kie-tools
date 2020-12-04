package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.LayoutPanelView;
import org.uberfire.client.workbench.panels.support.PartManager;

public class Type_factory__o_u_c_w_p_i_LayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<LayoutPanelView> { public Type_factory__o_u_c_w_p_i_LayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(LayoutPanelView.class, "Type_factory__o_u_c_w_p_i_LayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "LayoutPanelView", true));
    handle.setAssignableTypes(new Class[] { LayoutPanelView.class, Object.class, WorkbenchPanelView.class, UberView.class, IsWidget.class, HasPresenter.class, RequiresResize.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("LayoutPanelView") });
  }

  public LayoutPanelView createInstance(final ContextManager contextManager) {
    final LayoutPanelView instance = new LayoutPanelView();
    setIncompleteInstance(instance);
    final LayoutSelection LayoutPanelView_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    LayoutPanelView_LayoutSelection_layoutSelection(instance, LayoutPanelView_layoutSelection);
    final PartManager LayoutPanelView_partManager = (PartManager) contextManager.getInstance("Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default");
    LayoutPanelView_PartManager_partManager(instance, LayoutPanelView_partManager);
    setIncompleteInstance(null);
    return instance;
  }

  native static PartManager LayoutPanelView_PartManager_partManager(LayoutPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.LayoutPanelView::partManager;
  }-*/;

  native static void LayoutPanelView_PartManager_partManager(LayoutPanelView instance, PartManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.LayoutPanelView::partManager = value;
  }-*/;

  native static LayoutSelection LayoutPanelView_LayoutSelection_layoutSelection(LayoutPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.LayoutPanelView::layoutSelection;
  }-*/;

  native static void LayoutPanelView_LayoutSelection_layoutSelection(LayoutPanelView instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.LayoutPanelView::layoutSelection = value;
  }-*/;
}