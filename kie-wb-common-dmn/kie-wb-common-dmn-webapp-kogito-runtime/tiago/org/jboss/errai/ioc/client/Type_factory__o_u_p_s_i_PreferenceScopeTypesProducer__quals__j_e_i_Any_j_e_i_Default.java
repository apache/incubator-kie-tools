package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer;
import org.uberfire.rpc.SessionInfo;

public class Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferenceScopeTypesProducer> { private class Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PreferenceScopeTypesProducer implements Proxy<PreferenceScopeTypesProducer> {
    private final ProxyHelper<PreferenceScopeTypesProducer> proxyHelper = new ProxyHelperImpl<PreferenceScopeTypesProducer>("Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PreferenceScopeTypesProducer instance) {

    }

    public PreferenceScopeTypesProducer asBeanType() {
      return this;
    }

    public void setInstance(final PreferenceScopeTypesProducer instance) {
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

    @Override public PreferenceScopeTypes preferenceScopeTypesProducer() {
      if (proxyHelper != null) {
        final PreferenceScopeTypesProducer proxiedInstance = proxyHelper.getInstance(this);
        final PreferenceScopeTypes retVal = proxiedInstance.preferenceScopeTypesProducer();
        return retVal;
      } else {
        return super.preferenceScopeTypesProducer();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PreferenceScopeTypesProducer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferenceScopeTypesProducer.class, "Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferenceScopeTypesProducer.class, Object.class });
  }

  public PreferenceScopeTypesProducer createInstance(final ContextManager contextManager) {
    final PreferenceScopeTypesProducer instance = new PreferenceScopeTypesProducer();
    setIncompleteInstance(instance);
    final Instance PreferenceScopeTypesProducer_preferenceScopeTypes = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { PreferenceScopeTypes.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PreferenceScopeTypesProducer_preferenceScopeTypes);
    PreferenceScopeTypesProducer_Instance_preferenceScopeTypes(instance, PreferenceScopeTypesProducer_preferenceScopeTypes);
    final SessionInfo PreferenceScopeTypesProducer_sessionInfo = (SessionInfo) contextManager.getInstance("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    PreferenceScopeTypesProducer_SessionInfo_sessionInfo(instance, PreferenceScopeTypesProducer_sessionInfo);
    final DefaultPreferenceScopeTypes PreferenceScopeTypesProducer_defaultPreferenceScopeTypes = (DefaultPreferenceScopeTypes) contextManager.getInstance("Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation");
    PreferenceScopeTypesProducer_PreferenceScopeTypes_defaultPreferenceScopeTypes(instance, PreferenceScopeTypesProducer_defaultPreferenceScopeTypes);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PreferenceScopeTypesProducer> proxyImpl = new Type_factory__o_u_p_s_i_PreferenceScopeTypesProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Instance PreferenceScopeTypesProducer_Instance_preferenceScopeTypes(PreferenceScopeTypesProducer instance) /*-{
    return instance.@org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer::preferenceScopeTypes;
  }-*/;

  native static void PreferenceScopeTypesProducer_Instance_preferenceScopeTypes(PreferenceScopeTypesProducer instance, Instance<PreferenceScopeTypes> value) /*-{
    instance.@org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer::preferenceScopeTypes = value;
  }-*/;

  native static PreferenceScopeTypes PreferenceScopeTypesProducer_PreferenceScopeTypes_defaultPreferenceScopeTypes(PreferenceScopeTypesProducer instance) /*-{
    return instance.@org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer::defaultPreferenceScopeTypes;
  }-*/;

  native static void PreferenceScopeTypesProducer_PreferenceScopeTypes_defaultPreferenceScopeTypes(PreferenceScopeTypesProducer instance, PreferenceScopeTypes value) /*-{
    instance.@org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer::defaultPreferenceScopeTypes = value;
  }-*/;

  native static SessionInfo PreferenceScopeTypesProducer_SessionInfo_sessionInfo(PreferenceScopeTypesProducer instance) /*-{
    return instance.@org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer::sessionInfo;
  }-*/;

  native static void PreferenceScopeTypesProducer_SessionInfo_sessionInfo(PreferenceScopeTypesProducer instance, SessionInfo value) /*-{
    instance.@org.uberfire.preferences.shared.impl.PreferenceScopeTypesProducer::sessionInfo = value;
  }-*/;
}