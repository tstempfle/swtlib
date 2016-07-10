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

import java.util.ArrayList;

public class Tournament {

	private ArrayList<Player> players;
	private ArrayList<Round> rounds;
	
	public Tournament() {
		this.players = new ArrayList<Player>();
		this.rounds = new ArrayList<Round>();
	}
	
	public Tournament(ArrayList<Player> players, ArrayList<Round> rounds) {
		this.players = players;
		this.rounds = rounds;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public ArrayList<Round> getRounds() {
		return rounds;
	}
	
	public void setRounds(ArrayList<Round> rounds) {
		this.rounds = rounds;
	}
	
	public ArrayList<Pairing> getPlayerPairings(Player player) {
		
		ArrayList<Pairing> pairings = new ArrayList<Pairing>(rounds.size());
		
		for(Round round : rounds) {
			boolean pairingFound = false;
			for(Pairing pairing : round.getPairings()) {
				if(player == pairing.getWhitePlayer() || player == pairing.getBlackPlayer()) {
					pairings.add(pairing);
					pairingFound = true;
					break;
				}
			}
			if(!pairingFound) {
				pairings.add(null);
			}
		}
		
		return pairings;
		
	}
	
	public int getPlayerPointsDoubled(Player player, int beforeRound) {
		
		int pointsDoubled = 0;
		
		ArrayList<Pairing> playerPairings = getPlayerPairings(player);
		
		for(int roundIndex = 0; roundIndex < beforeRound; roundIndex++) {
			Pairing pairing = playerPairings.get(roundIndex);
			if(player == pairing.getWhitePlayer()) {
				pointsDoubled += pairing.getResult().getWhiteResult().getPointsDoubled();
			}
			else if(player == pairing.getBlackPlayer()) {
				pointsDoubled += pairing.getResult().getBlackResult().getPointsDoubled();
			}
		}
		
		return pointsDoubled;
		
	}
	
	public String getPlayerPoints(Player player, int beforeRound) {
		
		int pointsDoubled = getPlayerPointsDoubled(player, beforeRound);
		
		if(pointsDoubled == 1) {
			return "\u00BD";
		}
		else {
			String points = String.valueOf(pointsDoubled / 2);
			if(pointsDoubled % 2 != 0) {
				points += "\u00BD";
			}
			return points;
		}
		
	}
	
}
