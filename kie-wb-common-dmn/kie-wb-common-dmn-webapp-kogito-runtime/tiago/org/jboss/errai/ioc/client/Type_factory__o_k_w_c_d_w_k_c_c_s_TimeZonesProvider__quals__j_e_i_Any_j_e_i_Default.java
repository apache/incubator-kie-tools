package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.services.TimeZonesProvider;

public class Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<TimeZonesProvider> { private class Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends TimeZonesProvider implements Proxy<TimeZonesProvider> {
    private final ProxyHelper<TimeZonesProvider> proxyHelper = new ProxyHelperImpl<TimeZonesProvider>("Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final TimeZonesProvider instance) {

    }

    public TimeZonesProvider asBeanType() {
      return this;
    }

    public void setInstance(final TimeZonesProvider instance) {
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

    @Override public List getTimeZones() {
      if (proxyHelper != null) {
        final TimeZonesProvider proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getTimeZones();
        return retVal;
      } else {
        return super.getTimeZones();
      }
    }

    @Override protected String[] getNames() {
      if (proxyHelper != null) {
        final TimeZonesProvider proxiedInstance = proxyHelper.getInstance(this);
        final String[] retVal = TimeZonesProvider_getNames(proxiedInstance);
        return retVal;
      } else {
        return super.getNames();
      }
    }

    @Override protected double getOffset(String timeZoneName) {
      if (proxyHelper != null) {
        final TimeZonesProvider proxiedInstance = proxyHelper.getInstance(this);
        final double retVal = TimeZonesProvider_getOffset_String(proxiedInstance, timeZoneName);
        return retVal;
      } else {
        return super.getOffset(timeZoneName);
      }
    }

    @Override protected String getOffsetString(String timeZoneName) {
      if (proxyHelper != null) {
        final TimeZonesProvider proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = TimeZonesProvider_getOffsetString_String(proxiedInstance, timeZoneName);
        return retVal;
      } else {
        return super.getOffsetString(timeZoneName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final TimeZonesProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TimeZonesProvider.class, "Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TimeZonesProvider.class, Object.class });
  }

  public TimeZonesProvider createInstance(final ContextManager contextManager) {
    final TimeZonesProvider instance = new TimeZonesProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<TimeZonesProvider> proxyImpl = new Type_factory__o_k_w_c_d_w_k_c_c_s_TimeZonesProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static String TimeZonesProvider_getOffsetString_String(TimeZonesProvider instance, String a0) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.services.TimeZonesProvider::getOffsetString(Ljava/lang/String;)(a0);
  }-*/;

  public native static double TimeZonesProvider_getOffset_String(TimeZonesProvider instance, String a0) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.services.TimeZonesProvider::getOffset(Ljava/lang/String;)(a0);
  }-*/;

  public native static String[] TimeZonesProvider_getNames(TimeZonesProvider instance) /*-{
    return instance.@org.kie.workbench.common.dmn.webapp.kogito.common.client.services.TimeZonesProvider::getNames()();
  }-*/;
}