
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.util.Stack;

public class playGame extends Application
{
   final int NUMBER_CARDS = 4;
   Image[] displayCards = new Image[NUMBER_CARDS];
   ImageView[] cardView = new ImageView[NUMBER_CARDS];
   Card[] fourCards = new Card[NUMBER_CARDS];

   // Generates Random Cards
   public Card generateRandomCard()
   {
      int rangeCardVal = 13;
      int rangeSuitVal = 3;
      int randCardVal = (int) (Math.random() * rangeCardVal);
      int randSuitVal = (int) (Math.random() * rangeSuitVal);
      char getChar = GUICard.turnIntIntoCardValueChar(randCardVal);
      Card.Suit getSuit = GUICard.turnIntIntoSuit(randSuitVal);
      return new Card(getChar, getSuit);
   }

   // Gets the four cards
   // Avoids duplicates
   // Fill in Card array with random cards first then check for duplicates
   public void getCards()
   {
      Card newCard;
      int cardsCount = 0;
      int indexCount = 0;

      for (int k = 0; k < fourCards.length; k++)
      {
         fourCards[k] = generateRandomCard();
      }
      while (cardsCount < NUMBER_CARDS)
      {
         boolean duplicate = false;
         boolean cardAdded = false;
         newCard = generateRandomCard();
         for (int i = 0; i < fourCards.length; i++)
         {
            if ((newCard.getValue() == fourCards[i].getValue()) && newCard.getSuit() == fourCards[i].getSuit())
            {
               duplicate = true;
            }
         }
         for (int j = 0; j < fourCards.length; j++)
         {
            if (!duplicate && !cardAdded && indexCount <= 3)
            {
               fourCards[indexCount] = newCard;
               indexCount++;
               cardAdded = true;
            }
         }
         cardsCount++;
         if (duplicate)
         {
            cardsCount--;
         }
      }
   }

   // Displays the cards in the middle
   public void displayCards(HBox cardPane)
   {
      for (int i = 0; i < fourCards.length; i++)
      {
         Image cardImage;
         cardImage = GUICard.getImage(fourCards[i]);
         displayCards[i] = cardImage;
         cardView[i] = new ImageView(cardImage);
         cardPane.getChildren().add(cardView[i]);
      }
   }

   public void start(Stage primaryStage)
   {
      TextField textMessage = new TextField();

      Button shuffleButton = new Button("Shuffle");
      Button verifyButton = new Button("Verify");

      HBox cardPane = new HBox(15);
      cardPane.setPadding(new Insets(80, 100, 80, 100));

      // Get cards and display the cards
      getCards();
      displayCards(cardPane);

      HBox topBox = new HBox(10);
      topBox.setAlignment(Pos.TOP_RIGHT);
      topBox.getChildren().add(shuffleButton);
      EvaluateExpression evalExp = new EvaluateExpression();

      // Evaluate the expression and display the corresponding messages based on what
      // is entered after clicking verify
      verifyButton.setOnAction(e ->
      {
         evalExp.grabIndividualValues(textMessage.getText());
         evalExp.checkExpression(evalExp.evaluateExpression(textMessage.getText()), fourCards);
         final Text displayMessage = new Text(evalExp.showMessage);
         topBox.getChildren().clear();
         topBox.getChildren().addAll(displayMessage, shuffleButton);
      });

      // Displays new cards when Shuffled is clicked
      shuffleButton.setOnAction(e ->
      {
         cardPane.getChildren().clear();
         getCards();
         displayCards(cardPane);
      });

      HBox bottomBox = new HBox(10);
      bottomBox.setAlignment(Pos.CENTER);
      bottomBox.getChildren().addAll(new Label("Enter an expression: "), textMessage, verifyButton);

      BorderPane borderPane = new BorderPane();
      borderPane.setTop(topBox);
      borderPane.setCenter(cardPane);
      borderPane.setBottom(bottomBox);

      Scene scene = new Scene(borderPane, 550, 400);
      primaryStage.setTitle("Card game");
      primaryStage.setScene(scene);
      primaryStage.show();
   }

   public static void main(String[] args)
   {
      launch(args);
   }
}

//This class contains all the methods to check the expression entered
class EvaluateExpression
{
   public String showMessage;
   public char[] grabValues = new char[4];

