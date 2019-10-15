package org.kie.lienzo.client;

import java.util.ArrayList;

import com.ait.lienzo.client.core.animation.AnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.IndefiniteAnimation;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.PolyLine;
import com.ait.lienzo.client.core.shape.Polygon;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.panel.LienzoPanel;
import com.ait.lienzo.shared.core.types.ColorName;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.KeyboardEvent;
import org.kie.lienzo.client.BaseExample;

public class AsteroidsGameExample extends BaseExample implements Example
{
    private static final int   MAX_BULLETS = 6;

    private Ship               ship;
    private ArrayList<Bullet>  bullets;
    private ArrayList<Rock>    rocks;

    private int                score;
    private Layer scoreLayer;
    private Text scoreText;

    private int                numberOfLives;
    private ArrayList<Polygon> lives;

    private EventListener      keydownListener = (e) -> {
        KeyboardEvent keyboardEvent = (KeyboardEvent) e;
        key(keyboardEvent, true);
    };

    private EventListener      keyupListener = (e) -> {
        KeyboardEvent keyboardEvent = (KeyboardEvent) e;
        key(keyboardEvent, false);
    };

    public AsteroidsGameExample(final String title)
    {
        super(title);
    }

    @Override public void init(final LienzoPanel panel, final HTMLDivElement topDiv)
    {
        super.init(panel, topDiv);

        // Install key up/down handlers
        DomGlobal.window.addEventListener("keydown", keydownListener);
        DomGlobal.window.addEventListener("keyup", keyupListener);

        Layer bgLayer = new Layer();
        Rectangle background = new Rectangle(width, height);
        background.setFillColor(ColorName.BLACK);
        bgLayer.add(background);
        layer.getViewport().setBackgroundLayer(bgLayer);
    }

    @Override
    public void run()
    {
        numberOfLives = 3;
        score = 0;

        // Create a separate Layer with Text to display the score
        scoreLayer = new Layer();
        scoreText = new Text("Score: 0", "Courier", 18).setStrokeColor(ColorName.WHITE).setFillColor(ColorName.WHITE);
        scoreText.setX(25).setY(25);
        scoreLayer.add(scoreText);
        drawLives(numberOfLives);
        layer.getScene().add(scoreLayer);
        scoreLayer.draw();

        ship = new Ship(layer);
        bullets = new ArrayList<Bullet>();
        rocks = new ArrayList<Rock>();

        // Add 6 rocks. Not too close to the ship.
        for (int i = 0; i < 6;) {
            double x = width * Math.random();
            double y = height * Math.random();

            if (ship.distance(x, y) < 200)
            {
                continue;
            }

            rocks.add(new Rock(layer, x, y, 40));

            i++;
        }

        IndefiniteAnimation handle = new IndefiniteAnimation(new AnimationCallback() {
            private int hit = -1;

            @Override
            public void onFrame(IAnimation animation, IAnimationHandle handle) {
                // Update the bullets (if any)
                for (int i = bullets.size() - 1; i >= 0; i--) {
                    Bullet bullet = bullets.get(i);
                    if (!bullet.update()) {
                        bullet.stopFlying(layer);
                        bullets.remove(i);
                    }
                }

                // Update the rocks
                ROCKS: for (int i = rocks.size() - 1; i >= 0; i--) {
                    Rock rock = rocks.get(i);
                    rock.update();

                    // Check if rock hits any bullets
                    for (int j = bullets.size() - 1; j >= 0; j--) {
                        Bullet bullet = bullets.get(j);

                        if (rock.hits(bullet.getX(), bullet.getY(), 2)) {
                            // Bullet hit rock
                            bullet.stopFlying(layer);
                            bullets.remove(j);
                            rocks.remove(rock);

                            addScore(rock.getScore());

                            rock.explode(rocks, layer);
                            continue ROCKS;
                        }
                    }

                    // Check if the rock hit the ship
                    if (rock.hits(ship.getX(), ship.getY(), 5))
                    {
                        // Rock hit ship. Rotate colors to simulate explosion
                        ship.setShipColor(nextColor());
                        if ( hit <= 0 && numberOfLives > 0)
                        {
                            drawLives(--numberOfLives);
                        }

                        hit = 300;
                    }
                    else if ( hit >= 0 )
                    {
                        ship.setShipColor(nextColor());
                        hit--;
                    }
                    else
                    {
                        ship.setShipColor(ColorName.WHITE.getValue());
                    }
                }

                ship.update();

                layer.batch();

                if (numberOfLives == 0)
                {
                    Text gameOverText = new Text("Game Over Press Space To Restart").setStrokeColor(ColorName.CRIMSON).setFillColor(ColorName.CRIMSON);
                    gameOverText.setX(25).setY(300);
                    scoreLayer.add(gameOverText);
                    scoreLayer.draw();
                    handle.stop();
                }
            }

            private int colorIndex = 0;
            private String[] colors = { "yellow", "yellow", "yellow", "yellow",
                                        "orange", "orange", "orange", "orange",
                                        "red", "red", "red", "red",
                                        "blueviolet", "blueviolet", "blueviolet", "blueviolet"};

            private String nextColor() {
                colorIndex = (colorIndex + 1) % colors.length;
                return colors[colorIndex];
            }

        });
        handle.run();
    }

