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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import swtlib.tournament.Pairing;
import swtlib.tournament.PairingResult;
import swtlib.tournament.Player;
import swtlib.tournament.Round;
import swtlib.tournament.SingleResult;
import swtlib.tournament.Tournament;

public class SwtParser {
	
	// parses the given SWT file and returns its content as a Tournament object
	public static Tournament parse(File swtFile) throws IOException {
		
		SwtRandomAccessFile swtRaFile = null;
		
		try {
			
			// open the SWT file
			swtRaFile = new SwtRandomAccessFile(swtFile, "r");

			// get number of rounds and players
			boolean beforeFirstRound = (parseNumPairedRounds(swtRaFile) == 0);
			int numRounds = parseNumRounds(swtRaFile);
			int numPlayers = parseNumPlayers(swtRaFile);
			
			// parse player information
			ArrayList<Player> players = parsePlayers(swtRaFile, numPlayers, numRounds, beforeFirstRound);
			
			ArrayList<Round> rounds;
			if(beforeFirstRound) {
				// create the round list with empty rounds
				rounds = new ArrayList<Round>(numRounds);
				for(int roundIndex = 0; roundIndex < numRounds; roundIndex++) {
					rounds.add(new Round());
				}
			}
			else {
				// parse all pairings of all rounds
				rounds = parseRounds(swtRaFile, numRounds, players);
			}
			
			// construct the tournament
			return new Tournament(players, rounds);
			
		}
		catch(IllegalStateException e) {
			// simply pass the exception to the caller
			throw e;
		}
		catch(IOException e) {
			// simply pass the exception to the caller
			throw e;
		}
		finally {
			// close the SWT file
			if(swtRaFile != null) { 
				try {
					swtRaFile.close();
				}
				catch(IOException e) {}
			}
		}
		
	}
	
	static int parseNumPairedRounds(SwtRandomAccessFile swtRaFile) throws IOException {
		
		swtRaFile.seek(Constants.numPairedRoundsOffset);
		return swtRaFile.readLittleEndianUnsignedShort();
		
	}
	
	static int parseNumRounds(SwtRandomAccessFile swtRaFile) throws IOException {
		
		swtRaFile.seek(Constants.numRoundsOffset);
		return swtRaFile.readLittleEndianUnsignedShort();
		
	}
	
	private static int parseNumPlayers(SwtRandomAccessFile swtRaFile) throws IOException {
		
		swtRaFile.seek(Constants.numPlayersOffset);
		return swtRaFile.readLittleEndianUnsignedShort();
		
	}
	
	private static ArrayList<Player> parsePlayers(SwtRandomAccessFile swtRaFile, int numPlayers, int numRounds, boolean beforeFirstRound) throws IOException {
		
		// construct an empty player array list
		ArrayList<Player> players = new ArrayList<Player>();
		
		// calculate the file offset of the first player
		long firstPlayerOffset = Constants.getPlayerSectionOffset(numPlayers, numRounds, beforeFirstRound);
		
		// iterate over all players
		for(int playerIndex = 0; playerIndex < numPlayers; playerIndex++) {
			
			// calculate total file offset of the player
			long playerOffset = firstPlayerOffset + playerIndex * Constants.playerSize;
				
			// read the name of the player
			swtRaFile.seek(playerOffset + Constants.playerNameOffset);
			String name = swtRaFile.readNullTerminatedString(Constants.playerNameSize);
			
			// read the title of the player
			swtRaFile.seek(playerOffset + Constants.playerTitleOffset);
			String title = swtRaFile.readNullTerminatedString(Constants.playerTitleSize);
			
			// read the elo of the player
			swtRaFile.seek(playerOffset + Constants.playerEloOffset);
			String elo = swtRaFile.readNullTerminatedString(Constants.playerEloSize);
			
			// read the national rating of the player
			swtRaFile.seek(playerOffset + Constants.playerNationalRatingOffset);
			String nationalRating = swtRaFile.readNullTerminatedString(Constants.playerNationalRatingSize);
			
			// read the club of the player
			swtRaFile.seek(playerOffset + Constants.playerClubOffset);
			String club = swtRaFile.readNullTerminatedString(Constants.playerClubSize);
			
			// check if the player is active;
			swtRaFile.seek(playerOffset + Constants.playerActiveOffset);
			boolean active = toPlayerActive(swtRaFile.readByte());
			
			// check if the player is the bye player
			swtRaFile.seek(playerOffset + Constants.playerByeOffset);
			boolean bye = toPlayerBye(swtRaFile.readByte());
			
			// construct a new player and fill it into the player list array
			Player player = new Player(name, title, elo, nationalRating, club, bye, active);
			players.add(player);

		}
		
		return players;
		
	}
	
