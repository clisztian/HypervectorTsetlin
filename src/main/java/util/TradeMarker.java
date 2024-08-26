package util;

import de.gsi.chart.marker.Marker;
import javafx.scene.canvas.GraphicsContext;

public class TradeMarker implements Marker {
    @Override
    public void draw(GraphicsContext gc, double x, double y, double size) {
        gc.fillOval(x - size, y - size, 2.0 * size, 2.0 * size);
        gc.setEffect(TradeSizeShadow.drop);
    }
}
