package util;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class TradeSizeShadow {

    public static DropShadow drop = new DropShadow(BlurType.GAUSSIAN, Color.BLACK, 10, 0.2, 2, 2);
    public static Color trade_red = Color.rgb(240, 80, 70);
    public static Color trade_green = Color.rgb(110, 230, 130);
    public static Color trade_dark_buy = Color.rgb(179, 226, 255);
    public static Color trade_dark_sell = Color.rgb(186, 155, 255);
    /**
     * Get a glow
     * @param shadowColor
     * @return
     */
    public static DropShadow getDropShadow(Color shadowColor) {
        return new DropShadow(BlurType.GAUSSIAN, shadowColor, 20, 0.2, 0, 0);
    }
}