	private static ArrayList<Round> parseRounds(SwtRandomAccessFile swtRaFile, int numRounds, ArrayList<Player> players) throws IOException {
		
		// construct temporary containers 
		ArrayList<TreeMap<Integer, Pairing>> tempRounds = new ArrayList<TreeMap<Integer, Pairing>>();
		for(int roundIndex = 0; roundIndex < numRounds; roundIndex++) {
			tempRounds.add(new TreeMap<Integer, Pairing>());
		}
		
		// go to the first pairing
		swtRaFile.seek(Constants.roundOffset);
		
		// iterate over all players
		for(int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			
			// iterate over all rounds
			for(int roundIndex = 0; roundIndex < numRounds; roundIndex++) {
				
				// save file pointer position for a potential exception
				long saveFilePointerPos = swtRaFile.getFilePointer();
				
				try {
					
					// read all pairing information
					byte pairingFlags = swtRaFile.readByte();
					if(pairingFlags == 0x00) { // 0x00 means there is no pairing for this player in this round, so skip it 
						swtRaFile.skipBytes(Constants.pairingSize + Constants.spaceBetweenPairings - 1);
						continue;
					}
					boolean white = pairingFlagsToWhite(pairingFlags);
					boolean resultByDefault = pairingFlagsToResultByDefault(pairingFlags);
					int opponentIndex = swtRaFile.readLittleEndianUnsignedShort() - 1;
					SingleResult singleResult = toSingleResult(swtRaFile.readByte());
					swtRaFile.skipBytes(1);
					int pairingIndex = swtRaFile.readLittleEndianUnsignedShort() - 1;
					swtRaFile.skipBytes(1);
					
					// check consistency
					if(opponentIndex >= players.size()) {
						throw new IllegalStateException(String.format("Opponent index %d out of range.", opponentIndex));
					}
					if(pairingIndex > players.size() / 2) {
						throw new IllegalStateException(String.format("Pairing index %d out of range.", opponentIndex));
					}
					
					if(!tempRounds.get(roundIndex).containsKey(pairingIndex)) {
						
						// the pairing was not yet added, so construct a new pairing and add it
						// the only pairing information missing at this point is the single result of the opponent
						// this will be added when iteration reaches the opponent
						
						PairingResult result;
						Pairing pairing;
						
						if(white) {
							result = new PairingResult(singleResult, SingleResult.NONE, resultByDefault);
							pairing = new Pairing(players.get(playerIndex), players.get(opponentIndex), pairingIndex + 1, result);
						}
						else {
							result = new PairingResult(SingleResult.NONE, singleResult, resultByDefault);
							pairing = new Pairing(players.get(opponentIndex), players.get(playerIndex), pairingIndex + 1, result);
						}
						
						tempRounds.get(roundIndex).put(pairingIndex, pairing);

					}
					else {
						
						// the pairing was already added, so just check for consistency and add the single result of this player
						
						Pairing pairing = tempRounds.get(roundIndex).get(pairingIndex);
						PairingResult result = pairing.getResult();
						
						Player player = white ? pairing.getWhitePlayer() : pairing.getBlackPlayer();
						Player opponent = white ? pairing.getBlackPlayer() : pairing.getWhitePlayer();
						if(players.get(playerIndex) != player || players.get(opponentIndex) != opponent) {
							throw new IllegalStateException("Players inconsistent with previous pairing.");
						}
						
						if(pairing.getResult().isByDefault() != resultByDefault) {
							throw new IllegalStateException("Result default flag inconsistent with previous pairing.");
						}
						
						if(white) {
							result.setWhiteResult(singleResult);
						}
						else {
							result.setBlackResult(singleResult);
						}
						
						tempRounds.get(roundIndex).get(pairingIndex).setResult(result);
						
					}
					
				}
				catch(IllegalStateException e) {
					// add the current file pointer position to the exception message and pass it to the caller
					throw new IllegalStateException(e.getMessage() + String.format(" File pointer at 0x%x.", saveFilePointerPos));
				}
				
				swtRaFile.skipBytes(Constants.spaceBetweenPairings);
				
			}
			
		}
		
		// now construct the real round container and add the pairings from the temporary container
		ArrayList<Round> rounds = new ArrayList<Round>();		
		for(int roundIndex = 0; roundIndex < numRounds; roundIndex++) {
			ArrayList<Pairing> pairings = new ArrayList<Pairing>(tempRounds.get(roundIndex).values());
			Round round = new Round(pairings);
			rounds.add(round);
		}
		
		return rounds;
		
	}
	
	private static boolean toPlayerActive(byte playerActiveFlag) {
		
		return (playerActiveFlag != 0x2a);
		
	}
	
	private static boolean toPlayerBye(byte playerByeFlag) {
		
		return (playerByeFlag == 0x66);
		
	}

	private static boolean pairingFlagsToWhite(byte pairingFlags) {
		
		if(pairingFlags == 0x01 || pairingFlags == 0x02) {
			return true;
		}
		else if(pairingFlags == 0x03 || pairingFlags == 0x04) {
			return false;
		}
		else {
			throw new IllegalStateException(String.format("Unrecognized pairing flags 0x%02x.", pairingFlags));
		}
		
	}
	
	private static boolean pairingFlagsToResultByDefault(byte pairingFlags) {
		
		if(pairingFlags == 0x02 || pairingFlags == 0x04) {
			return true;
		}
		else if(pairingFlags == 0x01 || pairingFlags == 0x03) {
			return false;
		}
		else {
			throw new IllegalStateException(String.format("Unrecognized pairing flags 0x%02x.", pairingFlags));
		}
		
	}
	
	private static SingleResult toSingleResult(byte resultByte) {
		
		if(resultByte == 0x00) {
			return SingleResult.NONE;
		}
		else if(resultByte == 0x01) {
			return SingleResult.LOSS;
		}
		else if(resultByte == 0x02) {
			return SingleResult.DRAW;
		}
		else if(resultByte == 0x03) {
			return SingleResult.WIN;
		}
		else {
			throw new IllegalStateException(String.format("Unrecognized result byte 0x%02x.", resultByte));
		}
		
	}
	
}
