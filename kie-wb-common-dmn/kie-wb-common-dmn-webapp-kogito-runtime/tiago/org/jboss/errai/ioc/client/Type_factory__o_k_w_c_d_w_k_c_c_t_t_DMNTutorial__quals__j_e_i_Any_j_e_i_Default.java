package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial.View;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorialView;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNTutorial> { private class Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNTutorial implements Proxy<DMNTutorial> {
    private final ProxyHelper<DMNTutorial> proxyHelper = new ProxyHelperImpl<DMNTutorial>("Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DMNTutorial instance) {

    }

    public DMNTutorial asBeanType() {
      return this;
    }

    public void setInstance(final DMNTutorial instance) {
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

    @Override public Tutorial getTutorial() {
      if (proxyHelper != null) {
        final DMNTutorial proxiedInstance = proxyHelper.getInstance(this);
        final Tutorial retVal = proxiedInstance.getTutorial();
        return retVal;
      } else {
        return super.getTutorial();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNTutorial proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNTutorial.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNTutorial.class, Object.class });
  }

  public DMNTutorial createInstance(final ContextManager contextManager) {
    final View _view_0 = (DMNTutorialView) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorialView__quals__j_e_i_Any_j_e_i_Default");
    final DMNTutorial instance = new DMNTutorial(_view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNTutorial instance) {
    DMNTutorial_setup(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_w_k_c_c_t_t_DMNTutorial__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial ([org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial$View])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNTutorial> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DMNTutorial_setup(DMNTutorial instance) /*-{
    instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.tutorial.DMNTutorial::setup()();
  }-*/;
}