    private void drawLives(int numberOfLives)
    {
        if (lives != null)
        {
            for (int i = 0; i < lives.size(); i++)
            {
                lives.get(i).removeFromParent();
            }
        }

        lives = new ArrayList<Polygon>(numberOfLives);

        int x = 250;
        for (int i = 0; i < numberOfLives; i++)
        {
            Polygon ship = createShip();
            ship.setY(21);
            ship.setX(x);
            ship.setRotationDegrees(180);
            lives.add(ship);
            scoreLayer.add(ship);
            x = x + 25;
        }

        scoreLayer.batch();
    }

    private void addScore(int sc) {
        score += sc;
        scoreText.setText("Score: " + score);
        scoreLayer.draw();
    }

    protected void key(KeyboardEvent event, boolean down) {
        String code = event.code;
        switch (code) {
            case "Left": // IE/Edge specific value
            case "ArrowLeft": // Rotate left
                if (down)
                    ship.rotate(-1);
                break;
            case "Right": // IE/Edge specific value
            case "ArrowRight": // Rotate right
                if (down)
                    ship.rotate(1);
                break;

            case "ArrowUp": // Thrust ship
                if (down)
                    ship.thrust(true);
                else
                    ship.thrust(false);
                break;

            case "Space": // older browsers
            case "Spacebar": // older browsers
            case " ": // SPACE BAR - Fire bullet
                if ( numberOfLives == 0 )
                {
                    // remove or null everything, so we can start again.
                    layer.removeAll();
                    scoreLayer.removeAll();

                    ship = null;
                    bullets = null;
                    rocks = null;
                    scoreLayer.removeFromParent();
                    scoreText = null;
                    lives = null;
                    run();
                }
                else if (down && bullets.size() < MAX_BULLETS)
                {
                    ship.fire(layer, bullets);
                }
                break;
            case "Enter": // Teleport ship
                if (down) {
                    double x = width * Math.random();
                    double y = height * Math.random();
                    ship.setLocation(x, y);
                }
                break;
            default:
                break;
        }
    }

    public class Ship extends Falling {

        private Polygon ship;
        private PolyLine exhaust;
        private Group shape;
        private double angle; // ship points down initially
        private boolean thrust; // whether thrust is on

        private static final double ROT_ANGLE = Math.PI / 12;
        private static final double MAX_SPEED = 100;
        private static final double THRUST = 0.5;

        public Ship(Layer layer) {

            super(layer);

            shape = new Group();

            ship = createShip();
            shape.add(ship);

            Point2DArray p = new Point2DArray().pushXY(2, 0).pushXY(4, -3).pushXY(2, -2).pushXY(0, -5).pushXY(-2, -2).pushXY(-4, -3).pushXY(-2, -0);
            exhaust = new PolyLine(p);
            exhaust.setY(-5);
            exhaust.setStrokeColor(ColorName.YELLOW);
            exhaust.setVisible(false);
            exhaust.setStrokeWidth(2);
            shape.add(exhaust);

            setLocation(layer.getWidth() / 2, layer.getHeight() / 2);
            tick(shape);

            layer.add(shape);

        }

        public Group getShape() {
            return shape;
        }

        public void setShipColor(String color) {
            ship.setStrokeColor(color);
        }

        public void fire(Layer layer, ArrayList<Bullet> bullets) {
            double x = getX() - Math.sin(angle) * 10;
            double y = getY() + Math.cos(angle) * 10;

            Bullet bullet = new Bullet(layer, x, y, getDx(), getDy(), angle);
            bullets.add(bullet);
        }

        public void update() {
            tick(shape);

            exhaust.setVisible(thrust);
            if (thrust) {
                boolean small = System.currentTimeMillis() % 2 == 0;
                exhaust.setScale(small ? 0.5 : 1); // flicker the flame
            }
            shape.setRotation(angle);
        }

        // dir = {-1, 0, 1}
        public void rotate(int dir) {
            angle += (dir * ROT_ANGLE);
        }

        public void thrust(boolean val) {
            thrust = val;
            if (!thrust)
                return;

            // Thrusters are on - adjust the speed
            double dx = getDx();
            double nvx = dx - Math.sin(angle) * THRUST;
            if (nvx < MAX_SPEED)
                dx = nvx;

            double dy = getDy();
            double nvy = dy + Math.cos(angle) * THRUST;
            if (nvy < MAX_SPEED)
                dy = nvy;

            setSpeed(dx, dy);
        }
    }

