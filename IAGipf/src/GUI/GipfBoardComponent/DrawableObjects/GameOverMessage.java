package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GUI.UIval;

import java.awt.*;

/**
 * Created by frans on 2-10-2015.
 */
public class GameOverMessage extends DrawableObject {
    public GameOverMessage(Graphics2D g2, GipfBoardComponent gipfBoardComponent) {
        super(g2, gipfBoardComponent);
    }

    @Override
    public void draw() {
        if (gipfBoardComponent.game.players.winner() != null) {
            g2.setFont(UIval.get().gameOverFont);
            g2.setColor(UIval.get().gameOverTextColor);
            g2.drawString(gipfBoardComponent.game.players.winner().pieceColor + " won!", 100, 100);
        }
    }
}
