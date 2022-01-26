PopoverTextCell.prototype.m_initPopover__java_lang_String__java_lang_String_$p_org_uberfire_ext_widgets_common_client_tables_PopoverTextCell = function (id, placement) {
        var jQueryId = '#' + id;
        var div = jQuery(jQueryId);

        div.popover({
            trigger: 'manual',
            placement: placement,
            content: function () {
                var offsetWidth = document.getElementById(id).offsetWidth;
                var scrollWidth = document.getElementById(id).scrollWidth;
                return offsetWidth < scrollWidth ? div.html() : "";
            },
            container: 'body'
        });
}