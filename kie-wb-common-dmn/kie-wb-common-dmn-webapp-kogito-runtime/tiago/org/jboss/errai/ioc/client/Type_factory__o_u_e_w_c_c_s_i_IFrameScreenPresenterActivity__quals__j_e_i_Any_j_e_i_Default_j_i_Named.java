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
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenter;
import org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenterActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<IFrameScreenPresenterActivity> { public Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(IFrameScreenPresenterActivity.class, "Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "IFrameScreen", true));
    handle.setAssignableTypes(new Class[] { IFrameScreenPresenterActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("IFrameScreen") });
  }

  public IFrameScreenPresenterActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final IFrameScreenPresenterActivity instance = new IFrameScreenPresenterActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final IFrameScreenPresenter IFrameScreenPresenterActivity_realPresenter = (IFrameScreenPresenter) contextManager.getInstance("Type_factory__o_u_e_w_c_c_s_i_IFrameScreenPresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, IFrameScreenPresenterActivity_realPresenter);
    IFrameScreenPresenterActivity_IFrameScreenPresenter_realPresenter(instance, IFrameScreenPresenterActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static IFrameScreenPresenter IFrameScreenPresenterActivity_IFrameScreenPresenter_realPresenter(IFrameScreenPresenterActivity instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenterActivity::realPresenter;
  }-*/;

  native static void IFrameScreenPresenterActivity_IFrameScreenPresenter_realPresenter(IFrameScreenPresenterActivity instance, IFrameScreenPresenter value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.screens.iframe.IFrameScreenPresenterActivity::realPresenter = value;
  }-*/;
}