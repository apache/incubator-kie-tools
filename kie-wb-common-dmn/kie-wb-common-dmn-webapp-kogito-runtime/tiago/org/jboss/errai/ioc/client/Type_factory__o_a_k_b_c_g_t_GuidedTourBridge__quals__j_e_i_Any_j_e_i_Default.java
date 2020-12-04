package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.appformer.kogito.bridge.client.guided.tour.observers.GlobalHTMLObserver;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourBridge> { private class Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends GuidedTourBridge implements Proxy<GuidedTourBridge> {
    private final ProxyHelper<GuidedTourBridge> proxyHelper = new ProxyHelperImpl<GuidedTourBridge>("Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final GuidedTourBridge instance) {

    }

    public GuidedTourBridge asBeanType() {
      return this;
    }

    public void setInstance(final GuidedTourBridge instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final GuidedTourBridge proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public void refresh(UserInteraction userInteraction) {
      if (proxyHelper != null) {
        final GuidedTourBridge proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refresh(userInteraction);
      } else {
        super.refresh(userInteraction);
      }
    }

    @Override public void registerTutorial(Tutorial tutorial) {
      if (proxyHelper != null) {
        final GuidedTourBridge proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerTutorial(tutorial);
      } else {
        super.registerTutorial(tutorial);
      }
    }

    @Override public void registerObserver(GuidedTourObserver observer) {
      if (proxyHelper != null) {
        final GuidedTourBridge proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerObserver(observer);
      } else {
        super.registerObserver(observer);
      }
    }

    @Override public void registerPositionProvider(String type, PositionProviderFunction positionProviderFunction) {
      if (proxyHelper != null) {
        final GuidedTourBridge proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerPositionProvider(type, positionProviderFunction);
      } else {
        super.registerPositionProvider(type, positionProviderFunction);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final GuidedTourBridge proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourBridge.class, "Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourBridge.class, Object.class });
  }

  public GuidedTourBridge createInstance(final ContextManager contextManager) {
    final GlobalHTMLObserver _globalHTMLObserver_1 = (GlobalHTMLObserver) contextManager.getInstance("Type_factory__o_a_k_b_c_g_t_o_GlobalHTMLObserver__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourService _guidedTourService_0 = (GuidedTourService) contextManager.getInstance("Producer_factory__o_a_k_b_c_g_t_s_GuidedTourService__quals__j_e_i_Any_j_e_i_Default");
    final GuidedTourBridge instance = new GuidedTourBridge(_guidedTourService_0, _globalHTMLObserver_1);
    registerDependentScopedReference(instance, _globalHTMLObserver_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final GuidedTourBridge instance) {
    instance.init();
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_a_k_b_c_g_t_GuidedTourBridge__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge an exception was thrown from this constructor: @javax.inject.Inject()  public org.appformer.kogito.bridge.client.guided.tour.GuidedTourBridge ([org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService, org.appformer.kogito.bridge.client.guided.tour.observers.GlobalHTMLObserver])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<GuidedTourBridge> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}