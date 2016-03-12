/*
 * Copyright 2015 Tobias Stempfle <tobias.stempfle@gmx.net>
 * 
 * This file is part of swtlib.
 * 
 * swtlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * swtlib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with swtlib.  If not, see <http://www.gnu.org/licenses/>.
 */

package swtlib.tournament;

public class Pairing {

	private Player whitePlayer;
	private Player blackPlayer;
	private int pairingNumber;
	private PairingResult result;
	
	public Pairing(Player whitePlayer, Player blackPlayer, int pairingNumber) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		this.pairingNumber = pairingNumber;
		this.result = new PairingResult();
	}
	
	public Pairing(Player whitePlayer, Player blackPlayer, int pairingNumber, PairingResult result) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		this.pairingNumber = pairingNumber;
		this.result = result;
	}
	
	public Player getWhitePlayer() {
		return whitePlayer;
	}
	
	public void setWhitePlayer(Player whitePlayer) {
		this.whitePlayer = whitePlayer;
	}
	
	public Player getBlackPlayer() {
		return blackPlayer;
	}
	
	public void setBlackPlayer(Player blackPlayer) {
		this.blackPlayer = blackPlayer;
	}
	
	public int getPairingNumber() {
		return pairingNumber;
	}
	
	public PairingResult getResult() {
		return result;
	}
	
	public void setResult(PairingResult result) {
		this.result = result;
	}
	
}