   // Returns the value of the expression using Stack
   public Double evaluateExpression(String expression)
   {
      Stack<Double> operandStack = new Stack<>();
      Stack<Character> operatorStack = new Stack<>();

      expression = insertBlanks(expression);

      if (expression.length() == 0)
      {
         return 0.0;
      }

      String[] tokens = expression.split(" ");

      for (String token : tokens)
      {
         if (token.length() == 0)
         {
            continue;
         } else if (token.charAt(0) == '+' || token.charAt(0) == '-')
         {
            while (!operatorStack.isEmpty() && (operatorStack.peek() == '+' || operatorStack.peek() == '-'
                  || operatorStack.peek() == '*' || operatorStack.peek() == '/'))
            {
               processAnOperator(operandStack, operatorStack);
            }
            operatorStack.push(token.charAt(0));
         } else if (token.charAt(0) == '*' || token.charAt(0) == '/')
         {
            while (!operatorStack.isEmpty() && (operatorStack.peek() == '*' || operatorStack.peek() == '/'))
            {
               processAnOperator(operandStack, operatorStack);
            }
            operatorStack.push(token.charAt(0));
         } else if (token.trim().charAt(0) == '(')
         {
            operatorStack.push('(');
         } else if (token.trim().charAt(0) == ')')
         {
            while (operatorStack.peek() != '(')
            {
               processAnOperator(operandStack, operatorStack);
            }
            operatorStack.pop();
         } else
         {
            operandStack.push(Double.valueOf(token));
         }
      }
      while (!operatorStack.isEmpty())
      {
         processAnOperator(operandStack, operatorStack);
      }

      return operandStack.pop();
   }

   // Helper method to calculate the expression
   public static void processAnOperator(Stack<Double> operandStack, Stack<Character> operatorStack)
   {
      char op = operatorStack.pop();
      double op1 = operandStack.pop();
      double op2 = operandStack.pop();
      if (op == '+')
      {
         operandStack.push(op2 + op1);
      } else if (op == '-')
      {
         operandStack.push(op2 - op1);
      } else if (op == '*')
      {
         operandStack.push(op2 * op1);
      } else if (op == '/')
      {
         operandStack.push(op2 / op1);
      }
   }

   // Returns the expression with spacing
   public static String insertBlanks(String s)
   {
      String result = "";
      for (int i = 0; i < s.length(); i++)
      {
         if (s.charAt(i) == '(' || s.charAt(i) == ')' || s.charAt(i) == '+' || s.charAt(i) == '-' || s.charAt(i) == '*'
               || s.charAt(i) == '/')
         {
            result += " " + s.charAt(i) + " ";
         } else
         {
            result += s.charAt(i);
         }
      }
      return result;
   }

   // Grabs the numbers in the expression
   public char[] grabIndividualValues(String expression)
   {
      int count = 0;
      expression = insertBlanks(expression);
      String[] tokens = expression.split(" ");
      for (String token : tokens)
      {
         if (token.length() == 0)
         {
            continue;
         } else if (token.length() == 1 && token.charAt(0) == '1')
         {
            grabValues[count] = 'A';
            count++;
         } else if (token.length() == 1 && token.charAt(0) != '+' && token.charAt(0) != '-' && token.charAt(0) != '*'
               && token.charAt(0) != '/' && token.charAt(0) != '(' && token.charAt(0) != ')')
         {
            grabValues[count] = token.charAt(0);
            count++;
         } else if (token.length() == 2 && token.charAt(0) != '+' && token.charAt(0) != '-' && token.charAt(0) != '*'
               && token.charAt(0) != '/' && token.charAt(0) != '(' && token.charAt(0) != ')')
         {
            if (token.charAt(0) == '1' && token.charAt(1) == '0')
            {
               grabValues[count] = 'T';
               count++;
            } else if (token.charAt(0) == '1' && token.charAt(1) == '1')
            {
               grabValues[count] = 'J';
               count++;

            } else if (token.charAt(0) == '1' && token.charAt(1) == '2')
            {
               grabValues[count] = 'Q';
               count++;
            } else if (token.charAt(0) == '1' && token.charAt(1) == '3')
            {
               grabValues[count] = 'K';
               count++;
            }
         }
      }
      return grabValues;
   }

   // Checks if the numbers in the expression match the cards displayed
   public boolean compareCardToExpression(Card[] cardArray)
   {
      int match = 0;
      for (int i = 0; i < cardArray.length; i++)
      {
         for (int j = 0; j < grabValues.length; j++)
         {
            if (cardArray[i].getValue() == grabValues[j])
            {
               grabValues[j] = ' ';
               match++;
            }
         }
      }
      if (match == 4)
      {
         return true;
      } else
      {
         return false;
      }
   }

   // Displays the message for the text field depending on the expression entered
   public boolean checkExpression(double value, Card[] cardArray)
   {
      if (!compareCardToExpression(cardArray))
      {
         showMessage = "The numbers in the expression don't match the numbers in the set";
         return false;
      } else if (value == 24.0)
      {
         showMessage = "Correct";
         return true;
      } else
      {
         showMessage = "Incorrect";
         return false;
      }
   }
}

//This program contains methods to convert a card to an image and converts an image to a card
class GUICard
{
   private static Image[][] imageCards = new Image[13][4]; // 14 = A thru K (+ joker)
   private static ImageView[][] imageCardViews = new ImageView[13][4];
   private static Image imageBack;
   private static ImageView imageCardBack;
   private static boolean imagesLoaded = false;

