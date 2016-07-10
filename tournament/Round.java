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

public class Round {

	private ArrayList<Pairing> pairings;
	
	public Round() {
		this.pairings = new ArrayList<Pairing>();
	}
	
	public Round(ArrayList<Pairing> pairings) {
		this.pairings = pairings;
	}
	
	public ArrayList<Pairing> getPairings() {
		return pairings;
	}
	
	public void setPairings(ArrayList<Pairing> pairings) {
		this.pairings = pairings;
	}
	
	public void removePairings() {
		this.pairings.clear();
	}
	
}
