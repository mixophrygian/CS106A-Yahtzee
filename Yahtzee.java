/*
 * File: Yahtzee.java
 * ------------------
 * This program will play Yahtzee!  From the CS106a assignment in Programming Methodlogy, 
 * ported to iTunes U from the 2007 Stanford course.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;
import java.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
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
		
		initializeUsedCategories();
		
		for(int i = 0; i < (nPlayers * N_SCORING_CATEGORIES); i++){
			
			setCurrentPlayer();
			firstRoll();
			secondAndThirdRoll();
			selectACategory();
			evaluteDice(diceArray);
			checkCategory(category);
			display.updateScorecard(category, playerCounter, score);
			updateRunningTotal();
			setNextPlayer();
			
		}
		declareWinner();
	}


private void declareWinner() {
		//Compares the three total scores for each player to determine the winner, displays it.  Yahoo.//
	int highScore = 0;
	int winnerIndex = 0;
	for(int i =0; i< nPlayers; i++){
		if(playersTotalScore[i] > highScore){
			highScore=playersTotalScore[i];
			winnerIndex = i;
		}
	}
	winningPlayer = playerNames[winnerIndex];
	display.printMessage(winningPlayer+ " wins with a high score of " +highScore+"!");
	}

private void updateRunningTotal() {
		/*Updates the running total of a player's score during the game, including add an upper bonus if
		 * all of the first six categories have been selected. */
	
	playersTotalScore[playerCounter-1] += score;
	
	totaler =0;
	for(int i = 0; i<= 6; i++){
		if(usedCategories[playerCounter-1][i] != -1){
			totaler += usedCategories[playerCounter-1][i];
		}
	}
	display.updateScorecard(UPPER_SCORE, playerCounter, totaler);
	if(totaler >= 63){
		addUpperBonus();
	}
	
	display.updateScorecard(TOTAL, playerCounter, (playersTotalScore[playerCounter-1]));
	
}

private void addUpperBonus() {
	// adds the upper bonus after the first six categories have been selected. That condition is checked in updateRunning Total//
	int bonus = 35;
	display.updateScorecard(UPPER_BONUS, playerCounter, bonus);
	playersTotalScore[playerCounter-1] += bonus;
	display.updateScorecard(TOTAL, playerCounter, (playersTotalScore[playerCounter-1]));
	
}

private void initializeUsedCategories() {
		/*Creates a 2D array that stores the used and unused categories for each player 
		 * so no one can re-use any category.  Also initializes the "total score" category for each player.*/
	
	usedCategories  = new int[nPlayers][N_CATEGORIES];
	for(int i = 0; i < nPlayers; i++){
		for(int y = 0; y < N_CATEGORIES; y++){
			usedCategories[i][y] = -1;
		}
	}
	playersTotalScore = new int[nPlayers];
	for(int i = 0; i < nPlayers; i++){
		playersTotalScore[i]=0;
	}
}

private void resetVariables() {
		//This method resets variables so that the next player starts from a clean slate in terms of dice Eligibility.//

	thereAreThreeMatches = false;
	yahtzee = false;
	fourOfAKind = false;
	fullHouse = false;
	threeOfAKind = false;
	eligibleCategories.clear();
	score = 0;
	
	}

private void evaluteDice(int[] dice) {
		/* Evaluates the dice and their eligible categories*/
		
		checkForMatches();
		countSingleOccurances();
		
		/*Enables the Chance category*/
		eligibleCategories.add(15);
		
		if(thereAreThreeMatches){
			eligibleCategories.add(9);
			if(fullHouse){
				eligibleCategories.add(11);
			}
		}
		if(yahtzee){
			eligibleCategories.add(14);
			}
		if(fourOfAKind){
			eligibleCategories.add(10);
		}
		if(!thereAreThreeMatches){
			checkForStraights();
		}
		
	}

private void checkForStraights() {
	// The first two if statements qualify a dice configuration for a large (and small) straight//
	if (ONE >=1 && TWO >=1 && THREE >= 1 && FOUR >=1 && FIVE>=1){
		eligibleCategories.add(13);
		eligibleCategories.add(12);
		}
	if(TWO >=1 && THREE >= 1 && FOUR >=1 && FIVE >=1 && SIX >= 1){
		eligibleCategories.add(13);
		eligibleCategories.add(12);
		}

if (ONE >=1 && TWO >=1 && THREE >= 1 && FOUR >=1){
	eligibleCategories.add(12);
}
if (TWO >=1 && THREE >=1 && FOUR >= 1 && FIVE >=1){
	eligibleCategories.add(12);
}
if (THREE >=1 && FOUR >=1 && FIVE >= 1 && SIX >=1){
	eligibleCategories.add(12);
}
}

private void countSingleOccurances() {
	// Evaluates the dice for occurrences of numbers, regardless of matches//
	ONE = diceEvaluator[0];
	TWO = diceEvaluator[1];
	THREE = diceEvaluator[2];
	FOUR = diceEvaluator[3];
	FIVE = diceEvaluator[4];
	SIX = diceEvaluator[5];
	
	if(ONE > 0){
		eligibleCategories.add(1);
	}
	if(TWO > 0) {
		eligibleCategories.add(2);
	}
	if(THREE > 0) {
		eligibleCategories.add(3);
	}
	if(FOUR > 0) {
		eligibleCategories.add(4);
	}
	if(FIVE > 0) {
		eligibleCategories.add(5);
	}
	if(SIX > 0) {
		eligibleCategories.add(6);
	}
}

