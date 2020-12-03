package org.guvnor.messageconsole.client.console;

public class HyperLinkCell_TextTemplateImpl implements org.guvnor.messageconsole.client.console.HyperLinkCell.TextTemplate {
  
  public com.google.gwt.safehtml.shared.SafeHtml text(java.lang.String arg0,java.lang.String arg1) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<span title=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg1));
    sb.append("\">");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("</span>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
