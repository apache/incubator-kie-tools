package com.google.gwt.user.cellview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class DataGrid_Resources_default_InlineClientBundleGenerator implements com.google.gwt.user.cellview.client.DataGrid.Resources {
  private static DataGrid_Resources_default_InlineClientBundleGenerator _instance0 = new DataGrid_Resources_default_InlineClientBundleGenerator();
  private void dataGridLoadingInitializer() {
    dataGridLoading = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "dataGridLoading",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl : externalImage),
      0, 0, 43, 11, true, false
    );
  }
  private static class dataGridLoadingInitializer {
    static {
      _instance0.dataGridLoadingInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return dataGridLoading;
    }
  }
  public com.google.gwt.resources.client.ImageResource dataGridLoading() {
    return dataGridLoadingInitializer.get();
  }
  private void dataGridSortAscendingInitializer() {
    dataGridSortAscending = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "dataGridSortAscending",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl0 : externalImage0),
      0, 0, 11, 7, false, false
    );
  }
  private static class dataGridSortAscendingInitializer {
    static {
      _instance0.dataGridSortAscendingInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return dataGridSortAscending;
    }
  }
  public com.google.gwt.resources.client.ImageResource dataGridSortAscending() {
    return dataGridSortAscendingInitializer.get();
  }
  private void dataGridSortDescendingInitializer() {
    dataGridSortDescending = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "dataGridSortDescending",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl1 : externalImage1),
      0, 0, 11, 7, false, false
    );
  }
  private static class dataGridSortDescendingInitializer {
    static {
      _instance0.dataGridSortDescendingInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return dataGridSortDescending;
    }
  }
  public com.google.gwt.resources.client.ImageResource dataGridSortDescending() {
    return dataGridSortDescendingInitializer.get();
  }
  private void dataGridStyleInitializer() {
    dataGridStyle = new com.google.gwt.user.cellview.client.DataGrid.Style() {
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
        return "dataGridStyle";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDCH{border-top:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("right")  + ";color:" + ("#4b4a4a")  + ";text-shadow:" + ("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";white-space:" + ("nowrap")  + ";}.GFVDQLFDDH{border-bottom:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("right")  + ";color:") + (("#4b4a4a")  + ";text-shadow:" + ("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";white-space:" + ("nowrap")  + ";}.GFVDQLFDMG{padding:" + ("2px"+ " " +"15px")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDAI{cursor:" + ("pointer")  + ";cursor:" + ("hand")  + ";}.GFVDQLFDAI:hover{color:" + ("#6c6b6b")  + ";}.GFVDQLFDNG{background:" + ("#fff")  + ";}.GFVDQLFDOG{border:" + ("2px"+ " " +"solid"+ " " +"#fff") ) + (";}.GFVDQLFDMH{background:" + ("#f3f7fb")  + ";}.GFVDQLFDNH{border:" + ("2px"+ " " +"solid"+ " " +"#f3f7fb")  + ";}.GFVDQLFDEH{background:" + ("#eee")  + ";}.GFVDQLFDFH{border:" + ("2px"+ " " +"solid"+ " " +"#eee")  + ";}.GFVDQLFDHH{background:" + ("#ffc")  + ";}.GFVDQLFDIH{border:" + ("2px"+ " " +"solid"+ " " +"#ffc")  + ";}.GFVDQLFDOH{background:" + ("#628cd5")  + ";color:" + ("white")  + ";height:" + ("auto")  + ";overflow:" + ("auto")  + ";}.GFVDQLFDPH{border:") + (("2px"+ " " +"solid"+ " " +"#628cd5")  + ";}.GFVDQLFDGH{border:" + ("2px"+ " " +"solid"+ " " +"#d7dde8")  + ";}")) : ((".GFVDQLFDCH{border-top:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("left")  + ";color:" + ("#4b4a4a")  + ";text-shadow:" + ("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";white-space:" + ("nowrap")  + ";}.GFVDQLFDDH{border-bottom:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("left")  + ";color:") + (("#4b4a4a")  + ";text-shadow:" + ("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";white-space:" + ("nowrap")  + ";}.GFVDQLFDMG{padding:" + ("2px"+ " " +"15px")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDAI{cursor:" + ("pointer")  + ";cursor:" + ("hand")  + ";}.GFVDQLFDAI:hover{color:" + ("#6c6b6b")  + ";}.GFVDQLFDNG{background:" + ("#fff")  + ";}.GFVDQLFDOG{border:" + ("2px"+ " " +"solid"+ " " +"#fff") ) + (";}.GFVDQLFDMH{background:" + ("#f3f7fb")  + ";}.GFVDQLFDNH{border:" + ("2px"+ " " +"solid"+ " " +"#f3f7fb")  + ";}.GFVDQLFDEH{background:" + ("#eee")  + ";}.GFVDQLFDFH{border:" + ("2px"+ " " +"solid"+ " " +"#eee")  + ";}.GFVDQLFDHH{background:" + ("#ffc")  + ";}.GFVDQLFDIH{border:" + ("2px"+ " " +"solid"+ " " +"#ffc")  + ";}.GFVDQLFDOH{background:" + ("#628cd5")  + ";color:" + ("white")  + ";height:" + ("auto")  + ";overflow:" + ("auto")  + ";}.GFVDQLFDPH{border:") + (("2px"+ " " +"solid"+ " " +"#628cd5")  + ";}.GFVDQLFDGH{border:" + ("2px"+ " " +"solid"+ " " +"#d7dde8")  + ";}"));
      }
      public java.lang.String dataGridCell() {
        return "GFVDQLFDMG";
      }
      public java.lang.String dataGridEvenRow() {
        return "GFVDQLFDNG";
      }
      public java.lang.String dataGridEvenRowCell() {
        return "GFVDQLFDOG";
      }
      public java.lang.String dataGridFirstColumn() {
        return "GFVDQLFDPG";
      }
      public java.lang.String dataGridFirstColumnFooter() {
        return "GFVDQLFDAH";
      }
      public java.lang.String dataGridFirstColumnHeader() {
        return "GFVDQLFDBH";
      }
      public java.lang.String dataGridFooter() {
        return "GFVDQLFDCH";
      }
      public java.lang.String dataGridHeader() {
        return "GFVDQLFDDH";
      }
      public java.lang.String dataGridHoveredRow() {
        return "GFVDQLFDEH";
      }
      public java.lang.String dataGridHoveredRowCell() {
        return "GFVDQLFDFH";
      }
      public java.lang.String dataGridKeyboardSelectedCell() {
        return "GFVDQLFDGH";
      }
      public java.lang.String dataGridKeyboardSelectedRow() {
        return "GFVDQLFDHH";
      }
      public java.lang.String dataGridKeyboardSelectedRowCell() {
        return "GFVDQLFDIH";
      }
      public java.lang.String dataGridLastColumn() {
        return "GFVDQLFDJH";
      }
      public java.lang.String dataGridLastColumnFooter() {
        return "GFVDQLFDKH";
      }
      public java.lang.String dataGridLastColumnHeader() {
        return "GFVDQLFDLH";
      }
      public java.lang.String dataGridOddRow() {
        return "GFVDQLFDMH";
      }
      public java.lang.String dataGridOddRowCell() {
        return "GFVDQLFDNH";
      }
      public java.lang.String dataGridSelectedRow() {
        return "GFVDQLFDOH";
      }
      public java.lang.String dataGridSelectedRowCell() {
        return "GFVDQLFDPH";
      }
      public java.lang.String dataGridSortableHeader() {
        return "GFVDQLFDAI";
      }
      public java.lang.String dataGridSortedHeaderAscending() {
        return "GFVDQLFDBI";
      }
      public java.lang.String dataGridSortedHeaderDescending() {
        return "GFVDQLFDCI";
      }
      public java.lang.String dataGridWidget() {
        return "GFVDQLFDDI";
      }
    }
    ;
  }
  private static class dataGridStyleInitializer {
    static {
      _instance0.dataGridStyleInitializer();
    }
    static com.google.gwt.user.cellview.client.DataGrid.Style get() {
      return dataGridStyle;
    }
  }
  public com.google.gwt.user.cellview.client.DataGrid.Style dataGridStyle() {
    return dataGridStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String externalImage = "data:image/gif;base64,R0lGODlhKwALAPEAAP///0tKSqampktKSiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAKwALAAACMoSOCMuW2diD88UKG95W88uF4DaGWFmhZid93pq+pwxnLUnXh8ou+sSz+T64oCAyTBUAACH5BAkKAAAALAAAAAArAAsAAAI9xI4IyyAPYWOxmoTHrHzzmGHe94xkmJifyqFKQ0pwLLgHa82xrekkDrIBZRQab1jyfY7KTtPimixiUsevAAAh+QQJCgAAACwAAAAAKwALAAACPYSOCMswD2FjqZpqW9xv4g8KE7d54XmMpNSgqLoOpgvC60xjNonnyc7p+VKamKw1zDCMR8rp8pksYlKorgAAIfkECQoAAAAsAAAAACsACwAAAkCEjgjLltnYmJS6Bxt+sfq5ZUyoNJ9HHlEqdCfFrqn7DrE2m7Wdj/2y45FkQ13t5itKdshFExC8YCLOEBX6AhQAADsAAAAAAAAAAAA=";
  private static final java.lang.String externalImage_rtl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACsAAAALCAYAAADm8XT2AAAAg0lEQVR4XpWNwQ2AMBDD2H+QrgnkR61gOEv+Ocpx3Ky1zi/TvcG2yc0Tts3f4TYAbExuAxtzFhfYmNwGNuYsLrAxuQ1szFlcYGNyG9iYs7jAxuQ2sDFncYGNyW1gY87iAhuT28DGnMUFNia3gY05iwtsTG4DG/P3AB8bbJvcPGHbTHcBicgIrKLoecUAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage0 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAjUlEQVR4XmNgwALyKrumFRf3iKCLY4D8yq4qoOL/eRWd29HlUEBeeYdNXmXnfzCuAON6dDVgALI2t6LzMUhRLkghRNO/vKpOR3S1DLmVnTsgCrqgGGp6Zeez/KpWcbhCoO4mJEls+EBCfT0HQ15pl2pueedZoNUXQDRQ4jKQfTkPhCvB+HxuRdd1oM0hAPwcZIjP6ejiAAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage_rtl0 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAkElEQVR4XmNgQAPFxT0ieZVd09DFsYK8is7tQMX/8yu7qtDlUABQYT0Q/8+rhOLyDht0NWCQV9XpCFTwD6QoF6ShAkw/BjkLRWF+Vas4UNEzhKldUAzUUNm5A64wob6eAyh4AG41MoZprupsAisG6gzJrei6DhQ8D8SXgQouA62/DGLnlneeBbIvgOi80i5VAJLDZIiu4qm5AAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage1 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAj0lEQVR4XmPIrewMya3oup5X2XkeiC/nVXRezgViEDu3vPMskH0BROeVdqkyJNTXcwAlDgDxfwxcAaWrOpsYYCC/qlUcKPgMLlnZBcWd/4E274ArhAGgbkeg5D+wApCmCjD9uLi4RwRdLRgAFdQjTAfi8g4bdDUoAKh4O8gJ+ZVdVehyGABkLVDxNHRxEAAAkCpkiGG1rZcAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage_rtl1 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAjklEQVR4XmPIK+1SzS3vPJtb0XkBROdVdl4Gsi/ngXAlGJ/Prei6nlvZGcIAAnlVnU1Awf9ABf/BNCY+kFBfzwFWDAJAnTsgEl1QDNf8LL+qVRyuEASKi3tEgNY/BinIRdjwD2irI4pCGMgr77CBWw3SUNFZj64GBeRXdlWBnVHRuR1dDisAKp4Gcha6OAD5g2SIhySSvQAAAABJRU5ErkJggg==";
  private static com.google.gwt.resources.client.ImageResource dataGridLoading;
  private static com.google.gwt.resources.client.ImageResource dataGridSortAscending;
  private static com.google.gwt.resources.client.ImageResource dataGridSortDescending;
  private static com.google.gwt.user.cellview.client.DataGrid.Style dataGridStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      dataGridLoading(), 
      dataGridSortAscending(), 
      dataGridSortDescending(), 
      dataGridStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("dataGridLoading", dataGridLoading());
        resourceMap.put("dataGridSortAscending", dataGridSortAscending());
        resourceMap.put("dataGridSortDescending", dataGridSortDescending());
        resourceMap.put("dataGridStyle", dataGridStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'dataGridLoading': return this.@com.google.gwt.user.cellview.client.DataGrid.Resources::dataGridLoading()();
      case 'dataGridSortAscending': return this.@com.google.gwt.user.cellview.client.DataGrid.Resources::dataGridSortAscending()();
      case 'dataGridSortDescending': return this.@com.google.gwt.user.cellview.client.DataGrid.Resources::dataGridSortDescending()();
      case 'dataGridStyle': return this.@com.google.gwt.user.cellview.client.DataGrid.Resources::dataGridStyle()();
    }
    return null;
  }-*/;
}
