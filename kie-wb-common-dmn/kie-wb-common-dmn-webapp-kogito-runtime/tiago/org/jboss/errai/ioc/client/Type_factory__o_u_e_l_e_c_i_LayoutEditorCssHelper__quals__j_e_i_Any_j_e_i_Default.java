package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.layout.editor.api.css.CssAllowedValue;
import org.uberfire.ext.layout.editor.api.css.CssProperty;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutElementWithProperties;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.components.rows.Row;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;

public class Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorCssHelper> { private class Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutEditorCssHelper implements Proxy<LayoutEditorCssHelper> {
    private final ProxyHelper<LayoutEditorCssHelper> proxyHelper = new ProxyHelperImpl<LayoutEditorCssHelper>("Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutEditorCssHelper instance) {

    }

    public LayoutEditorCssHelper asBeanType() {
      return this;
    }

    public void setInstance(final LayoutEditorCssHelper instance) {
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

    @Override public String formatCssAllowedValue(CssProperty cssProperty) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.formatCssAllowedValue(cssProperty);
        return retVal;
      } else {
        return super.formatCssAllowedValue(cssProperty);
      }
    }

    @Override public String formatCssAllowedValue(CssProperty property, CssAllowedValue value) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.formatCssAllowedValue(property, value);
        return retVal;
      } else {
        return super.formatCssAllowedValue(property, value);
      }
    }

    @Override public String getHelpText(CssProperty cssProperty) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getHelpText(cssProperty);
        return retVal;
      } else {
        return super.getHelpText(cssProperty);
      }
    }

    @Override public List getValidators(CssProperty property) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getValidators(property);
        return retVal;
      } else {
        return super.getValidators(property);
      }
    }

    @Override public PropertyEditorType resolveFieldType(CssProperty cssProperty) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final PropertyEditorType retVal = proxiedInstance.resolveFieldType(cssProperty);
        return retVal;
      } else {
        return super.resolveFieldType(cssProperty);
      }
    }

    @Override public PropertyEditorCategory createCategory(String categoryCode) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final PropertyEditorCategory retVal = proxiedInstance.createCategory(categoryCode);
        return retVal;
      } else {
        return super.createCategory(categoryCode);
      }
    }

    @Override public PropertyEditorFieldInfo createField(LayoutElementWithProperties element, CssProperty property) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final PropertyEditorFieldInfo retVal = proxiedInstance.createField(element, property);
        return retVal;
      } else {
        return super.createField(element, property);
      }
    }

    @Override public PropertyEditorFieldInfo createField(Map propertyMap, CssProperty property) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final PropertyEditorFieldInfo retVal = proxiedInstance.createField(propertyMap, property);
        return retVal;
      } else {
        return super.createField(propertyMap, property);
      }
    }

    @Override public PropertyEditorFieldInfo createPropertyField(CssProperty property, String value) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final PropertyEditorFieldInfo retVal = proxiedInstance.createPropertyField(property, value);
        return retVal;
      } else {
        return super.createPropertyField(property, value);
      }
    }

    @Override public CssAllowedValue parseCssAllowedValue(String property, String value) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final CssAllowedValue retVal = proxiedInstance.parseCssAllowedValue(property, value);
        return retVal;
      } else {
        return super.parseCssAllowedValue(property, value);
      }
    }

    @Override public CssAllowedValue parseCssAllowedValue(CssProperty property, String value) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final CssAllowedValue retVal = proxiedInstance.parseCssAllowedValue(property, value);
        return retVal;
      } else {
        return super.parseCssAllowedValue(property, value);
      }
    }

    @Override public List readCssValues(Map propertyMap) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.readCssValues(propertyMap);
        return retVal;
      } else {
        return super.readCssValues(propertyMap);
      }
    }

    @Override public List getContainerPropertyCategories(Container container) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getContainerPropertyCategories(container);
        return retVal;
      } else {
        return super.getContainerPropertyCategories(container);
      }
    }

    @Override public List getRowPropertyCategories(Row row) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getRowPropertyCategories(row);
        return retVal;
      } else {
        return super.getRowPropertyCategories(row);
      }
    }

    @Override public List getComponentPropertyCategories(LayoutComponent layoutComponent) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getComponentPropertyCategories(layoutComponent);
        return retVal;
      } else {
        return super.getComponentPropertyCategories(layoutComponent);
      }
    }

    @Override public List allCategories(Map propertyMap) {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.allCategories(propertyMap);
        return retVal;
      } else {
        return super.allCategories(propertyMap);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutEditorCssHelper proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorCssHelper.class, "Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorCssHelper.class, Object.class });
  }

  public LayoutEditorCssHelper createInstance(final ContextManager contextManager) {
    final LayoutEditorCssHelper instance = new LayoutEditorCssHelper();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutEditorCssHelper> proxyImpl = new Type_factory__o_u_e_l_e_c_i_LayoutEditorCssHelper__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}