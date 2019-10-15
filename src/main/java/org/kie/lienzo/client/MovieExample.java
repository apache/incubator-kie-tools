package org.kie.lienzo.client;

import com.ait.lienzo.client.core.shape.Movie;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.TextAlign;
import org.kie.lienzo.client.BaseExample;

public class MovieExample extends BaseExample implements Example
{
    //https://upload.wikimedia.org/wikipedia/commons/9/91/Peach_poster_rodents.jpg
    private final String VIDEO_URL = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_5mb.mp4";
    private Movie movie;
    private Text text;
    private double originW;
    private double originH;

    public MovieExample(final String title)
    {
        super(title);
    }

    @Override
    public void run()
    {
        final int sw = width / 2;
        final int sh = height / 2;

        final int x = -sw / 2 + width / 2;
        final int y = -sh / 2 + height / 2;

        text = new Text("Look Ma, It's Canvas!", "oblique normal bold", 24);
        text.setFillColor("cornflowerblue").setTextAlign(TextAlign.CENTER).setShadow(new Shadow("#666", 20, 5, 5));
        text.setX(sw).setY(y - 30);
        layer.add(text);


        Movie.VideoElementOnLoad onLoad = (movie, elem) -> {
            movie.setLoop(true);
            originW = movie.getWidth();
            originH = movie.getHeight();
            setMovieLocation(sw, x, y, movie);
        };

        movie     = new Movie(VIDEO_URL, onLoad);

        layer.add(movie);

        movie.play();
    }

    private void setMovieLocation(final int sw, final int x, final int y, final Movie movie)
    {
        movie.setX(x);
        movie.setY(y);

        double ratio   =  originW / sw;
        double newHeight = originH / ratio;

        movie.setWidth(sw);
        movie.setHeight((int) newHeight);
    }

    @Override public void onResize()
    {
        super.onResize();

        final int sw = width / 2;
        final int sh = height / 2;

        final int x = -sw / 2 + width / 2;
        final int y = -sh / 2 + height / 2;

        text.setX(sw).setY(y - 30);

        setMovieLocation(sw, x, y, movie);
    }

    @Override public void destroy()
    {
        movie.stop();
        movie = null;
        text = null;
        
        super.destroy();
    }
}
