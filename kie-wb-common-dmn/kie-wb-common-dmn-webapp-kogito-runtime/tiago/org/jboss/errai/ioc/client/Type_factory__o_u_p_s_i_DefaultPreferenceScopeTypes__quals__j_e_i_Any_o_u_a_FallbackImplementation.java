package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.preferences.client.scope.ClientUsernameProvider;
import org.uberfire.preferences.shared.PreferenceScopeTypes;
import org.uberfire.preferences.shared.UsernameProvider;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes;
import org.uberfire.preferences.shared.impl.exception.InvalidPreferenceScopeException;

public class Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation extends Factory<DefaultPreferenceScopeTypes> { private class Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementationProxyImpl extends DefaultPreferenceScopeTypes implements Proxy<DefaultPreferenceScopeTypes> {
    private final ProxyHelper<DefaultPreferenceScopeTypes> proxyHelper = new ProxyHelperImpl<DefaultPreferenceScopeTypes>("Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation");
    public void initProxyProperties(final DefaultPreferenceScopeTypes instance) {

    }

    public DefaultPreferenceScopeTypes asBeanType() {
      return this;
    }

    public void setInstance(final DefaultPreferenceScopeTypes instance) {
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

    @Override public boolean typeRequiresKey(String type) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final DefaultPreferenceScopeTypes proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.typeRequiresKey(type);
        return retVal;
      } else {
        return super.typeRequiresKey(type);
      }
    }

    @Override public String getDefaultKeyFor(String type) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final DefaultPreferenceScopeTypes proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefaultKeyFor(type);
        return retVal;
      } else {
        return super.getDefaultKeyFor(type);
      }
    }

    @Override protected void validateType(String type) throws InvalidPreferenceScopeException {
      if (proxyHelper != null) {
        final DefaultPreferenceScopeTypes proxiedInstance = proxyHelper.getInstance(this);
        DefaultPreferenceScopeTypes_validateType_String(proxiedInstance, type);
      } else {
        super.validateType(type);
      }
    }

    @Override protected boolean isEmpty(String str) {
      if (proxyHelper != null) {
        final DefaultPreferenceScopeTypes proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = DefaultPreferenceScopeTypes_isEmpty_String(proxiedInstance, str);
        return retVal;
      } else {
        return super.isEmpty(str);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefaultPreferenceScopeTypes proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation() {
    super(new FactoryHandleImpl(DefaultPreferenceScopeTypes.class, "Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementation", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPreferenceScopeTypes.class, Object.class, PreferenceScopeTypes.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new FallbackImplementation() {
        public Class annotationType() {
          return FallbackImplementation.class;
        }
        public String toString() {
          return "@org.uberfire.annotations.FallbackImplementation()";
        }
    } });
  }

  public DefaultPreferenceScopeTypes createInstance(final ContextManager contextManager) {
    final UsernameProvider _usernameProvider_0 = (ClientUsernameProvider) contextManager.getInstance("Type_factory__o_u_p_c_s_ClientUsernameProvider__quals__j_e_i_Any_j_e_i_Default");
    final DefaultPreferenceScopeTypes instance = new DefaultPreferenceScopeTypes(_usernameProvider_0);
    registerDependentScopedReference(instance, _usernameProvider_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefaultPreferenceScopeTypes> proxyImpl = new Type_factory__o_u_p_s_i_DefaultPreferenceScopeTypes__quals__j_e_i_Any_o_u_a_FallbackImplementationProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DefaultPreferenceScopeTypes_validateType_String(DefaultPreferenceScopeTypes instance, String a0) /*-{
    instance.@org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes::validateType(Ljava/lang/String;)(a0);
  }-*/;

  public native static boolean DefaultPreferenceScopeTypes_isEmpty_String(DefaultPreferenceScopeTypes instance, String a0) /*-{
    return instance.@org.uberfire.preferences.shared.impl.DefaultPreferenceScopeTypes::isEmpty(Ljava/lang/String;)(a0);
  }-*/;
}