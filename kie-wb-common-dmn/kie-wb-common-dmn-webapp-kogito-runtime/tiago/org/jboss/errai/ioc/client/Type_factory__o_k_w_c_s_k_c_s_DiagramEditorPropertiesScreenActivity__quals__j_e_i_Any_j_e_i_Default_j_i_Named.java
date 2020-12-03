package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreen;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreenActivity;
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

public class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DiagramEditorPropertiesScreenActivity> { public Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DiagramEditorPropertiesScreenActivity.class, "Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "DiagramEditorPropertiesScreen", true));
    handle.setAssignableTypes(new Class[] { DiagramEditorPropertiesScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("DiagramEditorPropertiesScreen") });
  }

  public DiagramEditorPropertiesScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorPropertiesScreenActivity instance = new DiagramEditorPropertiesScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DiagramEditorPropertiesScreen DiagramEditorPropertiesScreenActivity_realPresenter = (DiagramEditorPropertiesScreen) contextManager.getInstance("Type_factory__o_k_w_c_s_k_c_s_DiagramEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DiagramEditorPropertiesScreenActivity_realPresenter);
    DiagramEditorPropertiesScreenActivity_DiagramEditorPropertiesScreen_realPresenter(instance, DiagramEditorPropertiesScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static DiagramEditorPropertiesScreen DiagramEditorPropertiesScreenActivity_DiagramEditorPropertiesScreen_realPresenter(DiagramEditorPropertiesScreenActivity instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreenActivity::realPresenter;
  }-*/;

  native static void DiagramEditorPropertiesScreenActivity_DiagramEditorPropertiesScreen_realPresenter(DiagramEditorPropertiesScreenActivity instance, DiagramEditorPropertiesScreen value) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorPropertiesScreenActivity::realPresenter = value;
  }-*/;
}