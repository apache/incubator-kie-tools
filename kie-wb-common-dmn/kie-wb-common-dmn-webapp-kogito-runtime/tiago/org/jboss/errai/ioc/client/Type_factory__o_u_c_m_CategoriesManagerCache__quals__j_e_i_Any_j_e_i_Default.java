package org.jboss.errai.ioc.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Undefined;

public class Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default extends Factory<CategoriesManagerCache> { private class Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CategoriesManagerCache implements Proxy<CategoriesManagerCache> {
    private final ProxyHelper<CategoriesManagerCache> proxyHelper = new ProxyHelperImpl<CategoriesManagerCache>("Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final CategoriesManagerCache instance) {

    }

    public CategoriesManagerCache asBeanType() {
      return this;
    }

    public void setInstance(final CategoriesManagerCache instance) {
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

    @Override public Set getCategories() {
      if (proxyHelper != null) {
        final CategoriesManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getCategories();
        return retVal;
      } else {
        return super.getCategories();
      }
    }

    @Override public void add(Category category) {
      if (proxyHelper != null) {
        final CategoriesManagerCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.add(category);
      } else {
        super.add(category);
      }
    }

    @Override public void addAll(Collection category) {
      if (proxyHelper != null) {
        final CategoriesManagerCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addAll(category);
      } else {
        super.addAll(category);
      }
    }

    @Override public void addAllFromResourceTypes(List clientResourceType) {
      if (proxyHelper != null) {
        final CategoriesManagerCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addAllFromResourceTypes(clientResourceType);
      } else {
        super.addAllFromResourceTypes(clientResourceType);
      }
    }

    @Override public Category getCategory(String filterType) {
      if (proxyHelper != null) {
        final CategoriesManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final Category retVal = proxiedInstance.getCategory(filterType);
        return retVal;
      } else {
        return super.getCategory(filterType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CategoriesManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CategoriesManagerCache.class, "Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CategoriesManagerCache.class, Object.class });
  }

  public CategoriesManagerCache createInstance(final ContextManager contextManager) {
    final Undefined _undefinedCategory_0 = (Undefined) contextManager.getInstance("Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default");
    final CategoriesManagerCache instance = new CategoriesManagerCache(_undefinedCategory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.mvp.CategoriesManagerCache an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.mvp.CategoriesManagerCache ([org.uberfire.workbench.category.Undefined])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CategoriesManagerCache> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}