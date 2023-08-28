package org.dashbuilder.client.navigation.widget;

import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.navigation.layout.LayoutRecursionIssueI18n;

public interface ClientLayoutRecursionIssueI18n extends LayoutRecursionIssueI18n {

    default String navRefPerspectiveI18n(String name) {
        return NavigationConstants.INSTANCE.navRefPerspective(name);
    }

    default String navRefPerspectiveFoundI18n(String name) {
        return NavigationConstants.INSTANCE.navRefPerspectiveFound(name);
    }

    default String navRefPerspectiveDefaultI18n(String name) {
        return NavigationConstants.INSTANCE.navRefPerspectiveDefault(name);
    }

    default String navRefPerspectiveInGroupI18n(String name) {
        return NavigationConstants.INSTANCE.navRefPerspectiveInGroup(name);
    }

    default String navRefComponentI18n(String name) {
        return NavigationConstants.INSTANCE.navRefComponent(name);
    }

    default String navRefGroupDefinedI18n(String name) {
        return NavigationConstants.INSTANCE.navRefGroupDefined(name);
    }

    default String navRefGroupContextI18n(String name) {
        return NavigationConstants.INSTANCE.navRefGroupContext(name);
    }

    default String navRefDefaultItemDefinedI18n(String name) {
        return NavigationConstants.INSTANCE.navRefDefaultItemDefined(name);
    }

    default String navRefDefaultItemFoundI18n(String name) {
        return NavigationConstants.INSTANCE.navRefDefaultItemFound(name);
    }

    default String navRefPerspectiveRecursionEndI18n() {
        return NavigationConstants.INSTANCE.navRefPerspectiveRecursionEnd();
    }

    default String navMenubarDragComponentI18n() {
        return NavigationConstants.INSTANCE.navMenubarDragComponent();
    }

    default String navTreeDragComponentI18n() {
        return NavigationConstants.INSTANCE.navTreeDragComponent();
    }

    default String navTilesDragComponentI18n() {
        return NavigationConstants.INSTANCE.navTilesDragComponent();
    }

    default String navTabListDragComponentI18n() {
        return NavigationConstants.INSTANCE.navTabListDragComponent();
    }

    default String navCarouselDragComponentI18n() {
        return NavigationConstants.INSTANCE.navCarouselDragComponent();
    }

}