   private static String cardlValsConvertAssist = "23456789TJQKA";
   private static String suitValsConvertAssist = "CDHS";
   private static Card.Suit suitConvertAssist[] =
   { Card.Suit.clubs, Card.Suit.diamonds, Card.Suit.hearts, Card.Suit.spades };

   // Load all the card images once
   static void loadCardImages()
   {
      String imageFileName;
      int intSuit, intVal;

      for (intVal = 0; intVal < 13; intVal++)
      {
         for (intSuit = 0; intSuit < 4; intSuit++)
         {
            if (!imagesLoaded)
            {
               imageFileName = "file:images/" + turnIntIntoCardValueChar(intVal) + turnIntIntoCardSuitChar(intSuit)
                     + ".gif";
               imageCards[intVal][intSuit] = new Image(imageFileName);
               imageCardViews[intVal][intSuit] = new ImageView(imageCards[intVal][intSuit]);
            }
         }
      }
      imagesLoaded = true;
   }

   // Returns the image of the card
   static public Image getImage(Card card)
   {
      loadCardImages(); // will not load twice, so no worries.
      return imageCards[valueAsInt(card)][suitAsInt(card)];
   }

   // Returns the back of a card image
   static public Image getBackCardImage()
   {
      String imageFileName;
      imageFileName = "file:images/BK.gif";
      imageBack = new Image(imageFileName);
      return imageBack;
   }

   // Get char at index of k in string for image for value (char)
   static char turnIntIntoCardValueChar(int k)
   {
      if (k < 0 || k > 13)
      {
         return '?';
      }
      return cardlValsConvertAssist.charAt(k);
   }

   // Get char at index of k in string for image for suit
   static char turnIntIntoCardSuitChar(int k)
   {
      if (k < 0 || k > 3)
      {
         return '?';
      }
      return suitValsConvertAssist.charAt(k);

   }

   // Get suit at index of k in string for image for suit
   static Card.Suit turnIntIntoSuit(int k)
   {
      if (k < 0 || k > 3)
      {
         return suitConvertAssist[0];
      }
      return suitConvertAssist[k];

   }

   // Get index of string where the char is equal to the value (char) of the card
   static int valueAsInt(Card card)
   {
      int intVal = 0;
      for (int i = 0; i < cardlValsConvertAssist.length(); i++)
      {
         if (cardlValsConvertAssist.charAt(i) == card.getValue())
         {
            intVal = i;
         }
      }
      return intVal;
   }

   // Get index of suit where the element (a suit) is equal to the suit of the card
   static int suitAsInt(Card card)
   {
      int intSuit = 0;
      for (int i = 0; i < suitConvertAssist.length; i++)
      {
         if (suitConvertAssist[i] == card.getSuit())
         {
            intSuit = i;
         }
      }
      return intSuit;
   }
}

//This is the card class
//Each card contains Value and a Suit
class CardIdentity
{

   public enum Suit
   {
      clubs, diamonds, hearts, spades
   }

   private char value;
   private Suit suit;

   public CardIdentity()
   {
      value = 'A';
      suit = Suit.spades;

   }

   public boolean set(char value, Suit suit)
   {
      if (isValid(value, suit))
      {
         this.value = value;
         this.suit = suit;
         return true;
      } else
      {
         return false;
      }
   }

   public char getValue()
   {
      return value;
   }

   public Suit getSuit()
   {
      return suit;
   }

   // Includes the Joker "X"
   private static boolean isValid(char value, Suit suit)
   {
      if (value == 'A' || value == 'K' || value == 'Q' || value == 'J' || value == 'T' || value == '2' || value == '3'
            || value == '4' || value == '5' || value == '6' || value == '7' || value == '8' || value == '9')
      {
         if (suit == Suit.clubs || suit == Suit.diamonds || suit == Suit.hearts || suit == Suit.spades)
         {
            return true;
         }
         return false;
      } else
      {
         return false;
      }
   }
}

class Card extends CardIdentity
{
   private boolean cardError;

   public Card()
   {
      super();
      cardError = false;
   }

   public Card(char value, Suit suit)
   {
      super();
      if (super.set(value, suit))
      {
         cardError = false;
      } else
      {
         value = getValue();
         suit = getSuit();
         cardError = true;
      }
   }

   @Override
   public boolean set(char value, Suit suit)
   {
      if (super.set(value, suit))
      {
         cardError = false;
         return true;
      } else
      {
         cardError = true;
         return false;
      }
   }

   public String toString()
   {
      if (cardError)
      {
         return "Illegal";
      } else
      {
         return getValue() + " of " + getSuit();
      }
   }

   public boolean getcardError()
   {
      return cardError;
   }

   public boolean equals(Card card)
   {
      if (!cardError)
      {
         if (this.getValue() == super.getValue() && this.getSuit() == super.getSuit())
         {
            return true;
         }
         return false;
      } else
      {
         return false;
      }
   }
}