package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenActivity;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreviewDiagramScreenActivity> { public Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(PreviewDiagramScreenActivity.class, "Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "DMNProjectDiagramExplorerScreen", true));
    handle.setAssignableTypes(new Class[] { PreviewDiagramScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("DMNProjectDiagramExplorerScreen") });
  }

  public PreviewDiagramScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PreviewDiagramScreenActivity instance = new PreviewDiagramScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final PreviewDiagramScreen PreviewDiagramScreenActivity_realPresenter = (PreviewDiagramScreen) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreen__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PreviewDiagramScreenActivity_realPresenter);
    PreviewDiagramScreenActivity_PreviewDiagramScreen_realPresenter(instance, PreviewDiagramScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static PreviewDiagramScreen PreviewDiagramScreenActivity_PreviewDiagramScreen_realPresenter(PreviewDiagramScreenActivity instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenActivity::realPresenter;
  }-*/;

  native static void PreviewDiagramScreenActivity_PreviewDiagramScreen_realPresenter(PreviewDiagramScreenActivity instance, PreviewDiagramScreen value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenActivity::realPresenter = value;
  }-*/;
}