private void checkForMatches() {
	//Evaluates dice for matches using an Array diceEvaluator that counts the iterations of each possible value//
	
	for(int i = 0; i<6; i++){
		diceEvaluator[i] = 0;
	}
	
	for(int i = 0; i<N_DICE; i++){
	    int diceNumber = 0;
	    diceNumber = diceArray[i];	
	    diceEvaluator[diceNumber-1]++;
	       }
	
	for(int i=0; i<6; i++){
		
		if(diceEvaluator[i] >= 3){
			thereAreThreeMatches = true;
			threeOfAKind = true;
			for(int y=0; y<6; y++){
				if(diceEvaluator[y] == 2){
					fullHouse = true;
				}
			}
		}
		if(diceEvaluator[i] >= 4){
			fourOfAKind = true;
		}
		if(diceEvaluator[i] == 5){
			yahtzee = true;
		}
	}	
}

private void checkCategory(int selectedCategory) {
		/* Checks whether or not the selected category is 
		 * eligible for a non-zero score based upon whether 
		 * or not the dice configuration meets the criteria for a
		 *  non-zero score and if the category has been selected or not
		 *   in the "usedCategories" 2D array.  playerCounter-1 has to be used because the yahtzee class gets
		 *   fussy when playerCounter = 0 in one of its methods, so it's initialized at 1.  */
		
		switch(selectedCategory) {
		
		case 1: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = diceEvaluator[0];
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 2: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = diceEvaluator[1] * 2;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
				selectAvailableCategory();}
				break;
			
		case 3: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = diceEvaluator[2] * 3;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
		
		case 4:
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = diceEvaluator[3] * 4;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
		
		case 5: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = diceEvaluator[4] * 5;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
		
		case 6:
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = diceEvaluator[5] * 6;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 9: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
			score = sumOfAll();
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 10:
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
			score = sumOfAll();
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 11: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = 25;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 12: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
			score = 30;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 13: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
			score = 40;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 14: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
				score = 50;
				usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		case 15: 
			if(usedCategories[playerCounter-1][selectedCategory] == -1){
			if(eligibleCategories.contains(selectedCategory)) {
			score = sumOfAll();
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else score = 0;
			usedCategories[playerCounter-1][selectedCategory] = score;
			}else{
			selectAvailableCategory();}
			break;
			
		default: break;
}
}

private void selectAvailableCategory() {
	// When a user tries to select a category that has been used they will be insulted and prompted to select an available category//
	display.printMessage("Category unavailable fool!  Pick another.");
	category = display.waitForPlayerToSelectCategory(); 
	checkCategory(category);
}

private int sumOfAll() {
	//Adds the values on all the dice//
	int total = 0;
	for(int i=0; i<N_DICE; i++){
		int adderHelper = 0;
		adderHelper = diceArray[i];
		total+= adderHelper;
	}
	return total;
}

private void selectACategory() {
	display.printMessage("Select a category for your dice results.");
	category = display.waitForPlayerToSelectCategory(); 
	}

private void secondAndThirdRoll() {
	for(int i = 0; i <2; i++){
		display.waitForPlayerToSelectDice(); 
		for(int y = 0; y < N_DICE; y++){
			if (display.isDieSelected(y)){
				diceArray[y] = rgen.nextInt(1,6);
		}
	}
	display.displayDice(diceArray);
	}
}

private void firstRoll(){
	display.waitForPlayerToClickRoll(playerCounter);
	for(int i=0; i < N_DICE; i++){
		diceArray[i] =  rgen.nextInt(1,6);
	}
	display.displayDice(diceArray);
	display.printMessage("You may now select dice for up to two more re-rolls.");
	}

private void setNextPlayer() {
	/*Updates the player counter so that the beginning of the next loop starts 
	 * with the next player's turn.	 */
	if (playerCounter < nPlayers) {
		playerCounter++;
	}else{
		playerCounter = 1;
	}
	playerTurns++;
	resetVariables();
}

private void setCurrentPlayer() {
	String currentPlayer = playerNames[playerCounter-1];
	display.printMessage(currentPlayer + "’s turn.  Roll the dice!");
	}

/* Private instance variables */
	private int nPlayers;
	private int playerCounter = 1;
	private int playerTurns;
	private String[] playerNames;
	private String winningPlayer;
	private YahtzeeDisplay display;
	private int[] diceArray = new int[N_DICE];
	private ArrayList<Integer> eligibleCategories = new ArrayList<Integer>();
	private int[][] usedCategories;
	private int[] playersTotalScore;
	private int score;
	private int category;
	private int[] diceEvaluator = new int[6];
	private boolean thereAreThreeMatches;
	private boolean yahtzee;
	private boolean fourOfAKind;
	private boolean fullHouse;
	private boolean threeOfAKind;
	private int totaler;
	private int ONE;
	private int TWO;
	private int THREE;
	private int FOUR;
	private int FIVE;
	private int SIX;
	private RandomGenerator rgen = new RandomGenerator();

}
