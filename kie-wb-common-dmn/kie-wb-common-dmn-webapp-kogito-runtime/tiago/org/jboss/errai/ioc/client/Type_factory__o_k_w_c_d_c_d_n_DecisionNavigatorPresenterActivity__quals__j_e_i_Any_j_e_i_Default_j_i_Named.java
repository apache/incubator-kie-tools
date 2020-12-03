package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenterActivity;
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

public class Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DecisionNavigatorPresenterActivity> { public Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DecisionNavigatorPresenterActivity.class, "Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "org.kie.dmn.decision.navigator", true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorPresenterActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("org.kie.dmn.decision.navigator") });
  }

  public DecisionNavigatorPresenterActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorPresenterActivity instance = new DecisionNavigatorPresenterActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DecisionNavigatorPresenter DecisionNavigatorPresenterActivity_realPresenter = (DecisionNavigatorPresenter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default");
    DecisionNavigatorPresenterActivity_DecisionNavigatorPresenter_realPresenter(instance, DecisionNavigatorPresenterActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static DecisionNavigatorPresenter DecisionNavigatorPresenterActivity_DecisionNavigatorPresenter_realPresenter(DecisionNavigatorPresenterActivity instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenterActivity::realPresenter;
  }-*/;

  native static void DecisionNavigatorPresenterActivity_DecisionNavigatorPresenter_realPresenter(DecisionNavigatorPresenterActivity instance, DecisionNavigatorPresenter value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenterActivity::realPresenter = value;
  }-*/;
}