    private Polygon createShip()
    {
        Point2DArray a    = new Point2DArray().pushXY(0, 10).pushXY(5, -6).pushXY(0, -2).pushXY(-5, -6);
        Polygon ship = new Polygon(a);
        ship.setStrokeColor(ColorName.WHITE);
        ship.setStrokeWidth(2);
        return ship;
    }

    public class Rock extends Falling {
        private int     size; // 40, 20, 10
        private double  spin; // how fast it spins
        private int     score; // number of points for this rock
        private double  maxRadius; // how big the rock is
        private Polygon shape;

        public Rock(Layer layer, double x, double y, int si) {
            super(layer);

            size = si;
            shape = new Polygon(createPoints(size));
            shape.setStrokeColor(ColorName.WHITE);
            shape.setStrokeWidth(3);
            setLocation(x, y);
            tick(shape);

            double angle = Math.random() * Math.PI * 2;

            double speedFactor = 0.5;
            double dx = Math.sin(angle) * (5 - size / 10) * speedFactor;
            double dy = Math.cos(angle) * (5 - size / 10) * speedFactor;
            setSpeed(dx, dy);

            spin = (1 + Math.random()) * 0.01;
            score = (5 - size / 10) * 100;

            layer.add(shape);
        }

        public void explode(ArrayList<Rock> rocks, Layer layer) {
            layer.remove(shape);

            if (size > 10) // if it's not the smallest rock...
            {
                // Add 2 new rocks, half the size
                for (int i = 0; i < 2; i++) {
                    Rock rock = new Rock(layer, getX(), getY(), size / 2);
                    rocks.add(rock);
                }
            }
        }

        public int getScore() {
            return score;
        }

        public void update() {
            int ticks = tick(shape);
            shape.setRotation(shape.getRotation() + spin * ticks);
        }

        private Point2DArray createPoints(int size) {
            Point2DArray a = new Point2DArray();
            for (double angle = 0; angle < Math.PI * 2; angle += 0.25 + Math.random() * 0.5) {
                double radius = size + (size / 2 * Math.random());
                a.pushXY(Math.sin(angle) * radius, Math.cos(angle) * radius);

                if (radius > maxRadius)
                {
                    maxRadius = radius; // track how big the rock is
                }
            }

            maxRadius *= 0.8; // use a slightly smaller size in hit detection

            return a;
        }

        public boolean hits(double x, double y, double size) {
            return distance(x, y) <= size + maxRadius;
        }
    }

    public class Bullet extends Falling {
        private static final double SPEED = 4;
        private static final int    DISTANCE = 400; // how far the bullet flies

        private              int    i = 0; // how many ticks the bullet has been flying
        private Circle shape;

        public Bullet(Layer layer, double x, double y, double dx, double dy, double angle) {
            super(layer);

            shape = new Circle(2).setFillColor(ColorName.WHITE);
            shape.setX(x).setY(y);
            layer.add(shape);

            setLocation(x, y);
            setSpeed(dx - Math.sin(angle) * SPEED, dy + Math.cos(angle) * SPEED);
        }

        public void stopFlying(Layer layer) {
            layer.remove(shape);
        }

        public boolean update() {
            i += tick(shape);

            if (i > DISTANCE / SPEED) {
                return false; // stop flying
            }
            return true;
        }
    }

    // Base class for falling objects, i.e. Ship, Bullet and Rock
    public class Falling {
        private double x;
        private double y;
        private double dx;
        private double dy;

        private int screenWidth;
        private int screenHeight;

        private long startTime;
        protected int every = 20;

        public Falling(Layer layer) {
            startTime = System.currentTimeMillis();

            init(layer);
        }

        public void init(Layer layer) {
            screenWidth = layer.getWidth();
            screenHeight = layer.getHeight();
        }

        public void setLocation(double xx, double yy) {
            x = xx;
            y = yy;
        }

        public void setSpeed(double ddx, double ddy) {
            dx = ddx;
            dy = ddy;
        }

        public int tick(IPrimitive<?> prim) {
            long time = System.currentTimeMillis();

            if (time - startTime < every) {
                prim.setX(x);
                prim.setY(y);
                return 0;
            }

            int ticks = 0;
            while (time - startTime > every) {
                x += dx;
                y += dy;

                if (x < 0)
                    x = screenWidth;
                else if (x > screenWidth)
                    x = 0;

                if (y < 0)
                    y = screenHeight;
                else if (y > screenHeight)
                    y = 0;

                startTime += every;
                ticks++;
            }

            prim.setX(x);
            prim.setY(y);

            return ticks;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getDx() {
            return dx;
        }

        public double getDy() {
            return dy;
        }

        public double distance(double x, double y) {
            double dx = x - getX();
            double dy = y - getY();
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    @Override public void onResize()
    {
        super.onResize();
    }

    @Override public void destroy()
    {
        super.destroy();
        DomGlobal.window.removeEventListener("keydown", keydownListener);
        DomGlobal.window.removeEventListener("keyup", keyupListener);
    }
}
