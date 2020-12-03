package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.categories.Decision;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.resource.DMNDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.definition.AbstractDefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.annotations.VisibleAsset;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAsset extends Factory<DMNDefinitionSetResourceType> { private class Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAssetProxyImpl extends DMNDefinitionSetResourceType implements Proxy<DMNDefinitionSetResourceType> {
    private final ProxyHelper<DMNDefinitionSetResourceType> proxyHelper = new ProxyHelperImpl<DMNDefinitionSetResourceType>("Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAsset");
    public void initProxyProperties(final DMNDefinitionSetResourceType instance) {

    }

    public DMNDefinitionSetResourceType asBeanType() {
      return this;
    }

    public void setInstance(final DMNDefinitionSetResourceType instance) {
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

    @Override public Category getCategory() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final Category retVal = proxiedInstance.getCategory();
        return retVal;
      } else {
        return super.getCategory();
      }
    }

    @Override public String getShortName() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getShortName();
        return retVal;
      } else {
        return super.getShortName();
      }
    }

    @Override public String getDescription() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription();
        return retVal;
      } else {
        return super.getDescription();
      }
    }

    @Override public String getSuffix() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getSuffix();
        return retVal;
      } else {
        return super.getSuffix();
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        return super.getPriority();
      }
    }

    @Override public Class getDefinitionSetType() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final Class retVal = proxiedInstance.getDefinitionSetType();
        return retVal;
      } else {
        return super.getDefinitionSetType();
      }
    }

    @Override public String getPrefix() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPrefix();
        return retVal;
      } else {
        return super.getPrefix();
      }
    }

    @Override public boolean accept(Path path) {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accept(path);
        return retVal;
      } else {
        return super.accept(path);
      }
    }

    @Override public String getSimpleWildcardPattern() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getSimpleWildcardPattern();
        return retVal;
      } else {
        return super.getSimpleWildcardPattern();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDefinitionSetResourceType proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAsset() {
    super(new FactoryHandleImpl(DMNDefinitionSetResourceType.class, "Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAsset", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDefinitionSetResourceType.class, AbstractDefinitionSetResourceType.class, Object.class, DefinitionSetResourceType.class, ResourceTypeDefinition.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, new VisibleAsset() {
        public Class annotationType() {
          return VisibleAsset.class;
        }
        public String toString() {
          return "@org.uberfire.workbench.annotations.VisibleAsset()";
        }
    } });
  }

  public DMNDefinitionSetResourceType createInstance(final ContextManager contextManager) {
    final Decision _category_0 = (Decision) contextManager.getInstance("Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default");
    final DMNDefinitionSetResourceType instance = new DMNDefinitionSetResourceType(_category_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDefinitionSetResourceType> proxyImpl = new Type_factory__o_k_w_c_d_a_r_DMNDefinitionSetResourceType__quals__j_e_i_Any_j_e_i_Default_o_u_w_a_VisibleAssetProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}