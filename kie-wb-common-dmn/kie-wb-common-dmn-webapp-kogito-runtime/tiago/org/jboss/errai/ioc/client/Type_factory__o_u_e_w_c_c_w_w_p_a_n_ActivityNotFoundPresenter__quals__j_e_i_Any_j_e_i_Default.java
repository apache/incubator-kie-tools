package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenter;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView;
import org.uberfire.mvp.PlaceRequest;

public class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ActivityNotFoundPresenter> { private class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ActivityNotFoundPresenter implements Proxy<ActivityNotFoundPresenter> {
    private final ProxyHelper<ActivityNotFoundPresenter> proxyHelper = new ProxyHelperImpl<ActivityNotFoundPresenter>("Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ActivityNotFoundPresenter instance) {

    }

    public ActivityNotFoundPresenter asBeanType() {
      return this;
    }

    public void setInstance(final ActivityNotFoundPresenter instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public void onStartup(PlaceRequest place) {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onStartup(place);
      } else {
        super.onStartup(place);
      }
    }

    @Override public void onClose() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onClose();
      } else {
        super.onClose();
      }
    }

    @Override public UberView getView() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final UberView retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ActivityNotFoundPresenter.class, "Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ActivityNotFoundPresenter.class, Object.class });
  }

  public ActivityNotFoundPresenter createInstance(final ContextManager contextManager) {
    final ActivityNotFoundView _view_0 = (ActivityNotFoundView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_1 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ActivityNotFoundPresenter instance = new ActivityNotFoundPresenter(_view_0, _placeManager_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenter an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenter ([org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundView, org.uberfire.client.mvp.PlaceManager])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ActivityNotFoundPresenter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}