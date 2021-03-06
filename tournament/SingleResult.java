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

public enum SingleResult {
	
	NONE (0, "?", "?"),
	LOSS (0, "0", "-"),
	DRAW (1, "\u00BD", "="),
	WIN  (2, "1", "+");
	
	private final int pointsDoubled;
	private final String pointsAsString;
	private final String byDefaultResultAsString;
	
	private SingleResult(int pointsDoubled, String pointsAsString, String byDefaultResultAsString) {
		this.pointsDoubled = pointsDoubled;
		this.pointsAsString = pointsAsString;
		this.byDefaultResultAsString = byDefaultResultAsString;
	}
	
	public int getPointsDoubled() {
		return pointsDoubled;
	}
	
	public String toString(boolean byDefault) {
		if(byDefault) {
			return byDefaultResultAsString;
		}
		else {
			return pointsAsString;
		}
	}
	
}
