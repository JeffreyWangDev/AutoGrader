////////////////////////////////////////////////////////////////////////////////
//345678901234567890123456789012345678901234567890123456789012345678901234567890
//
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            PowerBall
// Class:            Test block
//
// Author:           Test user
// 
//////////////////// STUDENTS WHO GET HELP FROM ANYONE//////////////////////////
//                   must fully acknowledge and credit those sources of help.
//                   The Instructor does not have to be credited here,
//                   but TAs, tutors, relatives, strangers, etc do.
//                           In addition, STUDENTS WHO HELP ANYONE must document that 
//                              here in this section.
//
// Persons from whom you received help:
//                       Identify persons by name, relationship to you.
//                   Describe in DETAIL the ideas and help they provided.
//
// Persons for whom you gave help:
//                       Identify persons by name, relationship to you.
//                   Describe in DETAIL the ideas and help you provided.
//
// Online sources:   avoid web searches to solve your problems, but if you do
//                   search, BE SURE  to include Web URLs and description of 
//                   of any information you find.
//
////////////////////////////////////////////////////////////////////////////////

package jeffrey;

import java.util.Scanner;

public class PowerBall {
    private int[] numbers;
    private int powerBall;
    
    public PowerBall(int[] numbers, int powerBall) {
        this.numbers = numbers;
        this.powerBall = powerBall;
    }
    
    public String returnString() {
        String result = "";
        for (int i = 0; i < numbers.length; i++) {
            result += numbers[i] + "\t";
        }
        return result + "Power Ball: "+powerBall;
    }
    
    public int cheekWinnings(PowerBall other) {
        int winnings = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] == other.numbers[i]) {
                winnings+=50;
            }
        }
        if (powerBall == other.powerBall) {
            winnings+=500;
        }
        return winnings;
    }
    
    public static PowerBall generateRandom() {
        int[] numbers = new int[5];
        for (int i = 0; i < 5; i++) {
            numbers[i] = (int) (Math.random() * 59) + 1;
        }
        int powerBall = (int) (Math.random() * 39) + 1;
        return new PowerBall(numbers, powerBall);
    }
    
    public static void main(String[] args) {

        PowerBall winningNumbers = PowerBall.generateRandom();
        System.out.println("The winning number combination is: ");
        System.out.println(winningNumbers.returnString());
        int[] playerNumbers = new int[5];
        int playerPowerBall = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your 5 numbers: ");
        
        for (int i = 0; i < 5; i++) {
            int num = scanner.nextInt();
            if (num < 1 || num > 59) {
                System.out.println("Invalid number. "
                        + "Please enter a number between 1 and 69.");
                i--;
                throw new IllegalArgumentException();
            }
            playerNumbers[i] = num;
        }
        System.out.println("Enter your power ball: ");
        playerPowerBall = scanner.nextInt();
        PowerBall player = new PowerBall(playerNumbers, playerPowerBall);
        System.out.println("Your number combination is: ");
        System.out.println(player.returnString());
        int winnings = winningNumbers.cheekWinnings(player);
        if (winnings == 0) {
            System.out.println("Sorry, you did not win.");
        } else {
            System.out.println("Congratulations! You won: $" + winnings);
        }
        
    }
}
