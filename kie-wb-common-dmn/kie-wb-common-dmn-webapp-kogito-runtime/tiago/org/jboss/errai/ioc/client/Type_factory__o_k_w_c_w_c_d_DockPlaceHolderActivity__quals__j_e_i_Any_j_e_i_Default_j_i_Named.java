package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolder;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderActivity;
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

public class Type_factory__o_k_w_c_w_c_d_DockPlaceHolderActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DockPlaceHolderActivity> { public Type_factory__o_k_w_c_w_c_d_DockPlaceHolderActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DockPlaceHolderActivity.class, "Type_factory__o_k_w_c_w_c_d_DockPlaceHolderActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "org.docks.PlaceHolder", true));
    handle.setAssignableTypes(new Class[] { DockPlaceHolderActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("org.docks.PlaceHolder") });
  }

  public DockPlaceHolderActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DockPlaceHolderActivity instance = new DockPlaceHolderActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DockPlaceHolder DockPlaceHolderActivity_realPresenter = (DockPlaceHolder) contextManager.getInstance("Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default");
    DockPlaceHolderActivity_DockPlaceHolder_realPresenter(instance, DockPlaceHolderActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static DockPlaceHolder DockPlaceHolderActivity_DockPlaceHolder_realPresenter(DockPlaceHolderActivity instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.docks.DockPlaceHolderActivity::realPresenter;
  }-*/;

  native static void DockPlaceHolderActivity_DockPlaceHolder_realPresenter(DockPlaceHolderActivity instance, DockPlaceHolder value) /*-{
    instance.@org.kie.workbench.common.widgets.client.docks.DockPlaceHolderActivity::realPresenter = value;
  }-*/;
}