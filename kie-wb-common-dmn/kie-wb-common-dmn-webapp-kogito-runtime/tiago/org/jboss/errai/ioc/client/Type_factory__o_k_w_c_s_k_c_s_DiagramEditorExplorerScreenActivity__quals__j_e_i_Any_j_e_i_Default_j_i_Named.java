package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenActivity;
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

public class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DiagramEditorExplorerScreenActivity> { public Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DiagramEditorExplorerScreenActivity.class, "Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "ProjectDiagramExplorerScreen", true));
    handle.setAssignableTypes(new Class[] { DiagramEditorExplorerScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("ProjectDiagramExplorerScreen") });
  }

  public DiagramEditorExplorerScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorExplorerScreenActivity instance = new DiagramEditorExplorerScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DiagramEditorExplorerScreen DiagramEditorExplorerScreenActivity_realPresenter = (DiagramEditorExplorerScreen) contextManager.getInstance("Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreen__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DiagramEditorExplorerScreenActivity_realPresenter);
    DiagramEditorExplorerScreenActivity_DiagramEditorExplorerScreen_realPresenter(instance, DiagramEditorExplorerScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static DiagramEditorExplorerScreen DiagramEditorExplorerScreenActivity_DiagramEditorExplorerScreen_realPresenter(DiagramEditorExplorerScreenActivity instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenActivity::realPresenter;
  }-*/;

  native static void DiagramEditorExplorerScreenActivity_DiagramEditorExplorerScreen_realPresenter(DiagramEditorExplorerScreenActivity instance, DiagramEditorExplorerScreen value) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenActivity::realPresenter = value;
  }-*/;
}