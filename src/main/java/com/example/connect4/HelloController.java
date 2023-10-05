package com.example.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.effect.Light;
import javafx.scene.effect.Shadow;
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

public class HelloController implements Initializable {

    private static final int COLUMNS = 7;
    private static final int Rows = 6;
    private static final int Circle_DIAMETER = 80;
    private static final String discColor1 = "#24303E";
    private static final String discColor2 = "#4CAA88";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    private Disc[][] insertedDiscArray = new Disc[Rows][COLUMNS]; // For stuctural changes for the developer

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiscsPane;

    @FXML
    public Label playerNameLabel;
    @FXML
    public TextField playerOneTextField, playerTwoTextField;
    @FXML
    public Button setNamesButton;
    private  boolean isAllowedToInsert = true;

    public void createPlayground(){

        setNamesButton.setOnAction(actionEvent -> {
            PLAYER_ONE = playerOneTextField.getText();
            PLAYER_TWO = playerTwoTextField.getText();
        });

        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0,1);

        List<Rectangle> rectangleList = createClickableColumns();

        for(Rectangle rectangle: rectangleList){
            rootGridPane.add(rectangle, 0, 1);
        }

    }
    private Shape createGameStructuralGrid(){
        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * Circle_DIAMETER,(Rows + 1) * Circle_DIAMETER);

        for (int row=0; row<Rows; row++){
            for (int col=0; col<COLUMNS; col++){
                Circle circle = new Circle();
                circle.setRadius(Circle_DIAMETER / 2);
                circle.setCenterX(Circle_DIAMETER / 2);
                circle.setCenterY(Circle_DIAMETER / 2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (Circle_DIAMETER + 5) + Circle_DIAMETER / 4);
                circle.setTranslateY(row * (Circle_DIAMETER + 5) + Circle_DIAMETER /4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }
        }

        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;
    }

    private List<Rectangle> createClickableColumns(){

        List<Rectangle> rectangleList = new ArrayList<>();
        for(int col=0; col<COLUMNS; col++){

            Rectangle rectangle = new Rectangle(Circle_DIAMETER, (Rows + 1) * Circle_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (Circle_DIAMETER + 5) + Circle_DIAMETER / 4);

            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                if(isAllowedToInsert){
                    isAllowedToInsert = false;
                    insertedDiscsPane(new Disc(isPlayerOneTurn), column);
                }

            });
            rectangleList.add(rectangle);
        }
        return rectangleList;
    }
    private void insertedDiscsPane(Disc disc, int column){

        int row = Rows - 1;
        while (row>=0){
            if (getDiscIfPresent(row, column) == null)
                break;
            row--;
        }
        if (row < 0)
            return;

        insertedDiscArray[row][column] = disc; // For the structural changes : for the developer
        insertedDiscsPane.getChildren().add(disc);

        disc.setTranslateX(column * (Circle_DIAMETER + 5) + Circle_DIAMETER / 4);

        int currentRow = row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (Circle_DIAMETER + 5) + Circle_DIAMETER /4);
        translateTransition.setOnFinished(actionEvent -> {

            isAllowedToInsert = true;
            if(gameEnded(currentRow, column)){
                gameOver();
                return;
            }
            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });
        translateTransition.play();
    }
    private boolean gameEnded(int row, int column){


        // Vertical points
        List<Point2D>verticalPoints = IntStream.rangeClosed(row-3,row + 3)   // range of row values = 0,1,2,3,4,5
                                      .mapToObj(r -> new Point2D(r,column))
                                      .collect(Collectors.toList()); // 0,3 1,3 2,3 3,3 4,3 5,3 --> point2D x,y

        // Horizontal Points
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column -3, column + 3)
                                       .mapToObj(col -> new Point2D(row,col))
                                       .collect(Collectors.toList());

       Point2D startPoint1 = new Point2D(row - 3,column + 3);
        List<Point2D> diagonal1Point = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i,-i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3,column - 3);
        List<Point2D> diagonal2Point = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i,i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                || checkCombinations(diagonal1Point) || checkCombinations(diagonal2Point);
        return isEnded;
    }
    private boolean checkCombinations(List<Point2D> points){

        int chain = 0;

        for (Point2D point: points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn){

                chain++;
                if(chain==4){
                    return true;
                }
            } else {
                chain = 0;
            }
        }
        return false;
    }
    private Disc getDiscIfPresent(int row, int column){
        if(row >= Rows || row < 0 || column >=COLUMNS || column < 0)
            return null;
            return insertedDiscArray[row][column];
    }
    private void gameOver(){
        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is: " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("The winner is " + winner);
        alert.setContentText("Want to play again");

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("N0, Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater( () -> {

            Optional<ButtonType> btnClicked = alert.showAndWait();
            if (btnClicked.isPresent() && btnClicked.get() == yesBtn){
                // ...user chose yes so Reset the game
                resetGame();
            } else {
                //... user chose No.. so Exit the game
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        insertedDiscsPane.getChildren().clear();
        for (int row=0; row< insertedDiscArray.length; row++){
            for (int col=0; col< insertedDiscArray[row].length; col++){
                insertedDiscArray[row][col] = null;
            }
        }
        isPlayerOneTurn = true; // Let Player  start the game
        playerNameLabel.setText(PLAYER_ONE);

        createPlayground(); // Prepare a fresh playground
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;
        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(Circle_DIAMETER / 2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1): Color.valueOf(discColor2));

            setCenterX(Circle_DIAMETER/2);
            setCenterY(Circle_DIAMETER/2);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}