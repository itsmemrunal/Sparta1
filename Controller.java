package com.internshala.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	private static final int columns=7;
	private static final int rows=6;
	private static final int circle_d = 80;
	private static final String disc1 = "#24303E";
	private static final String disc2 = "#4CAA88";


	private static String player_1= "Player 1";
	private static String player_2 = "Player 2";

	private boolean isPlayer1Turn= true;
	private Disc [][] insertDiscArray = new Disc[rows][columns];//for developers

	@FXML
	public GridPane rootGridPane;
	@FXML
	public Pane insertedDiscPane;
	@FXML
	public Label playerNameLable;
	private boolean isAllowedToInsert = true;   //flag to avoid same colour disk multiple times being added

	public void createPlayGround(){
		Shape rectangleWidthHoles = createGameStructuralGrid();
	    rootGridPane.add(rectangleWidthHoles,0,1);

	    List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle:rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
	}


    private Shape createGameStructuralGrid(){
	    Shape rectangleWidthHoles = new Rectangle((columns + 1)*circle_d,(rows+1)*circle_d);
	    for (int row =0; row<rows; row++){
		    for(int col =0; col<columns; col++){
			    Circle circle = new Circle();
			    circle.setRadius(circle_d/2);
			    circle.setCenterX(circle_d/2);
			    circle.setCenterY(circle_d/2);
			    circle.setSmooth(true);
			    circle.setTranslateX(col*(circle_d + 5)+ circle_d/4);
			    circle.setTranslateY(row *(circle_d + 5)+ circle_d/4);
			    rectangleWidthHoles = Shape.subtract(rectangleWidthHoles,circle);

		    }
	    }
	    rectangleWidthHoles.setFill(Color.WHITE);
	    return rectangleWidthHoles;
    }
    private List<Rectangle> createClickableColumns(){
	    List<Rectangle> rectangleList = new ArrayList<>();

		for (int col =0; col<columns ;col++){
			Rectangle rectangle =new Rectangle(circle_d,(rows+1)*circle_d);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(circle_d + 5)+ circle_d/4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			 final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert){
					isAllowedToInsert=false;       //when the disc is being inserted then no more disc wil be added
					insertedDisc(new Disc(isPlayer1Turn),column);
				   }
				});

			rectangleList.add(rectangle);

		}

		return rectangleList;
    }
    private  void insertedDisc(Disc disc, int column){
		int row = rows-1;
		while (row>=0) {
			if (getDiscIfPresent(row,column) == null)
				break;

			row--;
		}
			if(row<0)
				return;


      insertDiscArray[row][column]= disc;
      insertedDiscPane.getChildren().add(disc);
        disc.setTranslateX(column*(circle_d + 5)+ circle_d/4);
        int currentrow =row;
	    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row *(circle_d + 5)+ circle_d/4);

        translateTransition.setOnFinished(event -> {
        	isAllowedToInsert=true;

        	if(gameEnded(currentrow,column)){
              gameOver();
	        }

        	isPlayer1Turn = !isPlayer1Turn;
        	playerNameLable.setText(isPlayer1Turn?player_1:player_2);
        });
        translateTransition.play();
    }

	private  boolean gameEnded(int row, int column) {
		//vertical points:- eg. player has inserted his disc at row = 2,column = 3
		//range of row values = 0,1,2,3,4,5
		// index of each element present in column [row][column] : 0,3 1,3 2,3 3,3 4,3 5,3 -> class hold all this
		//values is ->Point 2D x,y
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3 ,row +3)//range of row values = 0,1,2,3,4,5
				                       .mapToObj(r->new Point2D(r,column))// 0,3 1,3 2,3 3,3 4,3 5,3 -> class hold all this
				                        .collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3 ,column +3)
				.mapToObj(col->new Point2D(row,col))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3,column+3);
		List<Point2D>diagonal1Points = IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3,column-3);
		List<Point2D>diagonal2Points = IntStream.rangeClosed(0,6)
				.mapToObj(i->startPoint2.add(i,i))
				.collect(Collectors.toList());

         boolean isEnded= checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
		         || checkCombinations(diagonal1Points) ||checkCombinations(diagonal2Points);

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for (Point2D point : points) {

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

			if (disc != null && disc.isPlayer1Move == isPlayer1Turn) {
				chain++;
				if (chain == 4) {
					return true;
				}
			}else {
				chain = 0;
			}
		}
		return false;
	}
	private Disc getDiscIfPresent(int row,int column){
		//ArrayIndexOutOfBoundException
		if(row>=rows|| row<0 || column>=columns||column <0) // if row or column index is invalid
		  return null;

		return insertDiscArray[row][column];
	}

	private void gameOver(){
         String winner = isPlayer1Turn? player_1: player_2;
		 System.out.println("winner is " + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("The winner is "+ winner);
		alert.setContentText("Want to play again?");
		ButtonType yesBtn = new ButtonType("Yes");

		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(()->{

			Optional<ButtonType>btnClicked = alert.showAndWait();
			if (btnClicked.isPresent()&&btnClicked.get()==yesBtn){
				//user chose yes so reset game
				resetGame();
			}else {
				//user chose no so exit the game
				Platform.exit();
				System.exit(0);
			}

		});

	}

	public void resetGame() {
     insertedDiscPane.getChildren().clear();  //remove all inserted disc from pane
		for (int row=0;row<insertDiscArray.length; row++){
			for (int col =0;col<insertDiscArray[row].length;col++){
				insertDiscArray[row][col]=null;
			}

		}
		isPlayer1Turn = true;  //let player start the game
		playerNameLable.setText("player_1");

		createPlayGround();//prepare  fresh playground
	}

	private static class Disc extends Circle{
		private final boolean isPlayer1Move;
		public Disc(boolean isPlayer1Move){
			this.isPlayer1Move = isPlayer1Move;
			setRadius(circle_d/2);
			setFill(isPlayer1Move?Color.valueOf(disc1):Color.valueOf(disc2));
			setCenterX(circle_d/2);
			setCenterY(circle_d/2);
		}
    }
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
