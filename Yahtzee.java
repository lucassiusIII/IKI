/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
		// Private instance variables
	private int nPlayers; 
	private String[] playerNames; 
	private YahtzeeDisplay display; 
	private RandomGenerator rgen = new RandomGenerator(); 
	private int[] dice = new int[N_DICE];
	private int[][] scoreCard;
	private boolean[][] isUsed;
	private int upperscore = 0;
	private int lowerscore = 0;
	private int bonusscore = 0;
	private boolean myMagicStub = false;

	public static void main(String[] args) {
		new Yahtzee().start(args); 
	}

	public void run() {
		IODialog dialog = getDialog(); 
		nPlayers = dialog.readInt("Enter number of players"); 
		playerNames = new String[nPlayers]; 
		for (int i = 1; i <= nPlayers; i++) { 
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i); 
		} 
		display = new YahtzeeDisplay(getGCanvas(), playerNames); 
		playGame(); 
	}

	private void playGame() {
		// for loop for the amount of turns per player
		for (int i = 0; i < N_SCORING_CATEGORIES; i++) {
			// for loop for the amount of players
			for (int j = 1; j <= nPlayers; j++) {
				//single turn 
				playerFirstRoll(j);
				for(int k = 0; k < 2; k++) {
					display.waitForPlayerToSelectDice();
					playerSelectDie();
				}
				scoreCard = new int[N_CATEGORIES][nPlayers];
				isUsed = new boolean[N_CATEGORIES][nPlayers];
				playerCheckCategory(j); 
				createUpperScore(j);
				createUpperBonus(j);
				createLowerScore(j);
				createTotal(j);
			}	
		}
		display.printMessage("Congratulations! You finished the game. (please click)");
	}

	// equivalent for the given magic stub. Might contain bugs.
	private boolean myMagicStub(int[] dice, int category) {
		boolean legit = false;
		if (category <= SIXES) {
			return true;
		}
		switch (category) {
			case THREE_OF_A_KIND: 	
				legit = isCategoryAmountOfAKind(dice, 3);
				return legit;
			case FOUR_OF_A_KIND: 	
				legit = isCategoryAmountOfAKind(dice, 4); 
				return legit;
			case FULL_HOUSE: 		
				legit = isCategoryFullHouse(dice); 
				return legit;
			case SMALL_STRAIGHT: 	
				legit = isCategoryStraight(dice, 4);	
				return legit;
			case LARGE_STRAIGHT: 	
				legit = isCategoryStraight(dice, 5);
				return legit;
			case YAHTZEE: 			
				legit = isCategoryAmountOfAKind(dice, 5);
				return legit;
			case CHANCE: 
				return legit;
		}
		return false;
	}

	// check if there is some amount of same numbers on the dice.
	private boolean isCategoryAmountOfAKind(int[] dice, int amountOfAKind) {
		int countAmount = 0;
		for (int diceN = 0; diceN < N_DICE; diceN++) {
			for (int i = 1; i <= 6; i++) {
				if (dice[diceN] == i) {
					countAmount = countAmount + 1; 
					if (countAmount >= amountOfAKind) { 
						return true; 
					} 
				} else {
					countAmount = 0;
				}
			}
		}
		return false;
	}

	private boolean isCategoryFullHouse(int[] dice) {
		int threeOfThese;
		int twoOfThese;
		int countThree = 0;
		int countTwo = 0;
		for (int diceN = 0; diceN < N_DICE; diceN++) {
			for (threeOfThese = 1; threeOfThese <= 6; threeOfThese++) {
				if (dice[diceN] == threeOfThese) {
					countThree = countThree + 1;
				}
			}
			for (twoOfThese = 1; twoOfThese <= 6; twoOfThese++) {
				if (dice[diceN] == twoOfThese) {
					countTwo = countTwo + 1;

				}
			}
			if (threeOfThese != twoOfThese && threeOfThese == 3 && twoOfThese == 2) {
				return true;
			}
		}
		return false;
	}

	private boolean isCategoryStraight(int[] dice, int kindOfStraight) {
		int countStreak = 0;
		for (int diceN = 0; diceN < N_DICE; diceN++) {
			for (int i = 1; i <= 5; i++) {
				if (dice[diceN] != 5 && dice[diceN] == i && dice[diceN + 1] == i + 1) {
					countStreak = countStreak + 1;
					if (countStreak >= kindOfStraight) {
						return true;
					}
				} else { 
					countStreak = 0;
				}
			}
		}
		return false;
	}

	// checks chosen category and edits scorecard
	private void playerCheckCategory(int player) {
		int category = display.waitForPlayerToSelectCategory();
		// if there are bugs, replace myMagicStub with display.yahtzeeMagicStub etc
		boolean p = myMagicStub(dice, category);
		int score = 0;
		
		// if the player chooses a taken category he can choose again
		if (isUsed[category - 1][player - 1] == true) {
			replay(player);
		}

		// if the player chooses a valid category the score will be updated 
		// (in the arrays and on the scoreboard)
		if (p == true && isUsed[category - 1][player - 1] == false) {
			score = returnScore(category);
			scoreCard[category - 1][player - 1] = score;
			isUsed[category - 1][player - 1] = true;
			display.updateScorecard(category, player, score);
		} 

		// if a player chooses a wrong category they will get zero points
		if (p == false && isUsed[category - 1][player - 1] == false) {
			score = 0;
			isUsed[category - 1][player - 1] = true;
			display.updateScorecard(category, player, score);
		}
	}

	private void replay(int player) {
		playerCheckCategory(player);
	}

	private int returnScore(int category) {
		if (category <= 6) {
			int score = checkNumberScore(category);
			return score;
		}
		int score = 0;
		switch(category) {
			case THREE_OF_A_KIND: 	
				score = scoreAmountOfAKind();
				return score;
			
			case FOUR_OF_A_KIND: 	
				score = scoreAmountOfAKind();
				return score;
			
			case FULL_HOUSE: 		
				return 25;
			
			case SMALL_STRAIGHT: 	
				return 30;
			
			case LARGE_STRAIGHT: 	
				return 40;
			
			case YAHTZEE: 			
				return 50;
			
			case CHANCE: 
				score = scoreAmountOfAKind();
				return score;
		}
		return score;
	}

	// check score for categories ONES to SIXES
	private int checkNumberScore(int category) {
		int score = 0;
		for (int i = 0; i < N_DICE; i++) {
			if (dice[i] == category) {
				score = score + category;
			} 
		}
		return score;
	}

	// calculate score for any category considering an amount of dice with the same value
	private int scoreAmountOfAKind() {
		int score = 0;
		for (int i = 0; i < N_DICE; i++) {
			score = score + dice[i];
		}
		return score;
	}
	// calculate upper score
	private void createUpperScore(int player) {
		for (int i = 0; i < 6; i++) {
			upperscore = upperscore + scoreCard[i][player - 1];
			scoreCard[UPPER_SCORE][player - 1] = upperscore;
			display.updateScorecard(UPPER_SCORE, player, upperscore);
		}
	}

	// calculate upper bonus
	private void createUpperBonus(int player) {
		if (scoreCard[UPPER_SCORE][player - 1] >= 20) {
			bonusscore = 35;
		} 
		display.updateScorecard(UPPER_BONUS, player, bonusscore);
	}

	private void createLowerScore(int player) {
		for (int i = THREE_OF_A_KIND - 1; i < LOWER_SCORE - 1; i++) {
			lowerscore = lowerscore + scoreCard[i][player - 1];
			scoreCard[LOWER_SCORE][player - 1] = lowerscore;
			display.updateScorecard(LOWER_SCORE, player, lowerscore); 
		}
	}

	private void createTotal(int player) {
		int totalscore = lowerscore + upperscore + bonusscore;
		display.updateScorecard(TOTAL, player, totalscore);
	}

	// check which dice the player selected
	private void playerSelectDie() {
		display.printMessage("Please select one or more dice.");
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i) == true) {
				dice[i] = rgen.nextInt(1, 6);
				display.displayDice(dice);
			}
		}
	}

	// initiate the players first roll
	private void playerFirstRoll(int player) {
		display.printMessage("Please roll your dice.");
		display.waitForPlayerToClickRoll(player);
		rollDice();
		display.displayDice(dice);
	}

	// generate a random number for the dice to show 
	private void rollDice() {
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
	}
}
