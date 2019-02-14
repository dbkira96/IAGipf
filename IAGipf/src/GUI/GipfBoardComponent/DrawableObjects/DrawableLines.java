package GUI.GipfBoardComponent.DrawableObjects;

import GUI.GipfBoardComponent.GipfBoardComponent;
import GameLogic.Direction;
import GameLogic.Position;

import java.awt.*;

/**
 * Created by frans on 25-9-2015.
 */
public class DrawableLines extends DrawableObject {
    private final Position start;
    private final Position end;
    private final Direction nextStart;
    private final Direction nextEnd;
    private final int nr;

    DrawableLines(Graphics2D g2, GipfBoardComponent gipfBoardComponent, Position start, Position end, Direction nextStart, Direction nextEnd, int nr) {
        super(g2, gipfBoardComponent);

        this.start = start;
        this.end = end;
        this.nextStart = nextStart;
        this.nextEnd = nextEnd;
        this.nr = nr;
    }

    @Override
    public void draw() {
        int startDeltaPos = nextStart.getDeltaPos();
        int endDeltaPos = nextEnd.getDeltaPos();

        for (int lineNr = 0; lineNr < nr; lineNr++) {
            Position start = new Position(this.start.getPosId() + (lineNr * startDeltaPos));
            Position end = new Position(this.end.getPosId() + (lineNr * endDeltaPos));

            g2.drawLine(
                    positionHelper.positionToScreenX(start),
                    positionHelper.positionToScreenY(start),
                    positionHelper.positionToScreenX(end),
                    positionHelper.positionToScreenY(end)
            );
        }

    }
}
