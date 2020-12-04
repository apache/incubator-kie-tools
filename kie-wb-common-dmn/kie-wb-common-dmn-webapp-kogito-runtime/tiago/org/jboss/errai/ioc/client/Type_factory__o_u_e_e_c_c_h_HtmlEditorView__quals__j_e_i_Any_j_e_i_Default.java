package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorLibraryLoader;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorPresenter.View;
import org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class Type_factory__o_u_e_e_c_c_h_HtmlEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<HtmlEditorView> { public interface o_u_e_e_c_c_h_HtmlEditorViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html") public TextResource getContents();
  @Source("org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_e_c_c_h_HtmlEditorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HtmlEditorView.class, "Type_factory__o_u_e_e_c_c_h_HtmlEditorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HtmlEditorView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, BaseEditorView.class, HasBusyIndicator.class, IsWidget.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_e_c_c_h_HtmlEditorViewTemplateResource) GWT.create(o_u_e_e_c_c_h_HtmlEditorViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public HtmlEditorView createInstance(final ContextManager contextManager) {
    final HtmlEditorLibraryLoader _libraryLoader_1 = (HtmlEditorLibraryLoader) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_HtmlEditorLibraryLoader__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final HtmlEditorView instance = new HtmlEditorView(_translationService_0, _libraryLoader_1);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Anchor HtmlEditorView_fontSizeActionXSmall = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeActionXSmall);
    HtmlEditorView_Anchor_fontSizeActionXSmall(instance, HtmlEditorView_fontSizeActionXSmall);
    final Button HtmlEditorView_mediumTitleAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_mediumTitleAction);
    HtmlEditorView_Button_mediumTitleAction(instance, HtmlEditorView_mediumTitleAction);
    final Input HtmlEditorView_backgroundColorInput = (Input) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_backgroundColorInput);
    HtmlEditorView_Input_backgroundColorInput(instance, HtmlEditorView_backgroundColorInput);
    final Button HtmlEditorView_fontColorAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontColorAction);
    HtmlEditorView_Button_fontColorAction(instance, HtmlEditorView_fontColorAction);
    final Div HtmlEditorView_toolbar = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_toolbar);
    HtmlEditorView_Div_toolbar(instance, HtmlEditorView_toolbar);
    final Button HtmlEditorView_plainTextAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_plainTextAction);
    HtmlEditorView_Button_plainTextAction(instance, HtmlEditorView_plainTextAction);
    final Div HtmlEditorView_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_container);
    HtmlEditorView_Div_container(instance, HtmlEditorView_container);
    final Button HtmlEditorView_alignLeftAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_alignLeftAction);
    HtmlEditorView_Button_alignLeftAction(instance, HtmlEditorView_alignLeftAction);
    final Button HtmlEditorView_alignCenterAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_alignCenterAction);
    HtmlEditorView_Button_alignCenterAction(instance, HtmlEditorView_alignCenterAction);
    final Button HtmlEditorView_smallTitleAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_smallTitleAction);
    HtmlEditorView_Button_smallTitleAction(instance, HtmlEditorView_smallTitleAction);
    final Button HtmlEditorView_orderedListAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_orderedListAction);
    HtmlEditorView_Button_orderedListAction(instance, HtmlEditorView_orderedListAction);
    final Button HtmlEditorView_quoteAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_quoteAction);
    HtmlEditorView_Button_quoteAction(instance, HtmlEditorView_quoteAction);
    final Button HtmlEditorView_bigTitleAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_bigTitleAction);
    HtmlEditorView_Button_bigTitleAction(instance, HtmlEditorView_bigTitleAction);
    final Div HtmlEditorView_htmlEditor = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_htmlEditor);
    HtmlEditorView_Div_htmlEditor(instance, HtmlEditorView_htmlEditor);
    final Button HtmlEditorView_removeLinkAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_removeLinkAction);
    HtmlEditorView_Button_removeLinkAction(instance, HtmlEditorView_removeLinkAction);
    final Button HtmlEditorView_fontSizeAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeAction);
    HtmlEditorView_Button_fontSizeAction(instance, HtmlEditorView_fontSizeAction);
    final Button HtmlEditorView_alignRightAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_alignRightAction);
    HtmlEditorView_Button_alignRightAction(instance, HtmlEditorView_alignRightAction);
    final Button HtmlEditorView_unorderedListAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_unorderedListAction);
    HtmlEditorView_Button_unorderedListAction(instance, HtmlEditorView_unorderedListAction);
    final Button HtmlEditorView_backgroundColorButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_backgroundColorButton);
    HtmlEditorView_Button_backgroundColorButton(instance, HtmlEditorView_backgroundColorButton);
    final Button HtmlEditorView_undoAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_undoAction);
    HtmlEditorView_Button_undoAction(instance, HtmlEditorView_undoAction);
    final Button HtmlEditorView_insertImageAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_insertImageAction);
    HtmlEditorView_Button_insertImageAction(instance, HtmlEditorView_insertImageAction);
    final Button HtmlEditorView_switchToHtmlAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_switchToHtmlAction);
    HtmlEditorView_Button_switchToHtmlAction(instance, HtmlEditorView_switchToHtmlAction);
    final Button HtmlEditorView_codeAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_codeAction);
    HtmlEditorView_Button_codeAction(instance, HtmlEditorView_codeAction);
    final Button HtmlEditorView_insertTableAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_insertTableAction);
    HtmlEditorView_Button_insertTableAction(instance, HtmlEditorView_insertTableAction);
    final Button HtmlEditorView_createLinkAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_createLinkAction);
    HtmlEditorView_Button_createLinkAction(instance, HtmlEditorView_createLinkAction);
    final Button HtmlEditorView_preAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_preAction);
    HtmlEditorView_Button_preAction(instance, HtmlEditorView_preAction);
    final Button HtmlEditorView_fontColorButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontColorButton);
    HtmlEditorView_Button_fontColorButton(instance, HtmlEditorView_fontColorButton);
    final Anchor HtmlEditorView_fontSizeActionXLarge = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeActionXLarge);
    HtmlEditorView_Anchor_fontSizeActionXLarge(instance, HtmlEditorView_fontSizeActionXLarge);
    final Button HtmlEditorView_underlineAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_underlineAction);
    HtmlEditorView_Button_underlineAction(instance, HtmlEditorView_underlineAction);
    final Button HtmlEditorView_italicAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_italicAction);
    HtmlEditorView_Button_italicAction(instance, HtmlEditorView_italicAction);
    final Button HtmlEditorView_redoAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_redoAction);
    HtmlEditorView_Button_redoAction(instance, HtmlEditorView_redoAction);
    final Anchor HtmlEditorView_fontSizeActionSmall = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeActionSmall);
    HtmlEditorView_Anchor_fontSizeActionSmall(instance, HtmlEditorView_fontSizeActionSmall);
    final Anchor HtmlEditorView_fontSizeActionLarge = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeActionLarge);
    HtmlEditorView_Anchor_fontSizeActionLarge(instance, HtmlEditorView_fontSizeActionLarge);
    final Button HtmlEditorView_boldAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_boldAction);
    HtmlEditorView_Button_boldAction(instance, HtmlEditorView_boldAction);
    final Button HtmlEditorView_backgroundColorAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_backgroundColorAction);
    HtmlEditorView_Button_backgroundColorAction(instance, HtmlEditorView_backgroundColorAction);
    final Button HtmlEditorView_indentListAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_indentListAction);
    HtmlEditorView_Button_indentListAction(instance, HtmlEditorView_indentListAction);
    final Button HtmlEditorView_paragraphAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_paragraphAction);
    HtmlEditorView_Button_paragraphAction(instance, HtmlEditorView_paragraphAction);
    final Anchor HtmlEditorView_fontSizeActionMedium = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeActionMedium);
    HtmlEditorView_Anchor_fontSizeActionMedium(instance, HtmlEditorView_fontSizeActionMedium);
    final Button HtmlEditorView_outdentListAction = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_outdentListAction);
    HtmlEditorView_Button_outdentListAction(instance, HtmlEditorView_outdentListAction);
    final Anchor HtmlEditorView_fontSizeActionXXLarge = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontSizeActionXXLarge);
    HtmlEditorView_Anchor_fontSizeActionXXLarge(instance, HtmlEditorView_fontSizeActionXXLarge);
    final Input HtmlEditorView_fontColorInput = (Input) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Input__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, HtmlEditorView_fontColorInput);
    HtmlEditorView_Input_fontColorInput(instance, HtmlEditorView_fontColorInput);
    thisInstance.setReference(instance, "docksInteractionEventSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent", new AbstractCDIEventCallback<UberfireDocksInteractionEvent>() {
      public void fireEvent(final UberfireDocksInteractionEvent event) {
        instance.docksInteractionEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent []";
      }
    }));
    o_u_e_e_c_c_h_HtmlEditorViewTemplateResource templateForHtmlEditorView = GWT.create(o_u_e_e_c_c_h_HtmlEditorViewTemplateResource.class);
    Element parentElementForTemplateOfHtmlEditorView = TemplateUtil.getRootTemplateParentElement(templateForHtmlEditorView.getContents().getText(), "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfHtmlEditorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfHtmlEditorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(41);
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("html-editor", new DataFieldMeta());
    dataFieldMetas.put("html-editor-toolbar", new DataFieldMeta());
    dataFieldMetas.put("bold-action", new DataFieldMeta());
    dataFieldMetas.put("italic-action", new DataFieldMeta());
    dataFieldMetas.put("underline-action", new DataFieldMeta());
    dataFieldMetas.put("create-link-action", new DataFieldMeta());
    dataFieldMetas.put("remove-link-action", new DataFieldMeta());
    dataFieldMetas.put("insert-image-action", new DataFieldMeta());
    dataFieldMetas.put("insert-table-action", new DataFieldMeta());
    dataFieldMetas.put("big-title-action", new DataFieldMeta());
    dataFieldMetas.put("medium-title-action", new DataFieldMeta());
    dataFieldMetas.put("small-title-action", new DataFieldMeta());
    dataFieldMetas.put("paragraph-action", new DataFieldMeta());
    dataFieldMetas.put("pre-action", new DataFieldMeta());
    dataFieldMetas.put("plain-text-action", new DataFieldMeta());
    dataFieldMetas.put("quote-action", new DataFieldMeta());
    dataFieldMetas.put("code-action", new DataFieldMeta());
    dataFieldMetas.put("font-size-action", new DataFieldMeta());
    dataFieldMetas.put("font-size-action-x-small", new DataFieldMeta());
    dataFieldMetas.put("font-size-action-small", new DataFieldMeta());
    dataFieldMetas.put("font-size-action-medium", new DataFieldMeta());
    dataFieldMetas.put("font-size-action-large", new DataFieldMeta());
    dataFieldMetas.put("font-size-action-x-large", new DataFieldMeta());
    dataFieldMetas.put("font-size-action-xx-large", new DataFieldMeta());
    dataFieldMetas.put("font-color-action", new DataFieldMeta());
    dataFieldMetas.put("background-color-action", new DataFieldMeta());
    dataFieldMetas.put("unordered-list-action", new DataFieldMeta());
    dataFieldMetas.put("ordered-list-action", new DataFieldMeta());
    dataFieldMetas.put("outdent-list-action", new DataFieldMeta());
    dataFieldMetas.put("indent-list-action", new DataFieldMeta());
    dataFieldMetas.put("align-left-action", new DataFieldMeta());
    dataFieldMetas.put("align-right-action", new DataFieldMeta());
    dataFieldMetas.put("align-center-action", new DataFieldMeta());
    dataFieldMetas.put("undo-action", new DataFieldMeta());
    dataFieldMetas.put("redo-action", new DataFieldMeta());
    dataFieldMetas.put("switch-to-html-action", new DataFieldMeta());
    dataFieldMetas.put("background-color-button", new DataFieldMeta());
    dataFieldMetas.put("background-color-input", new DataFieldMeta());
    dataFieldMetas.put("font-color-button", new DataFieldMeta());
    dataFieldMetas.put("font-color-input", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Div_htmlEditor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "html-editor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Div_toolbar(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "html-editor-toolbar");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_boldAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "bold-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_italicAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "italic-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_underlineAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "underline-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_createLinkAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "create-link-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_removeLinkAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove-link-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_insertImageAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "insert-image-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_insertTableAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "insert-table-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_bigTitleAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "big-title-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_mediumTitleAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "medium-title-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_smallTitleAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "small-title-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_paragraphAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "paragraph-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_preAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "pre-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_plainTextAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "plain-text-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_quoteAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "quote-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_codeAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "code-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_fontSizeAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionXSmall(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action-x-small");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionSmall(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action-small");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionMedium(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action-medium");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionLarge(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action-large");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionXLarge(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action-x-large");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionXXLarge(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-size-action-xx-large");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_fontColorAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-color-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_backgroundColorAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "background-color-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_unorderedListAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "unordered-list-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_orderedListAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "ordered-list-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_outdentListAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "outdent-list-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_indentListAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "indent-list-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_alignLeftAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "align-left-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_alignRightAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "align-right-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_alignCenterAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "align-center-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_undoAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "undo-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_redoAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "redo-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_switchToHtmlAction(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "switch-to-html-action");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_backgroundColorButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "background-color-button");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Input_backgroundColorInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "background-color-input");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_fontColorButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-color-button");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView", "org/uberfire/ext/editor/commons/client/htmleditor/HtmlEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Input_fontColorInput(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "font-color-input");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Div_container(instance))));
    templateFieldsMap.put("html-editor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Div_htmlEditor(instance))));
    templateFieldsMap.put("html-editor-toolbar", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Div_toolbar(instance))));
    templateFieldsMap.put("bold-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_boldAction(instance))));
    templateFieldsMap.put("italic-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_italicAction(instance))));
    templateFieldsMap.put("underline-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_underlineAction(instance))));
    templateFieldsMap.put("create-link-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_createLinkAction(instance))));
    templateFieldsMap.put("remove-link-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_removeLinkAction(instance))));
    templateFieldsMap.put("insert-image-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_insertImageAction(instance))));
    templateFieldsMap.put("insert-table-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_insertTableAction(instance))));
    templateFieldsMap.put("big-title-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_bigTitleAction(instance))));
    templateFieldsMap.put("medium-title-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_mediumTitleAction(instance))));
    templateFieldsMap.put("small-title-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_smallTitleAction(instance))));
    templateFieldsMap.put("paragraph-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_paragraphAction(instance))));
    templateFieldsMap.put("pre-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_preAction(instance))));
    templateFieldsMap.put("plain-text-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_plainTextAction(instance))));
    templateFieldsMap.put("quote-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_quoteAction(instance))));
    templateFieldsMap.put("code-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_codeAction(instance))));
    templateFieldsMap.put("font-size-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_fontSizeAction(instance))));
    templateFieldsMap.put("font-size-action-x-small", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionXSmall(instance))));
    templateFieldsMap.put("font-size-action-small", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionSmall(instance))));
    templateFieldsMap.put("font-size-action-medium", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionMedium(instance))));
    templateFieldsMap.put("font-size-action-large", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionLarge(instance))));
    templateFieldsMap.put("font-size-action-x-large", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionXLarge(instance))));
    templateFieldsMap.put("font-size-action-xx-large", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Anchor_fontSizeActionXXLarge(instance))));
    templateFieldsMap.put("font-color-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_fontColorAction(instance))));
    templateFieldsMap.put("background-color-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_backgroundColorAction(instance))));
    templateFieldsMap.put("unordered-list-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_unorderedListAction(instance))));
    templateFieldsMap.put("ordered-list-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_orderedListAction(instance))));
    templateFieldsMap.put("outdent-list-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_outdentListAction(instance))));
    templateFieldsMap.put("indent-list-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_indentListAction(instance))));
    templateFieldsMap.put("align-left-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_alignLeftAction(instance))));
    templateFieldsMap.put("align-right-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_alignRightAction(instance))));
    templateFieldsMap.put("align-center-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_alignCenterAction(instance))));
    templateFieldsMap.put("undo-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_undoAction(instance))));
    templateFieldsMap.put("redo-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_redoAction(instance))));
    templateFieldsMap.put("switch-to-html-action", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_switchToHtmlAction(instance))));
    templateFieldsMap.put("background-color-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_backgroundColorButton(instance))));
    templateFieldsMap.put("background-color-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Input_backgroundColorInput(instance))));
    templateFieldsMap.put("font-color-button", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Button_fontColorButton(instance))));
    templateFieldsMap.put("font-color-input", ElementWrapperWidget.getWidget(TemplateUtil.asElement(HtmlEditorView_Input_fontColorInput(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfHtmlEditorView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((HtmlEditorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final HtmlEditorView instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "docksInteractionEventSubscription", Subscription.class)).remove();
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final HtmlEditorView instance) {
    instance.postConstruct();
  }

  native static Button HtmlEditorView_Button_preAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::preAction;
  }-*/;

  native static void HtmlEditorView_Button_preAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::preAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_orderedListAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::orderedListAction;
  }-*/;

  native static void HtmlEditorView_Button_orderedListAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::orderedListAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_italicAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::italicAction;
  }-*/;

  native static void HtmlEditorView_Button_italicAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::italicAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_outdentListAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::outdentListAction;
  }-*/;

  native static void HtmlEditorView_Button_outdentListAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::outdentListAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_insertTableAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::insertTableAction;
  }-*/;

  native static void HtmlEditorView_Button_insertTableAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::insertTableAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_bigTitleAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::bigTitleAction;
  }-*/;

  native static void HtmlEditorView_Button_bigTitleAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::bigTitleAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_fontColorAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontColorAction;
  }-*/;

  native static void HtmlEditorView_Button_fontColorAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontColorAction = value;
  }-*/;

  native static Div HtmlEditorView_Div_toolbar(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::toolbar;
  }-*/;

  native static void HtmlEditorView_Div_toolbar(HtmlEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::toolbar = value;
  }-*/;

  native static Button HtmlEditorView_Button_removeLinkAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::removeLinkAction;
  }-*/;

  native static void HtmlEditorView_Button_removeLinkAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::removeLinkAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_codeAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::codeAction;
  }-*/;

  native static void HtmlEditorView_Button_codeAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::codeAction = value;
  }-*/;

  native static Anchor HtmlEditorView_Anchor_fontSizeActionXXLarge(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionXXLarge;
  }-*/;

  native static void HtmlEditorView_Anchor_fontSizeActionXXLarge(HtmlEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionXXLarge = value;
  }-*/;

  native static Button HtmlEditorView_Button_quoteAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::quoteAction;
  }-*/;

  native static void HtmlEditorView_Button_quoteAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::quoteAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_underlineAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::underlineAction;
  }-*/;

  native static void HtmlEditorView_Button_underlineAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::underlineAction = value;
  }-*/;

  native static Anchor HtmlEditorView_Anchor_fontSizeActionSmall(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionSmall;
  }-*/;

  native static void HtmlEditorView_Anchor_fontSizeActionSmall(HtmlEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionSmall = value;
  }-*/;

  native static Button HtmlEditorView_Button_alignCenterAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::alignCenterAction;
  }-*/;

  native static void HtmlEditorView_Button_alignCenterAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::alignCenterAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_fontSizeAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeAction;
  }-*/;

  native static void HtmlEditorView_Button_fontSizeAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_redoAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::redoAction;
  }-*/;

  native static void HtmlEditorView_Button_redoAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::redoAction = value;
  }-*/;

  native static Input HtmlEditorView_Input_backgroundColorInput(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::backgroundColorInput;
  }-*/;

  native static void HtmlEditorView_Input_backgroundColorInput(HtmlEditorView instance, Input value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::backgroundColorInput = value;
  }-*/;

  native static Button HtmlEditorView_Button_plainTextAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::plainTextAction;
  }-*/;

  native static void HtmlEditorView_Button_plainTextAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::plainTextAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_createLinkAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::createLinkAction;
  }-*/;

  native static void HtmlEditorView_Button_createLinkAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::createLinkAction = value;
  }-*/;

  native static Anchor HtmlEditorView_Anchor_fontSizeActionXSmall(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionXSmall;
  }-*/;

  native static void HtmlEditorView_Anchor_fontSizeActionXSmall(HtmlEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionXSmall = value;
  }-*/;

  native static Button HtmlEditorView_Button_paragraphAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::paragraphAction;
  }-*/;

  native static void HtmlEditorView_Button_paragraphAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::paragraphAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_switchToHtmlAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::switchToHtmlAction;
  }-*/;

  native static void HtmlEditorView_Button_switchToHtmlAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::switchToHtmlAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_mediumTitleAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::mediumTitleAction;
  }-*/;

  native static void HtmlEditorView_Button_mediumTitleAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::mediumTitleAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_fontColorButton(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontColorButton;
  }-*/;

  native static void HtmlEditorView_Button_fontColorButton(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontColorButton = value;
  }-*/;

  native static Anchor HtmlEditorView_Anchor_fontSizeActionLarge(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionLarge;
  }-*/;

  native static void HtmlEditorView_Anchor_fontSizeActionLarge(HtmlEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionLarge = value;
  }-*/;

  native static Button HtmlEditorView_Button_boldAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::boldAction;
  }-*/;

  native static void HtmlEditorView_Button_boldAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::boldAction = value;
  }-*/;

  native static Input HtmlEditorView_Input_fontColorInput(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontColorInput;
  }-*/;

  native static void HtmlEditorView_Input_fontColorInput(HtmlEditorView instance, Input value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontColorInput = value;
  }-*/;

  native static Button HtmlEditorView_Button_smallTitleAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::smallTitleAction;
  }-*/;

  native static void HtmlEditorView_Button_smallTitleAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::smallTitleAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_undoAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::undoAction;
  }-*/;

  native static void HtmlEditorView_Button_undoAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::undoAction = value;
  }-*/;

  native static Div HtmlEditorView_Div_container(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::container;
  }-*/;

  native static void HtmlEditorView_Div_container(HtmlEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::container = value;
  }-*/;

  native static Button HtmlEditorView_Button_backgroundColorAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::backgroundColorAction;
  }-*/;

  native static void HtmlEditorView_Button_backgroundColorAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::backgroundColorAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_alignRightAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::alignRightAction;
  }-*/;

  native static void HtmlEditorView_Button_alignRightAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::alignRightAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_insertImageAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::insertImageAction;
  }-*/;

  native static void HtmlEditorView_Button_insertImageAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::insertImageAction = value;
  }-*/;

  native static Anchor HtmlEditorView_Anchor_fontSizeActionMedium(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionMedium;
  }-*/;

  native static void HtmlEditorView_Anchor_fontSizeActionMedium(HtmlEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionMedium = value;
  }-*/;

  native static Button HtmlEditorView_Button_indentListAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::indentListAction;
  }-*/;

  native static void HtmlEditorView_Button_indentListAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::indentListAction = value;
  }-*/;

  native static Button HtmlEditorView_Button_backgroundColorButton(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::backgroundColorButton;
  }-*/;

  native static void HtmlEditorView_Button_backgroundColorButton(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::backgroundColorButton = value;
  }-*/;

  native static Div HtmlEditorView_Div_htmlEditor(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::htmlEditor;
  }-*/;

  native static void HtmlEditorView_Div_htmlEditor(HtmlEditorView instance, Div value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::htmlEditor = value;
  }-*/;

  native static Button HtmlEditorView_Button_unorderedListAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::unorderedListAction;
  }-*/;

  native static void HtmlEditorView_Button_unorderedListAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::unorderedListAction = value;
  }-*/;

  native static Anchor HtmlEditorView_Anchor_fontSizeActionXLarge(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionXLarge;
  }-*/;

  native static void HtmlEditorView_Anchor_fontSizeActionXLarge(HtmlEditorView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::fontSizeActionXLarge = value;
  }-*/;

  native static Button HtmlEditorView_Button_alignLeftAction(HtmlEditorView instance) /*-{
    return instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::alignLeftAction;
  }-*/;

  native static void HtmlEditorView_Button_alignLeftAction(HtmlEditorView instance, Button value) /*-{
    instance.@org.uberfire.ext.editor.commons.client.htmleditor.HtmlEditorView::alignLeftAction = value;
  }-*/;
}