package org.uberfire.ext.widgets.common.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class CommonResources_default_InlineClientBundleGenerator implements org.uberfire.ext.widgets.common.client.resources.CommonResources {
  private static CommonResources_default_InlineClientBundleGenerator _instance0 = new CommonResources_default_InlineClientBundleGenerator();
  private void CSSInitializer() {
    CSS = new org.uberfire.ext.widgets.common.client.resources.CommonCss() {
      private boolean injected;
      public boolean ensureInjected() {
        if (!injected) {
          injected = true;
          com.google.gwt.dom.client.StyleInjector.inject(getText());
          return true;
        }
        return false;
      }
      public String getName() {
        return "CSS";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDLO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDMO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getTop() + "px  no-repeat")  + ";height:") + (("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDFO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDGO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden") ) + (";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDEO{border-bottom:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:" + ("3px")  + ";width:" + ("100%")  + ";}.GFVDQLFDKO{border-top:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:" + ("3px")  + ";width:" + ("100%")  + ";}.GFVDQLFDIO{border-right:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:") + (("100%")  + ";width:" + ("3px")  + ";}.GFVDQLFDJO{border-left:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:" + ("100%")  + ";width:" + ("3px")  + ";}.GFVDQLFDAO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px") ) + (";}.GFVDQLFDBO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDKN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getTop() + "px  no-repeat")  + ";height:") + (("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDLN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDJN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getTop() + "px  repeat-x") ) + (";height:" + ("4px")  + ";width:" + ("100%")  + ";}.GFVDQLFDPN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getTop() + "px  repeat-x")  + ";height:" + ("4px")  + ";width:" + ("100%")  + ";}.GFVDQLFDNN{width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getTop() + "px  repeat-y")  + ";height:") + (("100%")  + ";width:" + ("4px")  + ";}.GFVDQLFDON{width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getTop() + "px  repeat-y")  + ";height:" + ("100%")  + ";width:" + ("4px")  + ";}.GFVDQLFDMN{background:" + ("#e3e3e3")  + ";}.GFVDQLFDCN{border:" + ("none")  + " !important;width:" + ("95%")  + ";}.GFVDQLFDBN{width:" + ("300px") ) + (";}.GFVDQLFDBN span{padding-right:" + ("15px")  + ";vertical-align:" + ("text-top")  + ";}.GFVDQLFDBN .spinner{margin-top:" + ("-3px")  + ";}.GFVDQLFDCO{height:" + ("32px")  + ";vertical-align:" + ("middle")  + ";display:" + ("table-cell")  + ";}.GFVDQLFDDO{font-size:" + ("smaller")  + ";font-style:" + ("italic")  + ";}.GFVDQLFDFN td div{white-space:" + ("nowrap")  + ";overflow:" + ("hidden")  + ";text-overflow:") + (("ellipsis")  + ";}.GFVDQLFDHN thead th{overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.GFVDQLFDGN,.GFVDQLFDIN:first-child td{border-top:" + ("none")  + ";}.GFVDQLFDEN{background-color:" + ("#fff")  + ";padding:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#d1d1d1")  + ";z-index:" + ("2000")  + ";}.GFVDQLFDEN .checkbox{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";}.btn-file{position:" + ("relative") ) + (";overflow:" + ("hidden")  + ";}.btn-file input[type=\"file\"]{position:" + ("absolute")  + ";top:" + ("0")  + ";left:" + ("0")  + ";min-width:" + ("100%")  + ";min-height:" + ("100%")  + ";font-size:" + ("999px")  + ";text-align:" + ("left")  + ";filter:" + ("alpha(opacity=0)")  + ";opacity:" + ("0")  + ";outline:") + (("none")  + ";background:" + ("white")  + ";cursor:" + ("inherit")  + ";display:" + ("block")  + ";}.btn-file>i+span,.btn-file>span+i{padding-right:" + ("2px")  + ";}")) : ((".GFVDQLFDLO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopLeftCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDMO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteTopRightCorner()).getTop() + "px  no-repeat")  + ";height:") + (("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDFO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomLeftCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDGO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden") ) + (";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().whiteBottomRightCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDEO{border-bottom:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:" + ("3px")  + ";width:" + ("100%")  + ";}.GFVDQLFDKO{border-top:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:" + ("3px")  + ";width:" + ("100%")  + ";}.GFVDQLFDIO{border-left:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:") + (("100%")  + ";width:" + ("3px")  + ";}.GFVDQLFDJO{border-right:" + ("1px"+ " " +"solid"+ " " +"#bbb")  + ";height:" + ("100%")  + ";width:" + ("3px")  + ";}.GFVDQLFDAO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopLeftCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px") ) + (";}.GFVDQLFDBO{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTopRightCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDKN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomLeftCorner()).getTop() + "px  no-repeat")  + ";height:") + (("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDLN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getHeight() + "px")  + ";width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottomRightCorner()).getTop() + "px  no-repeat")  + ";height:" + ("4px")  + ";width:" + ("4px")  + ";}.GFVDQLFDJN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyBottom()).getTop() + "px  repeat-x") ) + (";height:" + ("4px")  + ";width:" + ("100%")  + ";}.GFVDQLFDPN{height:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getHeight() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greyTop()).getTop() + "px  repeat-x")  + ";height:" + ("4px")  + ";width:" + ("100%")  + ";}.GFVDQLFDNN{width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideLeft()).getTop() + "px  repeat-y")  + ";height:") + (("100%")  + ";width:" + ("4px")  + ";}.GFVDQLFDON{width:" + ((CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getWidth() + "px")  + ";overflow:" + ("hidden")  + ";background:" + ("url(\"" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getSafeUri().asString() + "\") -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getLeft() + "px -" + (CommonResources_default_InlineClientBundleGenerator.this.images().greySideRight()).getTop() + "px  repeat-y")  + ";height:" + ("100%")  + ";width:" + ("4px")  + ";}.GFVDQLFDMN{background:" + ("#e3e3e3")  + ";}.GFVDQLFDCN{border:" + ("none")  + " !important;width:" + ("95%")  + ";}.GFVDQLFDBN{width:" + ("300px") ) + (";}.GFVDQLFDBN span{padding-left:" + ("15px")  + ";vertical-align:" + ("text-top")  + ";}.GFVDQLFDBN .spinner{margin-top:" + ("-3px")  + ";}.GFVDQLFDCO{height:" + ("32px")  + ";vertical-align:" + ("middle")  + ";display:" + ("table-cell")  + ";}.GFVDQLFDDO{font-size:" + ("smaller")  + ";font-style:" + ("italic")  + ";}.GFVDQLFDFN td div{white-space:" + ("nowrap")  + ";overflow:" + ("hidden")  + ";text-overflow:") + (("ellipsis")  + ";}.GFVDQLFDHN thead th{overflow:" + ("hidden")  + ";text-overflow:" + ("ellipsis")  + ";}.GFVDQLFDGN,.GFVDQLFDIN:first-child td{border-top:" + ("none")  + ";}.GFVDQLFDEN{background-color:" + ("#fff")  + ";padding:" + ("5px")  + ";border:" + ("1px"+ " " +"solid"+ " " +"#d1d1d1")  + ";z-index:" + ("2000")  + ";}.GFVDQLFDEN .checkbox{margin-top:" + ("2px")  + ";margin-bottom:" + ("2px")  + ";}.btn-file{position:" + ("relative") ) + (";overflow:" + ("hidden")  + ";}.btn-file input[type=\"file\"]{position:" + ("absolute")  + ";top:" + ("0")  + ";right:" + ("0")  + ";min-width:" + ("100%")  + ";min-height:" + ("100%")  + ";font-size:" + ("999px")  + ";text-align:" + ("right")  + ";filter:" + ("alpha(opacity=0)")  + ";opacity:" + ("0")  + ";outline:") + (("none")  + ";background:" + ("white")  + ";cursor:" + ("inherit")  + ";display:" + ("block")  + ";}.btn-file>i+span,.btn-file>span+i{padding-left:" + ("2px")  + ";}"));
      }
      public java.lang.String busyPopup() {
        return "GFVDQLFDBN";
      }
      public java.lang.String cleanTextArea() {
        return "GFVDQLFDCN";
      }
      public java.lang.String columnPickerButton() {
        return "GFVDQLFDDN";
      }
      public java.lang.String columnPickerPopup() {
        return "GFVDQLFDEN";
      }
      public java.lang.String dataGrid() {
        return "GFVDQLFDFN";
      }
      public java.lang.String dataGridContent() {
        return "GFVDQLFDGN";
      }
      public java.lang.String dataGridHeader() {
        return "GFVDQLFDHN";
      }
      public java.lang.String dataGridRow() {
        return "GFVDQLFDIN";
      }
      public java.lang.String greyBottomClass() {
        return "GFVDQLFDJN";
      }
      public java.lang.String greyBottomLeftCornerClass() {
        return "GFVDQLFDKN";
      }
      public java.lang.String greyBottomRightCornerClass() {
        return "GFVDQLFDLN";
      }
      public java.lang.String greyCenterClass() {
        return "GFVDQLFDMN";
      }
      public java.lang.String greySideLeftClass() {
        return "GFVDQLFDNN";
      }
      public java.lang.String greySideRightClass() {
        return "GFVDQLFDON";
      }
      public java.lang.String greyTopClass() {
        return "GFVDQLFDPN";
      }
      public java.lang.String greyTopLeftCornerClass() {
        return "GFVDQLFDAO";
      }
      public java.lang.String greyTopRightCornerClass() {
        return "GFVDQLFDBO";
      }
      public java.lang.String titleTextCellContainer() {
        return "GFVDQLFDCO";
      }
      public java.lang.String titleTextCellDescription() {
        return "GFVDQLFDDO";
      }
      public java.lang.String whiteBottomClass() {
        return "GFVDQLFDEO";
      }
      public java.lang.String whiteBottomLeftCornerClass() {
        return "GFVDQLFDFO";
      }
      public java.lang.String whiteBottomRightCornerClass() {
        return "GFVDQLFDGO";
      }
      public java.lang.String whiteCenterClass() {
        return "GFVDQLFDHO";
      }
      public java.lang.String whiteSideLeftClass() {
        return "GFVDQLFDIO";
      }
      public java.lang.String whiteSideRightClass() {
        return "GFVDQLFDJO";
      }
      public java.lang.String whiteTopClass() {
        return "GFVDQLFDKO";
      }
      public java.lang.String whiteTopLeftCornerClass() {
        return "GFVDQLFDLO";
      }
      public java.lang.String whiteTopRightCornerClass() {
        return "GFVDQLFDMO";
      }
    }
    ;
  }
  private static class CSSInitializer {
    static {
      _instance0.CSSInitializer();
    }
    static org.uberfire.ext.widgets.common.client.resources.CommonCss get() {
      return CSS;
    }
  }
  public org.uberfire.ext.widgets.common.client.resources.CommonCss CSS() {
    return CSSInitializer.get();
  }
  private void imagesInitializer() {
    images = com.google.gwt.core.client.GWT.create(org.uberfire.ext.widgets.common.client.resources.CommonImages.class);
  }
  private static class imagesInitializer {
    static {
      _instance0.imagesInitializer();
    }
    static org.uberfire.ext.widgets.common.client.resources.CommonImages get() {
      return images;
    }
  }
  public org.uberfire.ext.widgets.common.client.resources.CommonImages images() {
    return imagesInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static org.uberfire.ext.widgets.common.client.resources.CommonCss CSS;
  private static org.uberfire.ext.widgets.common.client.resources.CommonImages images;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      CSS(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("CSS", CSS());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'CSS': return this.@org.uberfire.ext.widgets.common.client.resources.CommonResources::CSS()();
    }
    return null;
  }-*/;
}
