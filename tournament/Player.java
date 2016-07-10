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

public class Player {

	private String name;
	private String title;
	private int elo;
	private int nationalRating;
	private String club;
	private boolean bye;
	private boolean active;
	
	public Player(String name, String title, String elo, String nationalRating, String club, boolean bye, boolean active) {
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
	
}
