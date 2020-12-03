package org.jboss.errai.ioc.client;

import elemental2.promise.Promise;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService;
import org.uberfire.client.promise.Promises;

public class Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoResourceContentService> { private class Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends KogitoResourceContentService implements Proxy<KogitoResourceContentService> {
    private final ProxyHelper<KogitoResourceContentService> proxyHelper = new ProxyHelperImpl<KogitoResourceContentService>("Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final KogitoResourceContentService instance) {

    }

    public KogitoResourceContentService asBeanType() {
      return this;
    }

    public void setInstance(final KogitoResourceContentService instance) {
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

    @Override public void loadFile(String fileUri, RemoteCallback callback, ErrorCallback errorCallback) {
      if (proxyHelper != null) {
        final KogitoResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadFile(fileUri, callback, errorCallback);
      } else {
        super.loadFile(fileUri, callback, errorCallback);
      }
    }

    @Override public Promise loadFile(String fileUri) {
      if (proxyHelper != null) {
        final KogitoResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.loadFile(fileUri);
        return retVal;
      } else {
        return super.loadFile(fileUri);
      }
    }

    @Override public void getAllItems(RemoteCallback callback, ErrorCallback errorCallback) {
      if (proxyHelper != null) {
        final KogitoResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getAllItems(callback, errorCallback);
      } else {
        super.getAllItems(callback, errorCallback);
      }
    }

    @Override public void getFilteredItems(String pattern, RemoteCallback callback, ErrorCallback errorCallback) {
      if (proxyHelper != null) {
        final KogitoResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getFilteredItems(pattern, callback, errorCallback);
      } else {
        super.getFilteredItems(pattern, callback, errorCallback);
      }
    }

    @Override public Promise getFilteredItems(String pattern, ResourceListOptions options) {
      if (proxyHelper != null) {
        final KogitoResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final Promise retVal = proxiedInstance.getFilteredItems(pattern, options);
        return retVal;
      } else {
        return super.getFilteredItems(pattern, options);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final KogitoResourceContentService proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoResourceContentService.class, "Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoResourceContentService.class, Object.class });
  }

  public KogitoResourceContentService createInstance(final ContextManager contextManager) {
    final Promises _promises_1 = (Promises) contextManager.getInstance("Type_factory__o_u_c_p_Promises__quals__j_e_i_Any_j_e_i_Default");
    final ResourceContentService _resourceContentService_0 = (ResourceContentService) contextManager.getInstance("Producer_factory__o_a_k_b_c_r_ResourceContentService__quals__j_e_i_Any_j_e_i_Default");
    final KogitoResourceContentService instance = new KogitoResourceContentService(_resourceContentService_0, _promises_1);
    registerDependentScopedReference(instance, _promises_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_k_w_b_c_w_KogitoResourceContentService__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService ([org.appformer.kogito.bridge.client.resource.ResourceContentService, org.uberfire.client.promise.Promises])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<KogitoResourceContentService> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}