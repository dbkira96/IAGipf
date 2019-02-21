package dlv;

import java.util.Optional;
import java.util.Set;

import GameLogic.Direction;
import GameLogic.Move;
import GameLogic.Piece;
import GameLogic.Position;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("mossa")
public class Mossa {

	@Param(0)
	private int nodoEsterno;
	
	@Param(1)
	private int direzione;
	
	public Mossa(int nodoEsterno, int direzione) {
		this.nodoEsterno = nodoEsterno;
		this.direzione = direzione;
	}
	
	public Mossa() {
		
	}

	public int getNodoEsterno() {
		return nodoEsterno;
	}

	public void setNodoEsterno(int nodoEsterno) {
		this.nodoEsterno = nodoEsterno;
	}

	public int getDirezione() {
		return direzione;
	}

	public void setDirezione(int direzione) {
		this.direzione = direzione;
	}
	
	public Move getMove() {
		Move m = new Move(Piece.BLACK_SINGLE, new Position(this.nodoEsterno), Direction.getDirectionFromDeltaPos(this.direzione), Optional.empty());
		return m;
	}

	
	
	
}
