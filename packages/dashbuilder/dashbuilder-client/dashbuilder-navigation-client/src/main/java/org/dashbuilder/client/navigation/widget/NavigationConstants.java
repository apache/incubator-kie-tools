package org.dashbuilder.client.navigation.widget;

public class NavigationConstants {

    private static final String NAV_GROUP_NOT_FOUND = "Navigation group not found.";
    private static final String NAV_GROUP_EMPTY = "Navigation group is empty.";
    private static final String INFINITE_RECURSION = "Infinite recursion on navigation: ";
    private static final String OPEN = "Open ";
    private static final String GO = "Go to ";
    private static final String SHOW = "Show ";

    private NavigationConstants() {
        // empty
    }

    public static String navGroupEmpty() {
        return NAV_GROUP_EMPTY;
    }

    public static String navGroupNotFound() {
        return NAV_GROUP_NOT_FOUND;
    }

    public static String infiniteRecursion(String cause) {
        return INFINITE_RECURSION + cause;
    }

    public static String open(String name) {
        return OPEN + name;
    }

    public static String go(String name) {
        return GO + name;
    }

    public static String show(String name) {
        return SHOW + name;
    }

}
