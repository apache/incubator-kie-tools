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
import org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelView;

public class Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<TemplatedWorkbenchPanelView> { public Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(TemplatedWorkbenchPanelView.class, "Type_factory__o_u_c_w_p_i_TemplatedWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "TemplatedWorkbenchPanelView", true));
    handle.setAssignableTypes(new Class[] { TemplatedWorkbenchPanelView.class, Object.class, WorkbenchPanelView.class, UberView.class, IsWidget.class, HasPresenter.class, RequiresResize.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("TemplatedWorkbenchPanelView") });
  }

  public TemplatedWorkbenchPanelView createInstance(final ContextManager contextManager) {
    final TemplatedWorkbenchPanelView instance = new TemplatedWorkbenchPanelView();
    setIncompleteInstance(instance);
    final LayoutSelection TemplatedWorkbenchPanelView_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    TemplatedWorkbenchPanelView_LayoutSelection_layoutSelection(instance, TemplatedWorkbenchPanelView_layoutSelection);
    setIncompleteInstance(null);
    return instance;
  }

  native static LayoutSelection TemplatedWorkbenchPanelView_LayoutSelection_layoutSelection(TemplatedWorkbenchPanelView instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelView::layoutSelection;
  }-*/;

  native static void TemplatedWorkbenchPanelView_LayoutSelection_layoutSelection(TemplatedWorkbenchPanelView instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.TemplatedWorkbenchPanelView::layoutSelection = value;
  }-*/;
}