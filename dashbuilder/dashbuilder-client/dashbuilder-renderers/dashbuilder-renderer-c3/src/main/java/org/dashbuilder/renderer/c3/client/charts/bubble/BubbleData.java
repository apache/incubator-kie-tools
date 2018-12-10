package org.dashbuilder.renderer.c3.client.charts.bubble;

public class BubbleData {
    public Double x, y;
    public String category;
    
    public BubbleData(Double x, Double y, String category) {
        super();
        this.x = x;
        this.y = y;
        this.category = category;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public String getCategory() {
        return category;
    }
    
}