package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceView;
import org.kie.workbench.common.widgets.client.handlers.NewResourceViewImpl;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

public class Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<NewResourcePresenter> { private class Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends NewResourcePresenter implements Proxy<NewResourcePresenter> {
    private final ProxyHelper<NewResourcePresenter> proxyHelper = new ProxyHelperImpl<NewResourcePresenter>("Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final NewResourcePresenter instance) {

    }

    public NewResourcePresenter asBeanType() {
      return this;
    }

    public void setInstance(final NewResourcePresenter instance) {
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

    @Override public void show(NewResourceHandler handler) {
      if (proxyHelper != null) {
        final NewResourcePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(handler);
      } else {
        super.show(handler);
      }
    }

    @Override public void validate(String fileName, ValidatorWithReasonCallback callback) {
      if (proxyHelper != null) {
        final NewResourcePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.validate(fileName, callback);
      } else {
        super.validate(fileName, callback);
      }
    }

    @Override public void makeItem(String fileName) {
      if (proxyHelper != null) {
        final NewResourcePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.makeItem(fileName);
      } else {
        super.makeItem(fileName);
      }
    }

    @Override public void complete() {
      if (proxyHelper != null) {
        final NewResourcePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.complete();
      } else {
        super.complete();
      }
    }

    @Override public void setResourceName(String resourceName) {
      if (proxyHelper != null) {
        final NewResourcePresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setResourceName(resourceName);
      } else {
        super.setResourceName(resourceName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final NewResourcePresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NewResourcePresenter.class, "Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NewResourcePresenter.class, Object.class });
  }

  public NewResourcePresenter createInstance(final ContextManager contextManager) {
    final NewResourceView _view_0 = (NewResourceViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_NewResourceViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final NewResourcePresenter instance = new NewResourcePresenter(_view_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final NewResourcePresenter instance) {
    NewResourcePresenter_setup(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_w_c_h_NewResourcePresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter ([org.kie.workbench.common.widgets.client.handlers.NewResourceView, org.jboss.errai.ui.client.local.spi.TranslationService])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<NewResourcePresenter> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void NewResourcePresenter_setup(NewResourcePresenter instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter::setup()();
  }-*/;
}