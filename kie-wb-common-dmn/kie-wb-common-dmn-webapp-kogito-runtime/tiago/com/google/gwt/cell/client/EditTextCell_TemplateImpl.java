package com.google.gwt.cell.client;

public class EditTextCell_TemplateImpl implements com.google.gwt.cell.client.EditTextCell.Template {
  
  public com.google.gwt.safehtml.shared.SafeHtml input(java.lang.String arg0) {
    StringBuilder sb = new java.lang.StringBuilder();
    sb.append("<input type=\"text\" value=\"");
    sb.append(com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape(arg0));
    sb.append("\" tabindex=\"-1\"></input>");
return new com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(sb.toString());
}
}
