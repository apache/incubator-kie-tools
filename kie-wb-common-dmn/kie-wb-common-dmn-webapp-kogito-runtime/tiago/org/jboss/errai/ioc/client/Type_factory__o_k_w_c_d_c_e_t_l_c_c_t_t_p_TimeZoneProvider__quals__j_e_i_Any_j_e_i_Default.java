package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.function.Consumer;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.DMNClientServicesProxyImpl;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeZoneProvider> { private class Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends TimeZoneProvider implements Proxy<TimeZoneProvider> {
    private final ProxyHelper<TimeZoneProvider> proxyHelper = new ProxyHelperImpl<TimeZoneProvider>("Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final TimeZoneProvider instance) {

    }

    public TimeZoneProvider asBeanType() {
      return this;
    }

    public void setInstance(final TimeZoneProvider instance) {
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

    @Override public void getTimeZones(Consumer consumer) {
      if (proxyHelper != null) {
        final TimeZoneProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.getTimeZones(consumer);
      } else {
        super.getTimeZones(consumer);
      }
    }

    @Override public boolean isTimeZone(String timeZone) {
      if (proxyHelper != null) {
        final TimeZoneProvider proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isTimeZone(timeZone);
        return retVal;
      } else {
        return super.isTimeZone(timeZone);
      }
    }

    @Override public List getTimeZonesOffsets() {
      if (proxyHelper != null) {
        final TimeZoneProvider proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getTimeZonesOffsets();
        return retVal;
      } else {
        return super.getTimeZonesOffsets();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final TimeZoneProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimeZoneProvider.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimeZoneProvider.class, Object.class });
  }

  public TimeZoneProvider createInstance(final ContextManager contextManager) {
    final DMNClientServicesProxy _clientServicesProxy_0 = (DMNClientServicesProxyImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNClientServicesProxyImpl__quals__j_e_i_Any_j_e_i_Default");
    final TimeZoneProvider instance = new TimeZoneProvider(_clientServicesProxy_0);
    registerDependentScopedReference(instance, _clientServicesProxy_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_l_c_c_t_t_p_TimeZoneProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider ([org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<TimeZoneProvider> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}