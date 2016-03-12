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

package swtlib.swt;

class Constants {

	static final long beforeFirstRoundFlagOffset = 0x0; // absolute file offset to the flag indicating if the first round was already paired
	static final long numPlayersOffset = 0x7;           // absolute file offset to the number of players field
	static final long numRoundsOffset = 0x1;            // absolute file offset to the number of rounds field
	static final long currentRoundOffset = 0x3;	       // absolute file offset to the current round field
	static final long numPairedRoundsOffset = 0x5;      // absolute file offset to the number of paired rounds field
	static final long numPairingsOffset = 0xf36;        // absolute file offset to the number of pairings of the first round
	static final long roundOffset = 0x3450;             // absolute file offset to the first pairing of the first round
	
	static final long playerOffsetBeforeFirstRound = 0x3448; // absolute file offset to the first player field if there are no paired rounds yet
	
	static final long playerNameOffset = 0x00;           // relative offset from the beginning of the player field to the name field
	static final long playerClubOffset = 0x21;           // relative offset from the beginning of the player field to the club field
	static final long playerTitleOffset = 0x42;          // relative offset from the beginning of the player field to the title field
	static final long playerEloOffset = 0x46;            // relative offset from the beginning of the player field to the elo field
	static final long playerNationalRatingOffset = 0x4b;	// relative offset from the beginning of the player field to the national rating field
	static final long playerActiveOffset = 0xb8;         // relative offset from the beginning of the player field to the active flag
	static final long playerByeOffset = 0xbd;            // relative offset from the beginning of the player field to the bye flag
	
	static final int pairingSize = 0x8;                   // size of one pairing
	static final int spaceBetweenPairings = 0xb;          // space between each pairing, filled with zeros
	static final int spaceBetweenRoundsAndPlayers = 0x03; // space between the last pairing of the last round and the first player
	static final int playerSize = 0x28f;                  // total size of one player
	static final int playerNameSize = 0x20;               // maximum length of the player name string (zero-terminated)
	static final int playerClubSize = 0x20;               // maximum length of the player club string (zero-terminated)
	static final int playerTitleSize = 0x03;              // maximum length of the player title string (zero-terminated)
	static final int playerEloSize = 0x04;                // maximum length of the player elo string (zero-terminated)
	static final int playerNationalRatingSize = 0x04;     // maximum length of the player national rating string (zero-terminated)
	
	static long getPlayerSectionOffset(int numPlayers, int numRounds, boolean beforeFirstRound) {
		if(beforeFirstRound) {
			return playerOffsetBeforeFirstRound;
		}
		else {
			return (roundOffset + (pairingSize + spaceBetweenPairings) * numPlayers * numRounds + spaceBetweenRoundsAndPlayers - spaceBetweenPairings);
		}
	}
	
}
