package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRow.View;
import org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView;

public class Type_factory__o_u_e_l_e_c_c_r_EmptyDropRowView__quals__j_e_i_Any_j_e_i_Default extends Factory<EmptyDropRowView> { public interface o_u_e_l_e_c_c_r_EmptyDropRowViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_c_r_EmptyDropRowView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EmptyDropRowView.class, "Type_factory__o_u_e_l_e_c_c_r_EmptyDropRowView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EmptyDropRowView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_c_r_EmptyDropRowViewTemplateResource) GWT.create(o_u_e_l_e_c_c_r_EmptyDropRowViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public EmptyDropRowView createInstance(final ContextManager contextManager) {
    final EmptyDropRowView instance = new EmptyDropRowView();
    setIncompleteInstance(instance);
    final Div EmptyDropRowView_row = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, EmptyDropRowView_row);
    EmptyDropRowView_Div_row(instance, EmptyDropRowView_row);
    final Div EmptyDropRowView_innerRow = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, EmptyDropRowView_innerRow);
    EmptyDropRowView_Div_innerRow(instance, EmptyDropRowView_innerRow);
    final Heading EmptyDropRowView_title = (Heading) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, EmptyDropRowView_title);
    EmptyDropRowView_Heading_title(instance, EmptyDropRowView_title);
    final Span EmptyDropRowView_subtitle = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, EmptyDropRowView_subtitle);
    EmptyDropRowView_Span_subtitle(instance, EmptyDropRowView_subtitle);
    o_u_e_l_e_c_c_r_EmptyDropRowViewTemplateResource templateForEmptyDropRowView = GWT.create(o_u_e_l_e_c_c_r_EmptyDropRowViewTemplateResource.class);
    Element parentElementForTemplateOfEmptyDropRowView = TemplateUtil.getRootTemplateParentElement(templateForEmptyDropRowView.getContents().getText(), "org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfEmptyDropRowView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfEmptyDropRowView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("row", new DataFieldMeta());
    dataFieldMetas.put("inner-row", new DataFieldMeta());
    dataFieldMetas.put("title", new DataFieldMeta());
    dataFieldMetas.put("subtitle", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView", "org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Div_row(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "row");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView", "org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Div_innerRow(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "inner-row");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView", "org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Heading_title(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "title");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView", "org/uberfire/ext/layout/editor/client/components/rows/EmptyDropRowView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Span_subtitle(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "subtitle");
    templateFieldsMap.put("row", ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Div_row(instance))));
    templateFieldsMap.put("inner-row", ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Div_innerRow(instance))));
    templateFieldsMap.put("title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Heading_title(instance))));
    templateFieldsMap.put("subtitle", ElementWrapperWidget.getWidget(TemplateUtil.asElement(EmptyDropRowView_Span_subtitle(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfEmptyDropRowView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((EmptyDropRowView) instance, contextManager);
  }

  public void destroyInstanceHelper(final EmptyDropRowView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span EmptyDropRowView_Span_subtitle(EmptyDropRowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::subtitle;
  }-*/;

  native static void EmptyDropRowView_Span_subtitle(EmptyDropRowView instance, Span value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::subtitle = value;
  }-*/;

  native static Heading EmptyDropRowView_Heading_title(EmptyDropRowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::title;
  }-*/;

  native static void EmptyDropRowView_Heading_title(EmptyDropRowView instance, Heading value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::title = value;
  }-*/;

  native static Div EmptyDropRowView_Div_innerRow(EmptyDropRowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::innerRow;
  }-*/;

  native static void EmptyDropRowView_Div_innerRow(EmptyDropRowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::innerRow = value;
  }-*/;

  native static Div EmptyDropRowView_Div_row(EmptyDropRowView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::row;
  }-*/;

  native static void EmptyDropRowView_Div_row(EmptyDropRowView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.components.rows.EmptyDropRowView::row = value;
  }-*/;
}