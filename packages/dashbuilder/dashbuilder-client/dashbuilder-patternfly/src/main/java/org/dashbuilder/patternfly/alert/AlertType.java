package org.dashbuilder.patternfly.alert;

public enum AlertType {

    INFO("pf-m-info", "fa-info-circle"),
    WARNING("pf-m-warning", "fa-exclamation-triangle"),
    SUCCESS("pf-m-success", "fa-check-circle"),
    ERROR("pf-m-danger", "fa-exclamation-circle");

    private String className;
    private String icon;

    AlertType(String className, String icon) {
        this.className = className;
        this.icon = icon;
    }

    public String getClassName() {
        return className;
    }

    public String getIcon() {
        return icon;
    }
}