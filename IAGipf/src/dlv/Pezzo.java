package dlv;

import GameLogic.Piece;
import GameLogic.Position;
import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("pezzo")
public class Pezzo {

	@Param(0)
	private String colore;
	
	@Param(1)
	private int posizione;

	public Pezzo(Position position, Piece piece) {
		
		this.posizione = position.getPosId();
		this.colore = piece.getPieceColor().toString().toLowerCase();
	}
	
	public Pezzo() {
		
	}
	
	public String getColore() {
		return colore;
	}

	public void setColore(String colore) {
		this.colore = colore;
	}

	public int getPosizione() {
		return posizione;
	}

	public void setPosizione(int posizione) {
		this.posizione = posizione;
	}
}
