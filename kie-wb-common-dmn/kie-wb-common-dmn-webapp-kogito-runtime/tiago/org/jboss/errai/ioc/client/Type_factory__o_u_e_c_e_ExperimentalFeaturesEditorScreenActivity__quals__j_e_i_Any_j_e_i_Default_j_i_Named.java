package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreen;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ExperimentalFeaturesEditorScreenActivity> { public Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(ExperimentalFeaturesEditorScreenActivity.class, "Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "ExperimentalFeaturesEditor", true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesEditorScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("ExperimentalFeaturesEditor") });
  }

  public ExperimentalFeaturesEditorScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeaturesEditorScreenActivity instance = new ExperimentalFeaturesEditorScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final ExperimentalFeaturesEditorScreen ExperimentalFeaturesEditorScreenActivity_realPresenter = (ExperimentalFeaturesEditorScreen) contextManager.getInstance("Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreen__quals__j_e_i_Any_j_e_i_Default");
    ExperimentalFeaturesEditorScreenActivity_ExperimentalFeaturesEditorScreen_realPresenter(instance, ExperimentalFeaturesEditorScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static ExperimentalFeaturesEditorScreen ExperimentalFeaturesEditorScreenActivity_ExperimentalFeaturesEditorScreen_realPresenter(ExperimentalFeaturesEditorScreenActivity instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenActivity::realPresenter;
  }-*/;

  native static void ExperimentalFeaturesEditorScreenActivity_ExperimentalFeaturesEditorScreen_realPresenter(ExperimentalFeaturesEditorScreenActivity instance, ExperimentalFeaturesEditorScreen value) /*-{
    instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenActivity::realPresenter = value;
  }-*/;
}