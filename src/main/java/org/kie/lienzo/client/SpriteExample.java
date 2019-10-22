package org.kie.lienzo.client;

import java.util.ArrayList;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Sprite;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.SpriteBehaviorMap;
import com.ait.lienzo.shared.core.types.DragMode;
import org.kie.lienzo.client.BaseExample;

public class SpriteExample extends BaseExample implements Example
{
    private String imagePath = "images/icon_sprite.png";

    private static final String      TEXT_FONT   = "oblique normal bold";

    private static final int         TEXT_SIZE   = 16;

    private final ArrayList<Sprite> m_splist = new ArrayList<>();

    private boolean           m_active = true;

    public SpriteExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        addSprite(50, 50, layer);

        addSprite(100, 200, layer);

        addSprite(200, 50, layer);

        addSprite(300, 300, layer);

        addSprite(400, 100, layer);

        Text text = new Text("Click coins to start/stop the Sprite animations.", TEXT_FONT, TEXT_SIZE);
        text.setX(10).setY(400).setFillColor("black");
        layer.add(text);
    }

    private final void addSprite(final int x, final int y, final Layer layer)
    {
        final ArrayList<BoundingBox> frames = new ArrayList<>();

        for (int i = 0; i < 10; i++)
        {
            frames.add(BoundingBox.fromDoubles(i * 50, 0, (i * 50) + 50, 50));
        }
        final double tickssec = 10; // ticks per second

        final String behavior = "spincoin";


        final Sprite theSprite = new Sprite(imagePath, tickssec, new SpriteBehaviorMap(behavior, frames.toArray(new BoundingBox[frames.size()])), behavior)
                .setDraggable(true).setDragMode(DragMode.SAME_LAYER).setX(x).setY(y)
                .onLoaded((sprite) -> {
                    layer.add(sprite);
                    if (m_active)
                    {
                        sprite.play();
                    }
                });

        theSprite.addNodeMouseClickHandler((e)->{
            if (m_active)
            {
                suspend();
            }
            else
            {
                activate();
            }
        });
        m_splist.add(theSprite);
    }

    public boolean activate()
    {
        if (!m_active)
        {
            for (final Sprite sprite : m_splist)
            {
                if ((null != sprite.getLayer()) && (sprite.isLoaded()) && (false == sprite.isPlaying()))
                {
                    sprite.play();
                }
            }
            m_active = true;
            return true;
        }
        return false;
    }

    public boolean suspend()
    {
        if (m_active)
        {
            for (final Sprite sprite : m_splist)
            {
                if ((null != sprite.getLayer()) && (sprite.isLoaded()) && (sprite.isPlaying()))
                {
                    sprite.pause();
                }
            }
            m_active = false;
            return true;
        }
        return false;
    }
}
