package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.BoundingBoxPathClipper;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPathClipper;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.PathPartListPathClipper;
import com.ait.lienzo.client.core.shape.Polygon;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Star;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.DragMode;
import com.google.gwt.dom.client.Style.Display;
import elemental2.dom.CSSProperties;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;

public class LionExample extends BaseExample implements Example {

    private IPathClipper m_bbox_clip;

    private IPathClipper m_star_clip;

    private HTMLButtonElement m_doclip = (HTMLButtonElement) DomGlobal.document.createElement("button");

    private HTMLButtonElement m_dostar = (HTMLButtonElement) DomGlobal.document.createElement("button");

    public LionExample(final String title) {
        super(title);
    }

    @Override
    public void init(final LienzoPanel panel, final HTMLDivElement topDiv) {
        super.init(panel, topDiv);
        topDiv.style.display = Display.INLINE_BLOCK.getCssName();

        m_doclip = (HTMLButtonElement) DomGlobal.document.createElement("button");
        m_doclip.textContent = "Rect Off";

        m_dostar = (HTMLButtonElement) DomGlobal.document.createElement("button");
        m_dostar.textContent = "Star Off";

        topDiv.appendChild(m_doclip);
        topDiv.appendChild(m_dostar);

        heightOffset = 30;
    }

    @Override
    public void destroy() {
        super.destroy();

        m_doclip.remove();
        m_dostar.remove();
    }

