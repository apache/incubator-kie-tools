package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.annotations.WorkbenchPopup.WorkbenchPopupSize;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PopupActivity;
import org.uberfire.client.views.pfly.notfound.ActivityNotFoundView;
import org.uberfire.client.views.pfly.popup.PopupViewImpl;
import org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter;
import org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter.View;
import org.uberfire.client.workbench.widgets.popup.PopupView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceType;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ActivityNotFoundPresenter> { private class Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_NamedProxyImpl extends ActivityNotFoundPresenter implements Proxy<ActivityNotFoundPresenter> {
    private final ProxyHelper<ActivityNotFoundPresenter> proxyHelper = new ProxyHelperImpl<ActivityNotFoundPresenter>("Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    public Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_NamedProxyImpl() {
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

    @Override public IsWidget getWidget() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getWidget();
        return retVal;
      } else {
        return super.getWidget();
      }
    }

    @Override public void init() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void onOpen() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onOpen();
      } else {
        super.onOpen();
      }
    }

    @Override public String getIdentifier() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getIdentifier();
        return retVal;
      } else {
        return super.getIdentifier();
      }
    }

    @Override public WorkbenchPopupSize getSize() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchPopupSize retVal = proxiedInstance.getSize();
        return retVal;
      } else {
        return super.getSize();
      }
    }

    @Override public void close() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.close();
      } else {
        super.close();
      }
    }

    @Override public ResourceType getResourceType() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final ResourceType retVal = proxiedInstance.getResourceType();
        return retVal;
      } else {
        return super.getResourceType();
      }
    }

    @Override public IsWidget getTitleDecoration() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getTitleDecoration();
        return retVal;
      } else {
        return super.getTitleDecoration();
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

    @Override public boolean onMayClose() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.onMayClose();
        return retVal;
      } else {
        return super.onMayClose();
      }
    }

    @Override public void onStartup(PlaceRequest place) {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onStartup(place);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void onShutdown() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onShutdown();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public PlaceRequest getPlace() {
      if (proxyHelper != null) {
        final ActivityNotFoundPresenter proxiedInstance = proxyHelper.getInstance(this);
        final PlaceRequest retVal = proxiedInstance.getPlace();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
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
  public Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(ActivityNotFoundPresenter.class, "Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_Named", ApplicationScoped.class, false, "uf.workbench.activity.notfound", true));
    handle.setAssignableTypes(new Class[] { ActivityNotFoundPresenter.class, AbstractPopupActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PopupActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("uf.workbench.activity.notfound") });
  }

  public ActivityNotFoundPresenter createInstance(final ContextManager contextManager) {
    final PopupView _popupView_1 = (PopupViewImpl) contextManager.getInstance("Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ActivityNotFoundPresenter instance = new ActivityNotFoundPresenter(_placeManager_0, _popupView_1);
    registerDependentScopedReference(instance, _popupView_1);
    setIncompleteInstance(instance);
    final PlaceManagerImpl ActivityNotFoundPresenter_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    ActivityNotFoundPresenter_PlaceManager_placeManager(instance, ActivityNotFoundPresenter_placeManager);
    final ActivityNotFoundView ActivityNotFoundPresenter_view = (ActivityNotFoundView) contextManager.getInstance("Type_factory__o_u_c_v_p_n_ActivityNotFoundView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ActivityNotFoundPresenter_view);
    ActivityNotFoundPresenter_View_view(instance, ActivityNotFoundPresenter_view);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ActivityNotFoundPresenter instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_w_w_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default_j_i_NamedProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter ([org.uberfire.client.mvp.PlaceManager, org.uberfire.client.workbench.widgets.popup.PopupView])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ActivityNotFoundPresenter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static View ActivityNotFoundPresenter_View_view(ActivityNotFoundPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter::view;
  }-*/;

  native static void ActivityNotFoundPresenter_View_view(ActivityNotFoundPresenter instance, View value) /*-{
    instance.@org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter::view = value;
  }-*/;

  native static PlaceManager ActivityNotFoundPresenter_PlaceManager_placeManager(ActivityNotFoundPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter::placeManager;
  }-*/;

  native static void ActivityNotFoundPresenter_PlaceManager_placeManager(ActivityNotFoundPresenter instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.widgets.notfound.ActivityNotFoundPresenter::placeManager = value;
  }-*/;
}