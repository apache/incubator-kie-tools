package com.google.gwt.user.cellview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ResourcePrototype;

public class CellTable_Resources_default_InlineClientBundleGenerator implements com.google.gwt.user.cellview.client.CellTable.Resources {
  private static CellTable_Resources_default_InlineClientBundleGenerator _instance0 = new CellTable_Resources_default_InlineClientBundleGenerator();
  private void cellTableFooterBackgroundInitializer() {
    cellTableFooterBackground = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellTableFooterBackground",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl : externalImage),
      0, 0, 82, 23, false, false
    );
  }
  private static class cellTableFooterBackgroundInitializer {
    static {
      _instance0.cellTableFooterBackgroundInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellTableFooterBackground;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellTableFooterBackground() {
    return cellTableFooterBackgroundInitializer.get();
  }
  private void cellTableHeaderBackgroundInitializer() {
    cellTableHeaderBackground = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellTableHeaderBackground",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl0 : externalImage0),
      0, 0, 82, 23, false, false
    );
  }
  private static class cellTableHeaderBackgroundInitializer {
    static {
      _instance0.cellTableHeaderBackgroundInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellTableHeaderBackground;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellTableHeaderBackground() {
    return cellTableHeaderBackgroundInitializer.get();
  }
  private void cellTableLoadingInitializer() {
    cellTableLoading = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellTableLoading",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl1 : externalImage1),
      0, 0, 43, 11, true, false
    );
  }
  private static class cellTableLoadingInitializer {
    static {
      _instance0.cellTableLoadingInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellTableLoading;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellTableLoading() {
    return cellTableLoadingInitializer.get();
  }
  private void cellTableSelectedBackgroundInitializer() {
    cellTableSelectedBackground = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellTableSelectedBackground",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl2 : externalImage2),
      0, 0, 82, 26, false, false
    );
  }
  private static class cellTableSelectedBackgroundInitializer {
    static {
      _instance0.cellTableSelectedBackgroundInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellTableSelectedBackground;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellTableSelectedBackground() {
    return cellTableSelectedBackgroundInitializer.get();
  }
  private void cellTableSortAscendingInitializer() {
    cellTableSortAscending = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellTableSortAscending",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl3 : externalImage3),
      0, 0, 11, 7, false, false
    );
  }
  private static class cellTableSortAscendingInitializer {
    static {
      _instance0.cellTableSortAscendingInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellTableSortAscending;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellTableSortAscending() {
    return cellTableSortAscendingInitializer.get();
  }
  private void cellTableSortDescendingInitializer() {
    cellTableSortDescending = new com.google.gwt.resources.client.impl.ImageResourcePrototype(
      "cellTableSortDescending",
      com.google.gwt.safehtml.shared.UriUtils.fromTrustedString(com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ?externalImage_rtl4 : externalImage4),
      0, 0, 11, 7, false, false
    );
  }
  private static class cellTableSortDescendingInitializer {
    static {
      _instance0.cellTableSortDescendingInitializer();
    }
    static com.google.gwt.resources.client.ImageResource get() {
      return cellTableSortDescending;
    }
  }
  public com.google.gwt.resources.client.ImageResource cellTableSortDescending() {
    return cellTableSortDescendingInitializer.get();
  }
  private void cellTableStyleInitializer() {
    cellTableStyle = new com.google.gwt.user.cellview.client.CellTable.Style() {
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
        return "cellTableStyle";
      }
      public String getText() {
        return com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale().isRTL() ? ((".GFVDQLFDPD{border-top:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("right")  + ";color:" + ("#4b4a4a")  + ";text-shadow:" + ("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDAE{border-bottom:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("right")  + ";color:" + ("#4b4a4a")  + ";text-shadow:") + (("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDJD{padding:" + ("2px"+ " " +"15px")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDOE{cursor:" + ("pointer")  + ";cursor:" + ("hand")  + ";}.GFVDQLFDOE:hover{color:" + ("#6c6b6b")  + ";}.GFVDQLFDKD{background:" + ("#fff")  + ";}.GFVDQLFDLD{border:" + ("2px"+ " " +"solid"+ " " +"#fff")  + ";}.GFVDQLFDKE{background:" + ("#f3f7fb")  + ";}.GFVDQLFDLE{border:" + ("2px"+ " " +"solid"+ " " +"#f3f7fb") ) + (";}.GFVDQLFDBE{background:" + ("#eee")  + ";}.GFVDQLFDCE{border:" + ("2px"+ " " +"solid"+ " " +"#eee")  + ";}.GFVDQLFDEE{background:" + ("#ffc")  + ";}.GFVDQLFDFE{border:" + ("2px"+ " " +"solid"+ " " +"#ffc")  + ";}.GFVDQLFDME{background:" + ("#628cd5")  + ";color:" + ("white")  + ";height:" + ("auto")  + ";overflow:" + ("auto")  + ";}.GFVDQLFDNE{border:" + ("2px"+ " " +"solid"+ " " +"#628cd5")  + ";}.GFVDQLFDDE{border:" + ("2px"+ " " +"solid"+ " " +"#d7dde8")  + ";}.GFVDQLFDJE{margin:") + (("30px")  + ";}")) : ((".GFVDQLFDPD{border-top:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("left")  + ";color:" + ("#4b4a4a")  + ";text-shadow:" + ("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDAE{border-bottom:" + ("2px"+ " " +"solid"+ " " +"#6f7277")  + ";padding:" + ("3px"+ " " +"15px")  + ";text-align:" + ("left")  + ";color:" + ("#4b4a4a")  + ";text-shadow:") + (("#ddf"+ " " +"1px"+ " " +"1px"+ " " +"0")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDJD{padding:" + ("2px"+ " " +"15px")  + ";overflow:" + ("hidden")  + ";}.GFVDQLFDOE{cursor:" + ("pointer")  + ";cursor:" + ("hand")  + ";}.GFVDQLFDOE:hover{color:" + ("#6c6b6b")  + ";}.GFVDQLFDKD{background:" + ("#fff")  + ";}.GFVDQLFDLD{border:" + ("2px"+ " " +"solid"+ " " +"#fff")  + ";}.GFVDQLFDKE{background:" + ("#f3f7fb")  + ";}.GFVDQLFDLE{border:" + ("2px"+ " " +"solid"+ " " +"#f3f7fb") ) + (";}.GFVDQLFDBE{background:" + ("#eee")  + ";}.GFVDQLFDCE{border:" + ("2px"+ " " +"solid"+ " " +"#eee")  + ";}.GFVDQLFDEE{background:" + ("#ffc")  + ";}.GFVDQLFDFE{border:" + ("2px"+ " " +"solid"+ " " +"#ffc")  + ";}.GFVDQLFDME{background:" + ("#628cd5")  + ";color:" + ("white")  + ";height:" + ("auto")  + ";overflow:" + ("auto")  + ";}.GFVDQLFDNE{border:" + ("2px"+ " " +"solid"+ " " +"#628cd5")  + ";}.GFVDQLFDDE{border:" + ("2px"+ " " +"solid"+ " " +"#d7dde8")  + ";}.GFVDQLFDJE{margin:") + (("30px")  + ";}"));
      }
      public java.lang.String cellTableCell() {
        return "GFVDQLFDJD";
      }
      public java.lang.String cellTableEvenRow() {
        return "GFVDQLFDKD";
      }
      public java.lang.String cellTableEvenRowCell() {
        return "GFVDQLFDLD";
      }
      public java.lang.String cellTableFirstColumn() {
        return "GFVDQLFDMD";
      }
      public java.lang.String cellTableFirstColumnFooter() {
        return "GFVDQLFDND";
      }
      public java.lang.String cellTableFirstColumnHeader() {
        return "GFVDQLFDOD";
      }
      public java.lang.String cellTableFooter() {
        return "GFVDQLFDPD";
      }
      public java.lang.String cellTableHeader() {
        return "GFVDQLFDAE";
      }
      public java.lang.String cellTableHoveredRow() {
        return "GFVDQLFDBE";
      }
      public java.lang.String cellTableHoveredRowCell() {
        return "GFVDQLFDCE";
      }
      public java.lang.String cellTableKeyboardSelectedCell() {
        return "GFVDQLFDDE";
      }
      public java.lang.String cellTableKeyboardSelectedRow() {
        return "GFVDQLFDEE";
      }
      public java.lang.String cellTableKeyboardSelectedRowCell() {
        return "GFVDQLFDFE";
      }
      public java.lang.String cellTableLastColumn() {
        return "GFVDQLFDGE";
      }
      public java.lang.String cellTableLastColumnFooter() {
        return "GFVDQLFDHE";
      }
      public java.lang.String cellTableLastColumnHeader() {
        return "GFVDQLFDIE";
      }
      public java.lang.String cellTableLoading() {
        return "GFVDQLFDJE";
      }
      public java.lang.String cellTableOddRow() {
        return "GFVDQLFDKE";
      }
      public java.lang.String cellTableOddRowCell() {
        return "GFVDQLFDLE";
      }
      public java.lang.String cellTableSelectedRow() {
        return "GFVDQLFDME";
      }
      public java.lang.String cellTableSelectedRowCell() {
        return "GFVDQLFDNE";
      }
      public java.lang.String cellTableSortableHeader() {
        return "GFVDQLFDOE";
      }
      public java.lang.String cellTableSortedHeaderAscending() {
        return "GFVDQLFDPE";
      }
      public java.lang.String cellTableSortedHeaderDescending() {
        return "GFVDQLFDAF";
      }
      public java.lang.String cellTableWidget() {
        return "GFVDQLFDBF";
      }
    }
    ;
  }
  private static class cellTableStyleInitializer {
    static {
      _instance0.cellTableStyleInitializer();
    }
    static com.google.gwt.user.cellview.client.CellTable.Style get() {
      return cellTableStyle;
    }
  }
  public com.google.gwt.user.cellview.client.CellTable.Style cellTableStyle() {
    return cellTableStyleInitializer.get();
  }
  private static java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype> resourceMap;
  private static final java.lang.String externalImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFIAAAAXCAYAAACYuRhEAAAAj0lEQVR4Xu3EWwrCQBQE0d7/ekQEUUQEEQXjgxiMISI+cAW5M/los4f2swtOge4vof32NB2aYaZD/elpOlTvnqZD+co0Hc7PTNPh+Mg0HYphpsP+nmk67NpE02HbJJoOm1vQdFjXiabD6ho0HZZV0HRYXIKmw7wMmg6zsqPpMD0FTYfJMNNhfOhoOoyKoOl+PTDH5SLwRl0AAAAASUVORK5CYII=";
  private static final java.lang.String externalImage_rtl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFIAAAAXCAYAAACYuRhEAAAAj0lEQVR4Xu3EWwrCQBQE0d7/ekQEUUQEEQXjgxiMISI+cAW5M/los4f2swtOge4vof32NB2aYaZD/elpOlTvnqZD+co0Hc7PTNPh+Mg0HYphpsP+nmk67NpE02HbJJoOm1vQdFjXiabD6ho0HZZV0HRYXIKmw7wMmg6zsqPpMD0FTYfJMNNhfOhoOoyKoOl+PTDH5SLwRl0AAAAASUVORK5CYII=";
  private static final java.lang.String externalImage0 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFIAAAAXCAYAAACYuRhEAAAAj0lEQVR4Xu3EWwrCQBQE0d7/ekQEUUQEEQXjgxiMISI+cAW5M/los4f2swtOge4vof32NB2aYaZD/elpOlTvnqZD+co0Hc7PTNPh+Mg0HYphpsP+nmk67NpE02HbJJoOm1vQdFjXiabD6ho0HZZV0HRYXIKmw7wMmg6zsqPpMD0FTYfJMNNhfOhoOoyKoOl+PTDH5SLwRl0AAAAASUVORK5CYII=";
  private static final java.lang.String externalImage_rtl0 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFIAAAAXCAYAAACYuRhEAAAAj0lEQVR4Xu3EWwrCQBQE0d7/ekQEUUQEEQXjgxiMISI+cAW5M/los4f2swtOge4vof32NB2aYaZD/elpOlTvnqZD+co0Hc7PTNPh+Mg0HYphpsP+nmk67NpE02HbJJoOm1vQdFjXiabD6ho0HZZV0HRYXIKmw7wMmg6zsqPpMD0FTYfJMNNhfOhoOoyKoOl+PTDH5SLwRl0AAAAASUVORK5CYII=";
  private static final java.lang.String externalImage1 = "data:image/gif;base64,R0lGODlhKwALAPEAAP///0tKSqampktKSiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAKwALAAACMoSOCMuW2diD88UKG95W88uF4DaGWFmhZid93pq+pwxnLUnXh8ou+sSz+T64oCAyTBUAACH5BAkKAAAALAAAAAArAAsAAAI9xI4IyyAPYWOxmoTHrHzzmGHe94xkmJifyqFKQ0pwLLgHa82xrekkDrIBZRQab1jyfY7KTtPimixiUsevAAAh+QQJCgAAACwAAAAAKwALAAACPYSOCMswD2FjqZpqW9xv4g8KE7d54XmMpNSgqLoOpgvC60xjNonnyc7p+VKamKw1zDCMR8rp8pksYlKorgAAIfkECQoAAAAsAAAAACsACwAAAkCEjgjLltnYmJS6Bxt+sfq5ZUyoNJ9HHlEqdCfFrqn7DrE2m7Wdj/2y45FkQ13t5itKdshFExC8YCLOEBX6AhQAADsAAAAAAAAAAAA=";
  private static final java.lang.String externalImage_rtl1 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACsAAAALCAYAAADm8XT2AAAAg0lEQVR4XpWNwQ2AMBDD2H+QrgnkR61gOEv+Ocpx3Ky1zi/TvcG2yc0Tts3f4TYAbExuAxtzFhfYmNwGNuYsLrAxuQ1szFlcYGNyG9iYs7jAxuQ2sDFncYGNyW1gY87iAhuT28DGnMUFNia3gY05iwtsTG4DG/P3AB8bbJvcPGHbTHcBicgIrKLoecUAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage2 = "data:image/png;base64,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";
  private static final java.lang.String externalImage_rtl2 = "data:image/png;base64,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";
  private static final java.lang.String externalImage3 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAjUlEQVR4XmNgwALyKrumFRf3iKCLY4D8yq4qoOL/eRWd29HlUEBeeYdNXmXnfzCuAON6dDVgALI2t6LzMUhRLkghRNO/vKpOR3S1DLmVnTsgCrqgGGp6Zeez/KpWcbhCoO4mJEls+EBCfT0HQ15pl2pueedZoNUXQDRQ4jKQfTkPhCvB+HxuRdd1oM0hAPwcZIjP6ejiAAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage_rtl3 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAkElEQVR4XmNgQAPFxT0ieZVd09DFsYK8is7tQMX/8yu7qtDlUABQYT0Q/8+rhOLyDht0NWCQV9XpCFTwD6QoF6ShAkw/BjkLRWF+Vas4UNEzhKldUAzUUNm5A64wob6eAyh4AG41MoZprupsAisG6gzJrei6DhQ8D8SXgQouA62/DGLnlneeBbIvgOi80i5VAJLDZIiu4qm5AAAAAElFTkSuQmCC";
  private static final java.lang.String externalImage4 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAj0lEQVR4XmPIrewMya3oup5X2XkeiC/nVXRezgViEDu3vPMskH0BROeVdqkyJNTXcwAlDgDxfwxcAaWrOpsYYCC/qlUcKPgMLlnZBcWd/4E274ArhAGgbkeg5D+wApCmCjD9uLi4RwRdLRgAFdQjTAfi8g4bdDUoAKh4O8gJ+ZVdVehyGABkLVDxNHRxEAAAkCpkiGG1rZcAAAAASUVORK5CYII=";
  private static final java.lang.String externalImage_rtl4 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAsAAAAHCAYAAADebrddAAAAjklEQVR4XmPIK+1SzS3vPJtb0XkBROdVdl4Gsi/ngXAlGJ/Prei6nlvZGcIAAnlVnU1Awf9ABf/BNCY+kFBfzwFWDAJAnTsgEl1QDNf8LL+qVRyuEASKi3tEgNY/BinIRdjwD2irI4pCGMgr77CBWw3SUNFZj64GBeRXdlWBnVHRuR1dDisAKp4Gcha6OAD5g2SIhySSvQAAAABJRU5ErkJggg==";
  private static com.google.gwt.resources.client.ImageResource cellTableFooterBackground;
  private static com.google.gwt.resources.client.ImageResource cellTableHeaderBackground;
  private static com.google.gwt.resources.client.ImageResource cellTableLoading;
  private static com.google.gwt.resources.client.ImageResource cellTableSelectedBackground;
  private static com.google.gwt.resources.client.ImageResource cellTableSortAscending;
  private static com.google.gwt.resources.client.ImageResource cellTableSortDescending;
  private static com.google.gwt.user.cellview.client.CellTable.Style cellTableStyle;
  
  public ResourcePrototype[] getResources() {
    return new ResourcePrototype[] {
      cellTableFooterBackground(), 
      cellTableHeaderBackground(), 
      cellTableLoading(), 
      cellTableSelectedBackground(), 
      cellTableSortAscending(), 
      cellTableSortDescending(), 
      cellTableStyle(), 
    };
  }
  public ResourcePrototype getResource(String name) {
    if (GWT.isScript()) {
      return getResourceNative(name);
    } else {
      if (resourceMap == null) {
        resourceMap = new java.util.HashMap<java.lang.String, com.google.gwt.resources.client.ResourcePrototype>();
        resourceMap.put("cellTableFooterBackground", cellTableFooterBackground());
        resourceMap.put("cellTableHeaderBackground", cellTableHeaderBackground());
        resourceMap.put("cellTableLoading", cellTableLoading());
        resourceMap.put("cellTableSelectedBackground", cellTableSelectedBackground());
        resourceMap.put("cellTableSortAscending", cellTableSortAscending());
        resourceMap.put("cellTableSortDescending", cellTableSortDescending());
        resourceMap.put("cellTableStyle", cellTableStyle());
      }
      return resourceMap.get(name);
    }
  }
  private native ResourcePrototype getResourceNative(String name) /*-{
    switch (name) {
      case 'cellTableFooterBackground': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableFooterBackground()();
      case 'cellTableHeaderBackground': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableHeaderBackground()();
      case 'cellTableLoading': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableLoading()();
      case 'cellTableSelectedBackground': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableSelectedBackground()();
      case 'cellTableSortAscending': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableSortAscending()();
      case 'cellTableSortDescending': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableSortDescending()();
      case 'cellTableStyle': return this.@com.google.gwt.user.cellview.client.CellTable.Resources::cellTableStyle()();
    }
    return null;
  }-*/;
}
