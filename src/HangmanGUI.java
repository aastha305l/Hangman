import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class HangmanGUI extends Application {

    private String currentWord; // the randomly selected word
    private TextField guessField; // the user enters their guess here
    private Text currentWordText; // show the current word (with - for unguessed letters)
    private Text outcomeText; // show the outcome of each guess and the game
    private Text wrongGuessesText; // show a list of incorrect guesses
    private Text wrongGuessNumberText; // show how many incorrect guesses (or how many guesses remain)
    private final static int MAX_WRONG_GUESSES = 7;
    private static final Color TITLE_AND_OUTCOME_COLOR = Color.rgb(221, 160, 221);
    private static final Color INFO_COLOR = Color.rgb(224, 255, 255);
    private static final Color WORD_COLOR = Color.rgb(224, 255, 255);


    private String welcomeMessage = "Welcome to the game of Hangman." + "\nGuess a letter:";
    private String improperInputString = "Incorrect input or character" + "\nhas been guessed.";
    private final int MAX_LENGTH_OF_INPUT = 1;
    private int incorrectGuessCounter = 0;
    private int correctGuessCounter = 0;
    ArrayList<Character> correctGuesses = new ArrayList<>();
    ArrayList<Character> wrongGuesses = new ArrayList<>();
    public static ArrayList<Character> tokenedWord;
    public static ArrayList<Character> blanks = new ArrayList<>();




    public void start(Stage primaryStage) {

        VBox mainVBox = new VBox();
        mainVBox.setStyle("-fx-background-color: royalblue");
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setSpacing(10);

        Text welcomeText = new Text("Welcome to Hangman!");
        welcomeText.setFont(Font.font("Helvetica", FontWeight.BOLD, 36));
        welcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
        Text introText1 = new Text("Guess a letter.");
        Text introText2 = new Text("You can make " + MAX_WRONG_GUESSES + " wrong guesses!");
        introText1.setFont(Font.font("Helvetica", 24));
        introText1.setFill(INFO_COLOR);
        introText2.setFont(Font.font("Helvetica", 24));
        introText2.setFill(INFO_COLOR);

        VBox introBox = new VBox(welcomeText, introText1, introText2);
        introBox.setAlignment(Pos.CENTER);
        introBox.setSpacing(10);
        mainVBox.getChildren().add(introBox);

        // create before game is started
        outcomeText = new Text("");
        guessField = new TextField();
        wrongGuessNumberText = new Text("");


        try {
            currentWord = chooseWord();
        } catch (IOException e) {
            System.out.println("Something went wrong.");
        }


        currentWordText = new Text(welcomeMessage);
        wrongGuessesText = new Text("Wrong Guesses: []");


        currentWordText.setFont(Font.font("Helvetica", FontWeight.BOLD, 18));
        currentWordText.setFill(WORD_COLOR);
        HBox currentBox = new HBox(currentWordText);
        currentBox.setAlignment(Pos.CENTER);
        currentBox.setSpacing(10);
        mainVBox.getChildren().add(currentBox);

        Text guessIntroText = new Text("Enter your guess: ");
        guessIntroText.setFont(Font.font("Helvetica", 26));
        guessIntroText.setFill(INFO_COLOR);
        guessField.setOnAction(this::handleGuessField);
        HBox guessBox = new HBox(guessIntroText, guessField);
        guessBox.setAlignment(Pos.CENTER);
        guessBox.setSpacing(10);
        mainVBox.getChildren().add(guessBox);

        outcomeText.setFont(Font.font("Helvetica", 28));
        outcomeText.setFill(TITLE_AND_OUTCOME_COLOR);
        HBox outcomeBox = new HBox(outcomeText);
        outcomeBox.setAlignment(Pos.CENTER);
        outcomeBox.setSpacing(10);
        mainVBox.getChildren().add(outcomeBox);

        wrongGuessesText.setFont(Font.font("Helvetica", 24));
        wrongGuessesText.setFill(INFO_COLOR);
        HBox wrongGuessesBox = new HBox(wrongGuessesText);
        wrongGuessesBox.setAlignment(Pos.CENTER);
        wrongGuessesBox.setSpacing(10);
        mainVBox.getChildren().add(wrongGuessesBox);

        wrongGuessNumberText.setFont(Font.font("Helvetica", 24));
        wrongGuessNumberText.setFill(INFO_COLOR);
        HBox wrongGuessNumberBox = new HBox(wrongGuessNumberText);
        wrongGuessNumberBox.setAlignment(Pos.CENTER);
        mainVBox.getChildren().add(wrongGuessNumberBox);

        Scene scene = new Scene(mainVBox, 550, 500);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    private void handleGuessField(ActionEvent event) {
        // YOUR CODE HERE
        String usrInput = guessField.getText();

        try {

            tokenedWord = tokeningTool();

            System.out.println(tokenedWord.toString());

            if (blanks.size() == 0) {
                setBlanks(blanks);
            }

            Character letter = usrInput.charAt(0);

            if (((tokenedWord.contains(letter) || letterEquivalence(letter)) &&
                    incorrectGuessCounter < tokenedWord.size() &&
                    !correctGuesses.contains(letter)) &&
                    !(usrInput.length() > MAX_LENGTH_OF_INPUT))
            {
                addLetters(letter);
                correctGuessCounter++;
                outcomeText.setText(correctOutputDisplay());
                System.out.println("correct Guesses: " + correctGuesses.toString());
                placeLetterInBlanks(letter);

            } else if (!tokenedWord.contains(letter) && !checkForSymbols(letter) &&
                    !(usrInput.length() > MAX_LENGTH_OF_INPUT))
            {
                incorrectGuessCounter++;
                wrongGuesses.add(letter);
                wrongGuessesText.setText(wrongGuessesOutputString());
                wrongGuessNumberText.setText("Number of incorrect guesses: " + incorrectGuessCounter);
                System.out.println("incorrect Guesses: " + wrongGuesses.toString());

            } else if (wrongGuesses.contains(letter) || letterEquivalence(letter)) {
                outcomeText.setText(improperInputString);

            } else if (usrInput.length() > MAX_LENGTH_OF_INPUT) {
                throw new InputTooLongException();
            } else if(checkForSymbols(letter)) {
                throw new ImproperInputException();
            }

        } catch(InputTooLongException e1) {
            outcomeText.setText(improperInputString + " Input too long.");
            e1.getStackTrace().toString();
        } catch(ImproperInputException e2) {
            outcomeText.setText(improperInputString + "\nInput was a symbol\n" + "or a number.");
            e2.getStackTrace();
        } catch(StringIndexOutOfBoundsException e3) {
            outcomeText.setText(improperInputString + "\nNo input was provided.");
            e3.getStackTrace();
        }


        currentWordText.setText(blanksToString(blanks));

        guessField.clear();


        if(correctGuessCounter == blanks.size()) {
            outcomeText.setText("Congratulations you've won!");
            guessField.setDisable(true);

        } else if(incorrectGuessCounter == MAX_WRONG_GUESSES) {
            outcomeText.setText("Sorry you lost, try again next time.\n" +
                                "Your word was: " + currentWord);
            guessField.setDisable(true);

        }


    }

    public void placeLetterInBlanks(Character letter) {
        String comparisonChar;
        String theLetter = letter.toString();
        Character temp;

        for(int i = 0; i < tokenedWord.size(); i++) {
            comparisonChar = tokenedWord.get(i) + "";
            if(comparisonChar.equalsIgnoreCase(theLetter)) {
                temp = tokenedWord.get(i);
                blanks.set(i, temp);
            }
        }


    }

    private boolean letterEquivalence(Character letter) {

        String comparisonChar;
        String theLetter = letter.toString();
        boolean isEqual = false;

        for (int i = 0; i < tokenedWord.size(); i++) {

            comparisonChar = tokenedWord.get(i) + "";


            if(comparisonChar.equalsIgnoreCase(theLetter)) {
                isEqual = true;
            }

        }


        return isEqual;


    }

    private String correctOutputDisplay() {
        String s = "Correct Guesses: ";

        for(Character letters : correctGuesses) {
            s += letters + ", ";
        }

        return s;
    }


    private String wrongGuessesOutputString() {

        String s = "Wrong Guesses: ";

        for(Character letters : wrongGuesses) {
            s += letters + ", ";
        }

        return s;

    }

    private void addLetters(Character letter){

        String comparisonChar = letter.charValue() + "";
        String temp = comparisonChar.toUpperCase();
        char x = temp.charAt(0);
        Character theLetter = (Character) x;


        for (int i = 0; i < tokenedWord.size(); i++) {

            comparisonChar = tokenedWord.get(i) + "";


            if(comparisonChar.equals(theLetter)) {
                correctGuesses.add(letter);
            } else if(!comparisonChar.equals(theLetter) && !correctGuesses.contains(letter)) {
                correctGuesses.add(letter);
            }
        }

    }

    private boolean checkForSymbols(Character letter) {
        boolean letterIsASymbol = false;
        String symbols = "!@#$%^&*(()_-+={}|[]1234567890";
        char[] symbolArray = symbols.toCharArray();
        ArrayList<Character> symbolArrayList = new ArrayList<>();

        for(char sign : symbolArray) {
            symbolArrayList.add(sign);
        }

        if(symbolArrayList.contains(letter)) {
            letterIsASymbol = true;
        }

        return letterIsASymbol;

    }

    private ArrayList<Character> tokeningTool () {

        ArrayList<Character> tokenedWord = new ArrayList<>();

        char[] currWordCharacters;
        currWordCharacters = currentWord.toCharArray();

        for (char letter : currWordCharacters) {
            tokenedWord.add(letter);
        }

        return tokenedWord;
    }

    private static void setBlanks(ArrayList<Character> blanks) {
        for(int i = 0; i < tokenedWord.size(); i++) {
            blanks.add('-');
        }
    }

    private String blanksToString(ArrayList<Character> blanks) {
        String s = "";

        for(Character space: blanks) {
            s+= space;
        }

        return s;
    }


    private String chooseWord () throws IOException {

        Scanner read = new Scanner(new FileReader(new File("words.txt")));
        ArrayList<String> word = new ArrayList<>();

        while (read.hasNext()) {
            word.add(read.nextLine());
        }

        String randomWord = word.get((int) (Math.random() * word.size()));

        return randomWord;
    }

    public static void main (String[]args){


        launch(args);


    }

}
