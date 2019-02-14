package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;
import GameLogic.Position;

import java.awt.*;

/**
 * Created by frans on 23-9-2015.
 */
public class PositionNames extends DrawableObject {
    public PositionNames(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        g2.setColor(UIval.get().positionNameColor);

        for (Position position : UIval.get().namedPositionsOnBoard) {
            g2.setColor(UIval.get().positionNameColor);
            g2.setFont(UIval.get().positionNameFont);
            g2.drawString(position.getName(), positionHelper.positionToScreenX(position) + 10, positionHelper.positionToScreenY(position) + 5);   // Translated by (10, 5), to make text not overlap with lines
        }
    }
}
