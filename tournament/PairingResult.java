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

public class PairingResult {

	private SingleResult whiteResult;
	private SingleResult blackResult;
	private boolean byDefault;
	
	public PairingResult() {
		this.whiteResult = SingleResult.NONE;
		this.blackResult = SingleResult.NONE;
		this.byDefault = false;
	}
	
	public PairingResult(SingleResult whiteResult, SingleResult blackResult, boolean byDefault) {
		this.whiteResult = whiteResult;
		this.blackResult = blackResult;
		this.byDefault = byDefault;
	}
	
	public SingleResult getWhiteResult() {
		return whiteResult;
	}
	
	public void setWhiteResult(SingleResult whiteResult) {
		this.whiteResult = whiteResult;
	}
	
	public SingleResult getBlackResult() {
		return blackResult;
	}
	
	public void setBlackResult(SingleResult blackResult) {
		this.blackResult = blackResult;
	}
	
	public boolean isByDefault() {
		return byDefault;
	}
	
	public void setByDefault(boolean byDefault) {
		this.byDefault = byDefault;
	}
	
	public String toString() {
		return whiteResult.toString(byDefault) + ":" + blackResult.toString(byDefault);
	}
	
}
