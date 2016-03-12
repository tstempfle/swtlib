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

public class Player {

	private int startRank;
	private String name;
	private String title;
	private int elo;
	private int nationalRating;
	private String club;
	private boolean bye;
	private boolean active;
	
	private ArrayList<Pairing> pairings;
	
	public Player(int startRank, String name, String title, String elo, String nationalRating, String club, boolean bye, boolean active, int numRounds) {
		this.startRank = startRank;
		this.name = name;
		this.title = title;
		try {
			this.elo = Integer.parseInt(elo);
		}
		catch(NumberFormatException e) {
			this.elo = 0;
		}
		try {
			this.nationalRating = Integer.parseInt(nationalRating);
		}
		catch(NumberFormatException e) {
			this.nationalRating = 0;
		}
		this.club = club;
		this.bye = bye;
		this.active = active;
		pairings = new ArrayList<Pairing>(numRounds);
		for(int roundIndex = 0; roundIndex < numRounds; roundIndex++) {
			pairings.add(null);
		}
	}
	
	public int getStartRank() {
		return startRank;
	}
	
	public void setStartRank(int startRank) {
		this.startRank = startRank;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getElo() {
		return elo;
	}
	
	public int getNationalRating() {
		return nationalRating;
	}
	
	public String getClub() {
		return club;
	}
	
	public boolean isBye() {
		return bye;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public ArrayList<Pairing> getPairings() {
		return new ArrayList<Pairing>(pairings);
	}
	
	public void setPairings(ArrayList<Pairing> pairings) {
		this.pairings = pairings;
	}
	
	public void setPairing(int roundIndex, Pairing pairing) {
		pairings.set(roundIndex, pairing);
	}
	
	public String getPoints(int beforeRound) {
		
		int pointsDoubled = 0;
		
		for(int roundIndex = 0; roundIndex < beforeRound; roundIndex++) {
			Pairing pairing = pairings.get(roundIndex);
			if(pairing == null) {
				continue;
			}
			if(this == pairing.getWhitePlayer()) {
				pointsDoubled += pairing.getResult().getWhiteResult().getPointsDoubled();
			}
			else if(this == pairing.getBlackPlayer()) {
				pointsDoubled += pairing.getResult().getBlackResult().getPointsDoubled();
			}
		}
		
		if(pointsDoubled == 1) {
			return "½";
		}
		else {
			String points = String.valueOf(pointsDoubled / 2);
			if(pointsDoubled % 2 != 0) {
				points += "½";
			}
			return points;
		}
		
	}
	
}
