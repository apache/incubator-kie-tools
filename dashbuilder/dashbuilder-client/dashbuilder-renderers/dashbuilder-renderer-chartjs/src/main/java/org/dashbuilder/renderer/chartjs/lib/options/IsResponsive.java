package org.dashbuilder.renderer.chartjs.lib.options;

public interface IsResponsive {

    final String RESPONSIVE = "responsive";
    final String MAINTAIN_ASPECT_RATIO = "maintainAspectRatio";

    public void setResponsive(boolean responsive);

    public void setMaintainAspectRatio(boolean aspectRatio);

}
