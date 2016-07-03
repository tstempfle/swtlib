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

import swtlib.tournament.Pairing;
import swtlib.tournament.Player;
import swtlib.tournament.Round;
import swtlib.tournament.SingleResult;
import swtlib.tournament.Tournament;

public class SwtWriter {

	public static void write(File swtFile, Tournament tournament) throws IOException {
		
		if(!swtFile.exists()) {
			throw new IOException("File " + swtFile.getName() + " does not exist.");
		}
		
		SwtRandomAccessFile swtRaFile = null;
		
		try {
			
			// open the SWT file
			swtRaFile = new SwtRandomAccessFile(swtFile, "rw");
			
			// check the old number of paired rounds
			boolean oldBeforeFirstRound = SwtParser.parseNumPairedRounds(swtRaFile) == 0;
			
			// check if there were new players added
			boolean newPlayers = false;
			int oldNumPlayers = checkNumPlayers(swtRaFile, tournament.getPlayers().size());
			if(oldNumPlayers != tournament.getPlayers().size()) {
				newPlayers = true;
			}
			
			// write the number of paired rounds
			int numPairedRounds = writeNumPairedRounds(swtRaFile, tournament.getRounds());
			boolean beforeFirstRound = (numPairedRounds == 0);
			
			// write flag indicating if the first round was already paired
			writeBeforeFirstRoundFlag(swtRaFile, beforeFirstRound);
			
			// write the number of pairings of each round
			writeNumPairings(swtRaFile, tournament.getRounds());
			
			boolean beforeFirstRoundChanged = oldBeforeFirstRound != beforeFirstRound;
			
			// save the whole player section if there are new players or if the first round was just paired, as it will be overwritten
			byte[] playerSection = null;
			if(newPlayers || beforeFirstRoundChanged) {
				long playerSectionOffset = Constants.getPlayerSectionOffset(oldNumPlayers, tournament.getRounds().size(), oldBeforeFirstRound);
				int playerSectionLength = (int)(swtRaFile.length() - playerSectionOffset);
				playerSection = new byte[playerSectionLength];
				swtRaFile.seek(playerSectionOffset);
				swtRaFile.readFully(playerSection, 0, playerSectionLength);
			}
			
			// write the pairings for each player
			if(!beforeFirstRound) {
				writeRounds(swtRaFile, tournament.getPlayers());
			}
			
			// rewrite the player section if necessary
			if(newPlayers || beforeFirstRoundChanged) {
				writePlayerSection(swtRaFile, playerSection, oldNumPlayers, tournament.getRounds().size(), tournament.getPlayers(), beforeFirstRound);
			}
			
			// refresh the player active flag
			writePlayerActiveFlags(swtRaFile, tournament.getRounds().size(), tournament.getPlayers(), beforeFirstRound);
			
			// if we just added the first round, we have to overwrite the section where the player information were, as this section is now used for storing ranking changes
			if(beforeFirstRoundChanged) {
				swtRaFile.seek(Constants.playerOffsetBeforeFirstRound);
				for(long byteIndex = Constants.playerOffsetBeforeFirstRound; byteIndex < Constants.roundOffset; byteIndex++) {
					swtRaFile.writeByte(0x00);
				}
			}
			
		}
		catch(IOException | IllegalStateException e) {
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
	
	private static int checkNumPlayers(SwtRandomAccessFile swtRaFile, int newNumPlayer) throws IOException {
		
		swtRaFile.seek(Constants.numPlayersOffset);
		int oldNumPlayers = swtRaFile.readLittleEndianUnsignedShort();
		
		if(oldNumPlayers != newNumPlayer) {
			swtRaFile.seek(Constants.numPlayersOffset);
			swtRaFile.writeLittleEndianUnsignedShort(newNumPlayer);
		}
		
		return oldNumPlayers;
		
	}
	
	private static int writeNumPairedRounds(SwtRandomAccessFile swtRaFile, ArrayList<Round> rounds) throws IOException {
		
		// determine the number of paired rounds
		int numPairedRounds = 0;
		for(Round round : rounds) {
			if(round.getPairings().isEmpty()) {
				break;
			}
			numPairedRounds++;
		}
		
		// write the number of paired rounds both to the number of paired round and the current round field
		swtRaFile.seek(Constants.currentRoundOffset);
		swtRaFile.writeLittleEndianUnsignedShort(numPairedRounds);
		swtRaFile.seek(Constants.numPairedRoundsOffset);
		swtRaFile.writeLittleEndianUnsignedShort(numPairedRounds);
		
		return numPairedRounds;
		
	}
	
	private static void writeBeforeFirstRoundFlag(SwtRandomAccessFile swtRaFile, boolean beforeFirstRound) throws IOException {
		
		swtRaFile.seek(Constants.beforeFirstRoundFlagOffset);
		if(beforeFirstRound) {
			swtRaFile.writeByte(0x00);
		}
		else {
			swtRaFile.writeByte(0xff);
		}
		
	}
	
	private static void writeNumPairings(SwtRandomAccessFile swtRaFile, ArrayList<Round> rounds) throws IOException {
		
		swtRaFile.seek(Constants.numPairingsOffset);
		
		for(Round round : rounds) {
			swtRaFile.writeLittleEndianUnsignedShort(round.getPairings().size());
		}
		
	}
	
	private static void writeRounds(SwtRandomAccessFile swtRaFile, ArrayList<Player> players) throws IOException {
		
		// go to the first pairing
		swtRaFile.seek(Constants.roundOffset);
		
		boolean firstPairing = true;
		
		// iterate over all players
		for(Player player : players) {
			
			// iterate over all pairings
			for(Pairing pairing : player.getPairings()) {
				
				if(!firstPairing) {
					// fill the space between the last and this pairing with zeros
					for(int byteIndex = 0; byteIndex < Constants.spaceBetweenPairings; byteIndex++) {
						swtRaFile.writeByte(0x00);
					}
				}
				else {
					firstPairing = false;
				}
				
				// if there is no pairing for this player in this round, fill with zeros
				if(pairing == null) {
					for(int byteIndex = 0; byteIndex < Constants.pairingSize; byteIndex++) {
						swtRaFile.writeByte(0x00);
					}
					continue;
				}
				
				// get some pairing information
				boolean white = player == pairing.getWhitePlayer();
				Player opponent;
				if(white) {
					opponent = pairing.getBlackPlayer();
				}
				else {
					opponent = pairing.getWhitePlayer();
				}
				
				// write the pairing data
				swtRaFile.writeByte(createPairingFlags(pairing.getWhitePlayer() == player, pairing.getResult().isByDefault()));
				swtRaFile.writeLittleEndianUnsignedShort(opponent.getStartRank());
				swtRaFile.writeByte(singleResultToByte(white ? pairing.getResult().getWhiteResult() : pairing.getResult().getBlackResult()));
				swtRaFile.writeByte(0x00);
				swtRaFile.writeLittleEndianUnsignedShort(pairing.getPairingNumber());
				swtRaFile.writeByte(pairing.getResult().isByDefault() ? 0x02 : 0x00);
				
			}
			
		}
		
		for(int byteIndex = 0; byteIndex < Constants.spaceBetweenRoundsAndPlayers; byteIndex++) {
			swtRaFile.writeByte(0x00);
		}
		
	}
	
	private static void writePlayerSection(SwtRandomAccessFile swtRaFile, byte[] oldPlayerSection, int oldNumPlayers, int numRounds, ArrayList<Player> players, boolean beforeFirstRound) throws IOException {
		
		long firstPlayerOffset = Constants.getPlayerSectionOffset(players.size(), numRounds, beforeFirstRound);
		
		swtRaFile.seek(firstPlayerOffset);		
		swtRaFile.write(oldPlayerSection);
		
		for(int playerIndex = oldNumPlayers; playerIndex < players.size(); playerIndex++) {
			
			Player player = players.get(playerIndex);
			
			long playerOffset = firstPlayerOffset + playerIndex * Constants.playerSize;
			swtRaFile.seek(playerOffset);
			swtRaFile.writeNullTerminatedString(player.getName());
			
			// fill with zeros
			for(long byteIndex = swtRaFile.getFilePointer(); byteIndex < playerOffset + Constants.playerByeOffset; byteIndex++) {
				swtRaFile.writeByte(0x00);
			}
			
			swtRaFile.seek(playerOffset + Constants.playerByeOffset);
			swtRaFile.writeByte(playerByeToByte(player.isBye()));
			
			// fill with zeros
			for(long byteIndex = Constants.playerByeOffset; byteIndex < Constants.playerSize; byteIndex++) {
				swtRaFile.writeByte(0x00);
			}
			
		}
		
	}
	
	private static void writePlayerActiveFlags(SwtRandomAccessFile swtRaFile, int numRounds, ArrayList<Player> players, boolean beforeFirstRound) throws IOException {
		
		long firstPlayerOffset = Constants.getPlayerSectionOffset(players.size(), numRounds, beforeFirstRound);
		
		for(int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
			// do this only for the bye player, as it is the only player whose status can change from within SwissMate
			if(players.get(playerIndex).isBye()) {
				swtRaFile.seek(firstPlayerOffset + playerIndex * Constants.playerSize + Constants.playerActiveOffset);
				swtRaFile.writeByte(playerActiveToByte(players.get(playerIndex).isActive()));
			}
		}
		
	}
	
	private static byte createPairingFlags(boolean white, boolean byDefault) {
		
		byte pairingFlags = 0;
		
		if(white) {
			pairingFlags = 1;
		}
		else {
			pairingFlags = 3;
		}
		
		if(byDefault) {
			pairingFlags++;
		}
		
		return pairingFlags;
		
	}
	
	private static byte singleResultToByte(SingleResult singleResult) {
		
		switch(singleResult) {
		
		case NONE:
			return 0x00;
		case LOSS:
			return 0x01;
		case DRAW:
			return 0x02;
		case WIN:
			return 0x03;
		default:
			throw new IllegalStateException("Unrecognized result enum " + singleResult.toString());	
			
		}
		
	}
	
	private static byte playerByeToByte(boolean isBye) {
		
		if(isBye) {
			return 0x66;
		}
		else {
			return 0x20;
		}
			
	}
	
	private static byte playerActiveToByte(boolean isActive) {
		
		if(isActive) {
			return 0x20; // this loses the player attribute, but the only player which can be changed to active is the bye player, who has no attribute
		}
		else {
			return 0x2a;
		}
		
	}
	
}
