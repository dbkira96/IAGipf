package GUI.GipfBoardComponent;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import GUI.UIval;
import GameLogic.Direction;
import GameLogic.Move;
import GameLogic.Piece;
import GameLogic.PieceColor;
import GameLogic.Position;
import GameLogic.Game.Game;
import dlv.Mossa;
import dlv.Pezzo;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

/**
 * This class acts as the MouseListener for GipfBoardComponent. If no instance of this class is added as a mouse listener
 * to the component, the component will not react on mouse movements or clicks
 * <p/>
 * Created by frans on 22-9-2015.
 */
class GipfBoardComponentMouseListener extends MouseAdapter implements Runnable {
    private final GipfBoardComponent gipfBoardComponent;
    private Thread hoverThread;
    
    private int count = 0;
    
    private static Handler handler;    
	private static String encodingResource="encodings/gipf";
    
    public GipfBoardComponentMouseListener(GipfBoardComponent gipfBoardComponent) {
        this.gipfBoardComponent = gipfBoardComponent;
        handler = new DesktopHandler(new DLV2DesktopService("lib/dlv2"));
        InputProgram encoding= new ASPInputProgram();
 	    encoding.addFilesPath(encodingResource);
        handler.addProgram(encoding);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Act on mouse press, not on a click (press and release)
        int mouseX = e.getX();
        int mouseY = e.getY();

        PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
        Position selectedPosition = positionHelper.screenCoordinateToPosition(mouseX, mouseY);
        Game game = gipfBoardComponent.game;

        // Only allow to put pieces on selectable positions
        if (gipfBoardComponent.selectableStartPositions.contains(selectedPosition)) {
            if (selectedPosition.equals(gipfBoardComponent.selectedStartPosition)) {
                // Toggle gipf pieces
                this.gipfBoardComponent.game.players.current().toggleIsPlacingGipfPieces();
            }
            else {
                gipfBoardComponent.selectedStartPosition = selectedPosition;
            }
            gipfBoardComponent.repaint();
        } else if (gipfBoardComponent.selectedMoveToPosition != null) {
            int deltaPos = Position.getDeltaPos(gipfBoardComponent.selectedStartPosition, gipfBoardComponent.selectedMoveToPosition);
            Move currentMove = new Move(game.getCurrentPiece(), gipfBoardComponent.selectedStartPosition, Direction.getDirectionFromDeltaPos(deltaPos), Optional.empty());
            gipfBoardComponent.selectedStartPosition = null;
            gipfBoardComponent.selectedMoveToPosition = null;
            game.applyMove(currentMove);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hoverThread = new Thread(this);
        hoverThread.start();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hoverThread.interrupt();
    }

    @Override
    public void run() {
        while (true) {
        	if (gipfBoardComponent.game.players.current() == gipfBoardComponent.game.players.get(PieceColor.WHITE))   
            { 
            try {
                TimeUnit.MILLISECONDS.sleep(UIval.get().hoverUpdateIntervalMs);
            } catch (InterruptedException e) {
                gipfBoardComponent.currentHoverPosition = null;
                gipfBoardComponent.selectedMoveToPosition = null;

                gipfBoardComponent.repaint();

                // Interrupt the thread
                break;
            }
            
           
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();                             // Get the mouse position relative to the screen
            Point componentPosition = gipfBoardComponent.getLocationOnScreen();                                            // Get the component position relative to the screen
            mouseLocation.translate((int) -componentPosition.getX(), (int) -componentPosition.getY()); // Calculate the mouse position relative to the component

            // Only update the position if the new position is different from the old position, and if the new
            // position is actually located on the board
            PositionHelper positionHelper = new PositionHelper(gipfBoardComponent);
            Position newHoverPosition = positionHelper.screenCoordinateToPosition((int) mouseLocation.getX(), (int) mouseLocation.getY());

            Set<Position> selectablePositions = gipfBoardComponent.game.getMoveToPositionsForStartPosition(gipfBoardComponent.selectedStartPosition);


            if (gipfBoardComponent.game.isPositionOnBigBoard(newHoverPosition)) {
                if (gipfBoardComponent.game.getStartPositionsForMoves().contains(newHoverPosition)) {
                    // If the mouse hovers over a position on the border of the board
                    // select it
                    gipfBoardComponent.selectedMoveToPosition = null;
                    gipfBoardComponent.currentHoverPosition = newHoverPosition;
                } else if (selectablePositions.contains(newHoverPosition)) {
                    // If there is a position selected, and the mouse is hovering over a position where that piece can move to,
                    // clear the hover circle, and update the arrow indicating where the player can move
                    gipfBoardComponent.selectedMoveToPosition = newHoverPosition;
                    gipfBoardComponent.currentHoverPosition = newHoverPosition;
                } else {
                    // If the player is hovering over a position on the board, but it can't put a piece on it, or select it,
                    // the hover circle and the arrow indicating where the player can move are cleared
                    gipfBoardComponent.currentHoverPosition = null;
                    gipfBoardComponent.selectedMoveToPosition = null;
                }
            } else {
                // If the mouse is not hovering over a position on the board, clear the arrow and hover circle
                gipfBoardComponent.currentHoverPosition = null;
                gipfBoardComponent.selectedMoveToPosition = null;
            }
         }//FINE IFFONE    
         
         else if (gipfBoardComponent.game.players.current() == gipfBoardComponent.game.players.get(PieceColor.BLACK))
         {
           InputProgram facts= new ASPInputProgram();
     	   
           System.out.println("SONO QUI --> " + count);
           count++;
           
     		
     	   HashMap<Position, Piece> situation = (HashMap<Position, Piece>) gipfBoardComponent.game.getGipfBoardState().getPieceMap();
        	
     	    Iterator it = situation.entrySet().iterator();
     	    while (it.hasNext()) {
     	        Map.Entry pair = (Map.Entry)it.next();
     	        try {
     	        	Pezzo p = new Pezzo((Position)pair.getKey(), (Piece)pair.getValue());
					facts.addObjectInput(p);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//     	        it.remove(); // avoids a ConcurrentModificationException
     	    }
     	   
     	   handler.addProgram(facts);
     	   System.out.println(facts.getPrograms());
//     	   InputProgram encoding= new ASPInputProgram();
//     	   encoding.addFilesPath(encodingResource);
//     	   handler.addProgram(encoding);
     	   
     	   
     	  try {
  			ASPMapper.getInstance().registerClass(Mossa.class);
  			
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
//     	   handler.startAsync(new MyCallback(gipfBoardComponent));
  		
     	    Output o =  handler.startSync();
     	    
//     	    System.out.println("HO STARTATO");
     	    
	  		AnswerSets answers = (AnswerSets) o;
	  		
	  		System.out.println(answers.getAnswersets().size());
	  		if (answers.getAnswersets().size() != 0)
	  		{
		  		for(AnswerSet a:answers.getAnswersets()){
		  			System.out.println(a.getWeights());
		  			try {
		  				for(Object obj:a.getAtoms()){
		  					if(! (obj instanceof Mossa))
		  						{
		  						continue;
		  						}
		  					gipfBoardComponent.game.applyMove(((Mossa) obj).getMove());
	//	  					System.out.println("MOSSA APPLICATA");
		  					System.out.println(((Mossa) obj).getNodoEsterno() + " " + ((Mossa) obj).getDirezione());
		  					break;
		  				}
		  			} catch (Exception e) {
		  				e.printStackTrace();
		  			} 
		  			
		  		}
	  		}
	  		else
	  		{
	  			gipfBoardComponent.game.applyMove(new Mossa(51, 1).getMove());
	  		}

     	  
	  		handler.removeProgram(facts);
     	  
     	  // gipfBoardComponent.game.players.updateCurrent();	//MESSO PER FAR GIOCARE L'IA
         }
           gipfBoardComponent.repaint();
        }
    }
}