    @Override
    public void run() {
        final Layer boxes = new Layer();

        final Rectangle rect = new Rectangle(352, 352).setX(124).setY(124).setStrokeColor(ColorName.BLACK).setStrokeWidth(3).setListening(false).setVisible(false);

        final Star star = new Star(5, 150, 250).setX(300).setY(325).setStrokeColor(ColorName.BLACK).setStrokeWidth(3).setListening(false).setVisible(false);

        final Group lion = new Group().setX(-100).setY(0).setDraggable(true).setDragMode(DragMode.SAME_LAYER);

        final PathPartListPathClipper path = new PathPartListPathClipper(star);
        path.setX(300);
        path.setY(325);
        m_star_clip = path;
        m_star_clip.setActive(false);

        layer.setPathClipper(new BoundingBoxPathClipper(BoundingBox.fromDoubles(125, 125, 475, 475)));
        m_bbox_clip = layer.getPathClipper();
        m_bbox_clip.setActive(false);

        m_doclip.name = "Rect Off";
        m_doclip.onclick = (e) -> {
            if (m_star_clip.isActive()) {
                m_star_clip.setActive(false);

                star.setVisible(false);

                m_dostar.textContent = "Star Off";
            }
            if (m_bbox_clip.isActive()) {
                m_bbox_clip.setActive(false);

                rect.setVisible(false);

                m_doclip.textContent = "Rect Off";
            } else {
                layer.setPathClipper(m_bbox_clip);

                m_bbox_clip.setActive(true);

                rect.setVisible(true);

                m_doclip.textContent = "Rect On";
            }
            layer.batch();

            boxes.batch();
            return null;
        };
        m_doclip.style.width = CSSProperties.WidthUnionType.of("90px");

        m_dostar.onclick = (e) -> {
            if (m_bbox_clip.isActive()) {
                m_bbox_clip.setActive(false);

                rect.setVisible(false);

                m_doclip.textContent = "Rect Off";
            }
            if (m_star_clip.isActive()) {
                m_star_clip.setActive(false);

                star.setVisible(false);

                m_dostar.textContent = "Star Off";
            } else {
                layer.setPathClipper(m_star_clip);

                m_star_clip.setActive(true);

                star.setVisible(true);

                m_dostar.textContent = "Star On";
            }
            layer.batch();

            boxes.batch();
            return null;
        };
        m_dostar.style.width = CSSProperties.WidthUnionType.of("90px");

        lion.add(new Polygon(392, 85, 380, 128, 339, 98).setFillColor("#FADFAA"));
        lion.add(new Polygon(392, 85, 380, 128, 412, 111).setFillColor("#EABA8C"));
        lion.add(new Polygon(339, 98, 380, 128, 340, 140).setFillColor("#FAD398"));
        lion.add(new Polygon(339, 98, 340, 140, 309, 142).setFillColor("#DFA387"));
        lion.add(new Polygon(339, 98, 309, 142, 286, 133).setFillColor("#F9D8AD"));
        lion.add(new Polygon(392, 85, 412, 111, 443, 101).setFillColor("#DBB08E"));
        lion.add(new Polygon(443, 101, 412, 111, 434, 126).setFillColor("#D59F7D"));
        lion.add(new Polygon(443, 101, 434, 126, 475, 122).setFillColor("#FACC91"));
        lion.add(new Polygon(412, 111, 380, 128, 402, 132).setFillColor("#CE8670"));
        lion.add(new Polygon(412, 111, 402, 132, 418, 142).setFillColor("#BC716C"));
        lion.add(new Polygon(309, 142, 340, 140, 309, 185).setFillColor("#BC716C"));
        lion.add(new Polygon(412, 111, 434, 126, 418, 142).setFillColor("#D1806D"));
        lion.add(new Polygon(434, 126, 475, 122, 462, 137).setFillColor("#F8DC9B"));
        lion.add(new Polygon(475, 122, 462, 137, 509, 139).setFillColor("#FAD295"));
        lion.add(new Polygon(434, 126, 462, 137, 425, 155).setFillColor("#DC8C6B"));
        lion.add(new Polygon(434, 126, 425, 155, 418, 142).setFillColor("#EC9B6C"));
        lion.add(new Polygon(380, 128, 402, 132, 369, 156).setFillColor("#E49C76"));
        lion.add(new Polygon(380, 128, 369, 156, 340, 140).setFillColor("#DD8D76"));
        lion.add(new Polygon(402, 132, 396, 168, 369, 156).setFillColor("#DB8A6F"));
        lion.add(new Polygon(462, 137, 509, 139, 491, 171).setFillColor("#FBC27F"));
        lion.add(new Polygon(425, 155, 462, 137, 461, 173).setFillColor("#CE7660"));
        lion.add(new Polygon(340, 140, 327, 193, 309, 185).setFillColor("#BF5D76"));
        lion.add(new Polygon(402, 132, 418, 142, 396, 168).setFillColor("#A8526D"));
        lion.add(new Polygon(286, 133, 309, 142, 292, 179).setFillColor("#F6B78B"));
        lion.add(new Polygon(509, 139, 491, 171, 542, 154).setFillColor("#F7CB8C"));
        lion.add(new Polygon(542, 154, 491, 171, 520, 192).setFillColor("#D99860"));
        lion.add(new Polygon(418, 142, 410, 172, 425, 155).setFillColor("#D4846D"));
        lion.add(new Polygon(410, 172, 425, 155, 431, 188).setFillColor("#EFA872"));
        lion.add(new Polygon(425, 155, 460, 187, 461, 173).setFillColor("#954A5E"));
        lion.add(new Polygon(369, 156, 340, 140, 344, 188).setFillColor("#C46374"));
        lion.add(new Polygon(286, 133, 292, 179, 258, 161).setFillColor("#B95E7F"));
        lion.add(new Polygon(258, 161, 261, 177, 255, 195).setFillColor("#944F8B"));
        lion.add(new Polygon(418, 142, 396, 168, 410, 172).setFillColor("#B45E69"));
        lion.add(new Polygon(462, 137, 461, 173, 491, 171).setFillColor("#D7835F"));
        lion.add(new Polygon(491, 171, 482, 192, 520, 192).setFillColor("#BD6D56"));
        lion.add(new Polygon(461, 173, 491, 171, 482, 192).setFillColor("#B05D59"));
        lion.add(new Polygon(461, 173, 482, 192, 460, 187).setFillColor("#82365A"));
        lion.add(new Polygon(258, 161, 292, 179, 261, 177).setFillColor("#9C4083"));
        lion.add(new Polygon(309, 142, 292, 179, 309, 185).setFillColor("#B75D79"));
        lion.add(new Polygon(369, 156, 362, 192, 380, 183).setFillColor("#F9CB8D"));
        lion.add(new Polygon(292, 179, 301, 203, 278, 191).setFillColor("#C86E78"));
        lion.add(new Polygon(261, 177, 292, 179, 278, 191).setFillColor("#EA9E86"));
        lion.add(new Polygon(292, 179, 309, 185, 301, 203).setFillColor("#AF5078"));
        lion.add(new Polygon(369, 156, 344, 188, 362, 192).setFillColor("#D59071"));
        lion.add(new Polygon(431, 188, 425, 155, 460, 187).setFillColor("#F9CD90"));
        lion.add(new Polygon(340, 140, 344, 188, 327, 193).setFillColor("#AD4F74"));
        lion.add(new Polygon(380, 183, 380, 206, 388, 187).setFillColor("#E0A072"));
        lion.add(new Polygon(344, 188, 322, 223, 342, 208).setFillColor("#E8AA7D"));
        lion.add(new Polygon(380, 183, 388, 187, 396, 168).setFillColor("#ECB984"));
        lion.add(new Polygon(388, 187, 408, 193, 396, 168).setFillColor("#F9D49D"));
        lion.add(new Polygon(380, 183, 396, 168, 369, 156).setFillColor("#F9D49D"));
        lion.add(new Polygon(261, 177, 278, 191, 255, 195).setFillColor("#EDAD87"));
        lion.add(new Polygon(278, 191, 277, 203, 301, 203).setFillColor("#A55079"));
        lion.add(new Polygon(380, 206, 388, 187, 408, 193).setFillColor("#F3BB7E"));
        lion.add(new Polygon(431, 188, 460, 187, 464, 208).setFillColor("#F7BC76"));
        lion.add(new Polygon(344, 188, 362, 192, 342, 208).setFillColor("#CC8571"));
        lion.add(new Polygon(362, 192, 380, 183, 380, 206).setFillColor("#F7C185"));
        lion.add(new Polygon(460, 187, 490, 213, 482, 192).setFillColor("#8A4256"));
        lion.add(new Polygon(362, 192, 380, 206, 361, 205).setFillColor("#DC9D72"));
        lion.add(new Polygon(327, 193, 344, 188, 322, 223).setFillColor("#C9766E"));
        lion.add(new Polygon(327, 193, 322, 223, 306, 217).setFillColor("#A35370"));
        lion.add(new Polygon(255, 195, 278, 191, 277, 203).setFillColor("#B4607A"));
        lion.add(new Polygon(255, 195, 277, 203, 252, 222).setFillColor("#A5497A"));
        lion.add(new Polygon(327, 193, 309, 185, 301, 203, 306, 217).setFillColor("#933A73"));
        lion.add(new Polygon(362, 192, 361, 205, 348, 223).setFillColor("#C67468"));
        lion.add(new Polygon(348, 223, 361, 205, 380, 206).setFillColor("#662366"));
        lion.add(new Polygon(520, 192, 482, 192, 490, 213, 520, 205).setFillColor("#974F53"));
        lion.add(new Polygon(460, 187, 464, 208, 490, 213).setFillColor("#E19E67"));
        lion.add(new Polygon(342, 208, 362, 192, 348, 223).setFillColor("#B26369"));
        lion.add(new Polygon(490, 213, 520, 205, 529, 218).setFillColor("#5E2A50"));
        lion.add(new Polygon(464, 208, 490, 213, 468, 220).setFillColor("#DB9460"));
        lion.add(new Polygon(277, 203, 294, 221, 306, 217, 301, 203).setFillColor("#6A2774"));
        lion.add(new Polygon(490, 213, 529, 218, 500, 245).setFillColor("#8F4A4F"));
        lion.add(new Polygon(277, 203, 252, 222, 294, 221).setFillColor("#802A75"));
        lion.add(new Polygon(342, 208, 348, 223, 322, 223).setFillColor("#F9C589"));
        lion.add(new Polygon(252, 222, 294, 221, 287, 236).setFillColor("#A75472"));
        lion.add(new Polygon(322, 223, 348, 223, 331, 253).setFillColor("#F1BB7F"));
        lion.add(new Polygon(331, 253, 348, 223, 342, 245).setFillColor("#D8996E"));
        lion.add(new Polygon(500, 245, 529, 218, 498, 273).setFillColor("#A05A50"));
        lion.add(new Polygon(331, 253, 351, 253, 345, 280).setFillColor("#F8BF7A"));
        lion.add(new Polygon(498, 273, 509, 320, 520, 279).setFillColor("#EEA65A"));
        lion.add(new Polygon(468, 284, 498, 273, 471, 303).setFillColor("#BB6C4B"));
        lion.add(new Polygon(471, 303, 498, 273, 509, 320).setFillColor("#E89553"));
        lion.add(new Polygon(471, 303, 509, 320, 466, 320).setFillColor("#B77954"));
        lion.add(new Polygon(466, 320, 509, 320, 468, 338).setFillColor("#A15B53"));
        lion.add(new Polygon(520, 279, 509, 320, 522, 330).setFillColor("#F2BA70"));
        lion.add(new Polygon(534, 304, 522, 330, 562, 331).setFillColor("#BC7255"));
        lion.add(new Polygon(522, 330, 562, 331, 550, 386).setFillColor("#A5625C"));
        lion.add(new Polygon(509, 320, 522, 330, 490, 351).setFillColor("#CD8754"));
        lion.add(new Polygon(509, 320, 490, 351, 468, 338).setFillColor("#AF6751"));
        lion.add(new Polygon(468, 338, 490, 351, 440, 382).setFillColor("#BF7B54"));
        lion.add(new Polygon(468, 338, 440, 382, 444, 350).setFillColor("#A65E52"));
        lion.add(new Polygon(490, 351, 522, 330, 512, 382).setFillColor("#E6A56D"));
        lion.add(new Polygon(490, 351, 512, 382, 469, 394).setFillColor("#CE875B"));
        lion.add(new Polygon(490, 351, 469, 394, 440, 382).setFillColor("#DB925D"));
        lion.add(new Polygon(402, 354, 440, 382, 398, 402).setFillColor("#753653"));
        lion.add(new Polygon(522, 330, 512, 382, 550, 386).setFillColor("#8E4E5C"));
        lion.add(new Polygon(440, 382, 398, 402, 432, 426).setFillColor("#712F53"));
        lion.add(new Polygon(440, 382, 432, 426, 469, 394).setFillColor("#C37456"));
        lion.add(new Polygon(512, 382, 550, 386, 490, 463).setFillColor("#6A275D"));
        lion.add(new Polygon(512, 382, 490, 463, 469, 394).setFillColor("#7A385C"));
        lion.add(new Polygon(469, 394, 490, 463, 432, 426).setFillColor("#9F4E5F"));
        lion.add(new Polygon(432, 426, 490, 463, 422, 451).setFillColor("#812F5D"));
        lion.add(new Polygon(431, 188, 449, 208, 440, 210).setFillColor("#C58468"));
        lion.add(new Polygon(396, 168, 410, 172, 408, 193).setFillColor("#BC716B"));
        lion.add(new Polygon(410, 172, 408, 193, 431, 188).setFillColor("#C58468"));
        lion.add(new Polygon(420, 213, 423, 235, 444, 220).setFillColor("#D1885F"));
        lion.add(new Polygon(380, 206, 408, 193, 420, 213).setFillColor("#D99C6F"));
        lion.add(new Polygon(408, 193, 440, 210, 431, 188).setFillColor("#D59764"));
        lion.add(new Polygon(408, 193, 420, 213, 440, 210).setFillColor("#D1885F"));
        lion.add(new Polygon(294, 221, 306, 217, 294, 249).setFillColor("#87386F"));
        lion.add(new Polygon(294, 249, 287, 236, 294, 221).setFillColor("#AF5C6E"));
        lion.add(new Polygon(529, 218, 498, 273, 520, 279).setFillColor("#C0724C"));
        lion.add(new Polygon(529, 218, 520, 279, 540, 260).setFillColor("#D48C51"));
        lion.add(new Polygon(294, 249, 306, 217, 322, 223).setFillColor("#AF5C6E"));
        lion.add(new Polygon(252, 222, 287, 236, 294, 249).setFillColor("#55276F"));
        lion.add(new Polygon(331, 253, 342, 245, 351, 253).setFillColor("#EBAC79"));
        lion.add(new Polygon(252, 222, 294, 249, 245, 245).setFillColor("#662873"));
        lion.add(new Polygon(245, 245, 294, 249, 266, 270).setFillColor("#7A2B6D"));
        lion.add(new Polygon(294, 249, 322, 223, 331, 253).setFillColor("#D4846B"));
        lion.add(new Polygon(294, 249, 331, 253, 299, 264).setFillColor("#EBAC79"));
        lion.add(new Polygon(299, 264, 331, 253, 345, 280).setFillColor("#E29F70"));
        lion.add(new Polygon(520, 279, 522, 330, 534, 304).setFillColor("#EDB05F"));
        lion.add(new Polygon(520, 279, 534, 304, 540, 260).setFillColor("#DC9B59"));
        lion.add(new Polygon(540, 260, 534, 304, 562, 331).setFillColor("#CA834D"));
        lion.add(new Polygon(294, 249, 299, 264, 266, 270).setFillColor("#D68C6F"));
        lion.add(new Polygon(266, 270, 299, 264, 272, 301).setFillColor("#B46868"));
        lion.add(new Polygon(220, 306, 272, 301, 234, 399).setFillColor("#985066"));
        lion.add(new Polygon(468, 284, 471, 303, 452, 311).setFillColor("#FAC174"));
        lion.add(new Polygon(444, 350, 440, 382, 402, 354).setFillColor("#5A2750"));
        lion.add(new Polygon(297, 376, 367, 430, 368, 409).setFillColor("#BC7652"));
        lion.add(new Polygon(234, 399, 297, 376, 367, 430).setFillColor("#4F1E56"));
        lion.add(new Polygon(406, 445, 422, 451, 418, 476).setFillColor("#984B5B"));
        lion.add(new Polygon(234, 399, 390, 471, 394, 525).setFillColor("#5D2C4C"));
        lion.add(new Polygon(432, 426, 422, 451, 406, 445).setFillColor("#854659"));
        lion.add(new Polygon(406, 445, 432, 426, 391, 420).setFillColor("#A45B55"));
        lion.add(new Polygon(234, 399, 367, 430, 390, 471).setFillColor("#5D2552"));
        lion.add(new Polygon(406, 445, 390, 471, 418, 476).setFillColor("#853E5C"));
        lion.add(new Polygon(422, 451, 418, 476, 490, 463).setFillColor("#5D2552"));
        lion.add(new Polygon(418, 476, 490, 463, 394, 525).setFillColor("#732F62"));
        lion.add(new Polygon(394, 525, 418, 476, 390, 471).setFillColor("#914758"));
        lion.add(new Polygon(342, 245, 351, 253, 348, 223).setFillColor("#974E65"));
        lion.add(new Polygon(266, 270, 272, 301, 220, 306).setFillColor("#A85E6D"));
        lion.add(new Polygon(220, 306, 266, 270, 245, 245).setFillColor("#7A336F"));
        lion.add(new Polygon(351, 253, 365, 226, 379, 245).setFillColor("#C78C6C"));
        lion.add(new Polygon(468, 284, 452, 311, 441, 282).setFillColor("#F8D088"));
        lion.add(new Polygon(430, 276, 441, 282, 427, 295).setFillColor("#633752"));
        lion.add(new Polygon(427, 295, 441, 282, 452, 311).setFillColor("#F7DB9C"));
        lion.add(new Polygon(416, 283, 427, 295, 405, 315).setFillColor("#FAD58E"));
        lion.add(new Polygon(272, 301, 234, 399, 297, 376).setFillColor("#6C2861"));
        lion.add(new Polygon(427, 295, 405, 315, 427, 306).setFillColor("#C9A26B"));
        lion.add(new Polygon(427, 306, 427, 312, 452, 317).setFillColor("#744154"));
        lion.add(new Polygon(272, 301, 297, 376, 307, 309).setFillColor("#AC6960"));
        lion.add(new Polygon(405, 315, 427, 306, 427, 312).setFillColor("#5F3752"));
        lion.add(new Polygon(405, 315, 427, 312, 422, 323).setFillColor("#C99667"));
        lion.add(new Polygon(452, 317, 466, 320, 468, 338).setFillColor("#F2B66E"));
        lion.add(new Polygon(452, 317, 468, 338, 444, 350).setFillColor("#F9CC89"));
        lion.add(new Polygon(402, 354, 444, 350, 413, 335).setFillColor("#D3A46C"));
        lion.add(new Polygon(413, 335, 422, 323, 383, 331).setFillColor("#E8B576"));
        lion.add(new Polygon(297, 376, 364, 359, 368, 409).setFillColor("#D88F5A"));
        lion.add(new Polygon(364, 359, 368, 409, 381, 361).setFillColor("#AD654F"));
        lion.add(new Polygon(367, 430, 390, 471, 406, 445).setFillColor("#792E55"));
        lion.add(new Polygon(413, 335, 444, 350, 422, 323).setFillColor("#F1BF7A"));
        lion.add(new Polygon(383, 331, 413, 335, 402, 354).setFillColor("#C39160"));
        lion.add(new Polygon(405, 315, 422, 323, 383, 331).setFillColor("#BB875F"));
        lion.add(new Polygon(422, 323, 444, 350, 452, 317).setFillColor("#F7D79A"));
        lion.add(new Polygon(422, 323, 452, 317, 427, 312).setFillColor("#DEB072"));
        lion.add(new Polygon(452, 311, 471, 303, 466, 320, 452, 317).setFillColor("#D38E57"));
        lion.add(new Polygon(452, 311, 452, 317, 427, 306).setFillColor("#DEB072"));
        lion.add(new Polygon(427, 306, 452, 311, 427, 295).setFillColor("#B88864"));
        lion.add(new Polygon(376, 322, 405, 315, 377, 307).setFillColor("#8E4453"));
        lion.add(new Polygon(365, 311, 377, 307, 368, 290).setFillColor("#A06057"));
        lion.add(new Polygon(377, 307, 405, 315, 394, 295).setFillColor("#E2A76B"));
        lion.add(new Polygon(405, 315, 394, 295, 416, 283).setFillColor("#F9DB9F"));
        lion.add(new Polygon(307, 309, 297, 376, 354, 312).setFillColor("#E79C62"));
        lion.add(new Polygon(368, 290, 377, 307, 394, 295).setFillColor("#C5885B"));
        lion.add(new Polygon(307, 309, 354, 312, 345, 280).setFillColor("#D6895D"));
        lion.add(new Polygon(307, 309, 345, 280, 322, 272).setFillColor("#EAA267"));
        lion.add(new Polygon(400, 276, 394, 295, 416, 283).setFillColor("#F7C684"));
        lion.add(new Polygon(430, 276, 427, 295, 416, 283).setFillColor("#4F2150"));
        lion.add(new Polygon(441, 282, 455, 268, 430, 276).setFillColor("#442551"));
        lion.add(new Polygon(429, 268, 430, 276, 409, 268).setFillColor("#905952"));
        lion.add(new Polygon(400, 276, 416, 283, 430, 276, 409, 268).setFillColor("#3F1C53"));
        lion.add(new Polygon(368, 290, 394, 295, 400, 276).setFillColor("#F9BD74"));
        lion.add(new Polygon(368, 290, 400, 276, 379, 266).setFillColor("#BE7356"));
        lion.add(new Polygon(307, 309, 322, 272, 272, 301).setFillColor("#C67B5C"));
        lion.add(new Polygon(272, 301, 322, 272, 299, 264).setFillColor("#C68464"));
        lion.add(new Polygon(430, 276, 455, 268, 429, 268).setFillColor("#744855"));
        lion.add(new Polygon(368, 290, 379, 266, 345, 280).setFillColor("#98525A"));
        lion.add(new Polygon(345, 280, 379, 266, 351, 253).setFillColor("#B1625D"));
        lion.add(new Polygon(345, 280, 368, 290, 354, 312).setFillColor("#B1625D"));
        lion.add(new Polygon(429, 268, 455, 268, 444, 254).setFillColor("#D89F6A"));
        lion.add(new Polygon(444, 254, 455, 268, 457, 248).setFillColor("#C47F55"));
        lion.add(new Polygon(414, 254, 444, 254, 423, 235).setFillColor("#BD7859"));
        lion.add(new Polygon(351, 253, 348, 223, 365, 226).setFillColor("#EAAF77"));
        lion.add(new Polygon(348, 223, 365, 226, 361, 216).setFillColor("#E09B72"));
        lion.add(new Polygon(367, 430, 406, 445, 391, 420).setFillColor("#743854"));
        lion.add(new Polygon(367, 430, 391, 420, 368, 409).setFillColor("#8E4C50"));
        lion.add(new Polygon(368, 409, 391, 420, 398, 402).setFillColor("#6D3A3D"));
        lion.add(new Polygon(398, 402, 391, 420, 432, 426).setFillColor("#A5585D"));
        lion.add(new Polygon(368, 409, 398, 402, 381, 361).setFillColor("#87484C"));
        lion.add(new Polygon(381, 361, 398, 402, 402, 354).setFillColor("#571C4F"));
        lion.add(new Polygon(402, 354, 383, 331, 381, 361).setFillColor("#6B3B51"));
        lion.add(new Polygon(383, 331, 405, 315, 376, 322).setFillColor("#6B3B51"));
        lion.add(new Polygon(377, 307, 376, 322, 365, 311).setFillColor("#7E3854"));
        lion.add(new Polygon(354, 312, 297, 376, 364, 359).setFillColor("#E9A461"));
        lion.add(new Polygon(381, 361, 383, 331, 376, 322, 365, 311, 354, 312, 364, 359).setFillColor("#571C4F"));
        lion.add(new Polygon(354, 312, 365, 311, 368, 290).setFillColor("#8D4956"));
        lion.add(new Polygon(441, 282, 468, 284, 455, 268).setFillColor("#F2B974"));
        lion.add(new Polygon(429, 268, 444, 254, 414, 254).setFillColor("#C8885C"));
        lion.add(new Polygon(414, 254, 429, 268, 409, 268).setFillColor("#CD9566"));
        lion.add(new Polygon(351, 253, 379, 245, 379, 266).setFillColor("#AC7068"));
        lion.add(new Polygon(400, 276, 409, 268, 394, 265).setFillColor("#6C2E57"));
        lion.add(new Polygon(409, 268, 414, 254, 379, 245, 379, 266, 400, 276, 394, 265).setFillColor("#9D5C58"));
        lion.add(new Polygon(365, 226, 379, 245, 383, 226).setFillColor("#CD9971"));
        lion.add(new Polygon(391, 224, 420, 213, 423, 235).setFillColor("#B26D5D"));
        lion.add(new Polygon(414, 254, 379, 245, 383, 226, 391, 224, 423, 235).setFillColor("#91535D"));
        lion.add(new Polygon(383, 226, 391, 224, 379, 218).setFillColor("#B0746A"));
        lion.add(new Polygon(379, 218, 383, 226, 367, 218).setFillColor("#F0BD8A"));
        lion.add(new Polygon(365, 226, 383, 226, 367, 218).setFillColor("#F6DA9B"));
        lion.add(new Polygon(365, 226, 367, 218, 361, 216).setFillColor("#F9F0B8"));
        lion.add(new Polygon(367, 218, 372, 210, 361, 216).setFillColor("#9E5663"));
        lion.add(new Polygon(380, 206, 420, 213, 391, 224).setFillColor("#CC8A6A"));
        lion.add(new Polygon(367, 218, 379, 218, 372, 210).setFillColor("#DA9172"));
        lion.add(new Polygon(372, 210, 380, 206, 391, 224, 379, 218, 372, 210).setFillColor("#7B3962"));
        lion.add(new Polygon(455, 268, 468, 284, 474, 260).setFillColor("#D49A5A"));
        lion.add(new Polygon(500, 245, 498, 273, 468, 284).setFillColor("#C87A4C"));
        lion.add(new Polygon(490, 213, 487, 235, 500, 245).setFillColor("#DA8951"));
        lion.add(new Polygon(468, 220, 490, 213, 487, 235).setFillColor("#F2AF67"));
        lion.add(new Polygon(455, 268, 474, 260, 472, 231).setFillColor("#C78052"));
        lion.add(new Polygon(468, 284, 500, 245, 487, 235, 468, 220, 460, 226, 472, 231, 474, 260).setFillColor("#ECA45F"));
        lion.add(new Polygon(444, 220, 440, 210, 449, 208).setFillColor("#BB7E5A"));
        lion.add(new Polygon(468, 220, 460, 226, 461, 217).setFillColor("#F7F0AE"));
        lion.add(new Polygon(431, 188, 464, 208, 449, 208).setFillColor("#AC5D58"));
        lion.add(new Polygon(468, 220, 461, 217, 451, 221, 464, 208).setFillColor("#4C2055"));
        lion.add(new Polygon(455, 268, 472, 231, 457, 248).setFillColor("#E9B674"));
        lion.add(new Polygon(460, 226, 461, 217, 451, 221).setFillColor("#F5D997"));
        lion.add(new Polygon(420, 213, 440, 210, 444, 220).setFillColor("#D9A46E"));
        lion.add(new Polygon(457, 248, 472, 231, 460, 226, 451, 221, 444, 220).setFillColor("#F7C07B"));
        lion.add(new Polygon(457, 248, 444, 220, 423, 235, 444, 254).setFillColor("#D89B65"));
        lion.add(new Polygon(451, 221, 456, 216, 446, 215).setFillColor("#D89B65"));
        lion.add(new Polygon(446, 215, 451, 221, 444, 220).setFillColor("#682B58"));
        lion.add(new Polygon(456, 216, 446, 215, 449, 208, 464, 208).setFillColor("#682B58"));

        layer.add(lion);

        boxes.add(rect);

        boxes.add(star);

        panel.add(boxes);
    }
}
