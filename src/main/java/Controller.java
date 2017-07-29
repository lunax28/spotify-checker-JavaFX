import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller{

    ObservableList<String> obsList = FXCollections.observableArrayList("Name","ID");

    @FXML
    private ApiQueryUtils apiQuery;

    @FXML
    private String token;

    @FXML
    private TextArea artistsTextArea;

    @FXML
    private List<String> artistsArray;

    @FXML
    private List<String> linkArray;

    @FXML
    private File logFile;

    @FXML
    private File sourceFolderPath;

    @FXML
    private File upcSourceFolderPath;

    @FXML
    private Label sourceLabel;

    @FXML
    private Label upcLabel;

    @FXML
    private TextArea upcTextArea;

    @FXML
    private ChoiceBox<String> choiceBox;



    public Controller(){



        this.token = "";

        this.artistsArray = null;

        this.linkArray = null;

        this.apiQuery  = new ApiQueryUtils();

        //this.sourceFolderPath = new File("/Users/equilibrium/Desktop/test");

    }

    @FXML
    public void locateFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        this.sourceFolderPath = fileChooser.showOpenDialog(new Stage());

        if(this.sourceFolderPath != null){
            this.sourceLabel.setText(this.sourceFolderPath.getAbsolutePath().toString());
        }


    }

    @FXML
    private void initialize(){

        this.choiceBox.setValue("ID");
        this.choiceBox.setItems(this.obsList);
    }

    @FXML
    public void upcLocateFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        this.upcSourceFolderPath = fileChooser.showOpenDialog(new Stage());

        if(this.upcSourceFolderPath != null){
            this.upcLabel.setText(this.upcSourceFolderPath.getAbsolutePath().toString());
        }


    }


    @FXML
    public void clearAction(){

        this.artistsTextArea.clear();


    }

    @FXML
    public void clearUpc(){
        this.upcTextArea.clear();
    }

    @FXML
    public void aboutAlertBox(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spotify Checker v1.0");
        alert.setHeaderText("Alberto Vecchi");
        alert.setContentText("Copy and paste the list of artists' ID you want to research.\nOne per line.\nThe results will be logged in your .txt file.");
        alert.showAndWait();

    }


    public void checkArtists(){

        if(this.choiceBox.getValue().equals(obsList.get(0))){
            this.checkArtistsByName();
            return;

        }

        if(this.artistsTextArea.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setContentText("You have not inserted any artist's ID!");
            alert.showAndWait();

            return;
        } else if (this.sourceLabel.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setContentText("You have not selected the destination file!");
            alert.showAndWait();
            return;
        }

        String[] arrayID = this.artistsTextArea.getText().split("\n");

        for(int i= 0; i < arrayID.length;i++){

            if(arrayID[i].length() != 22){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setContentText("You have entered an INVALID ID:\n\n" + arrayID[i]);
                alert.showAndWait();
                return;

            }
        }

        String link = "";

        Scanner scanner = null;
        String tmp = "";

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            scanner = new Scanner(artistsTextArea.getText());

            fw = new FileWriter(this.sourceFolderPath);
            bw = new BufferedWriter(fw);

            while (scanner.hasNextLine()) {
                tmp = scanner.nextLine();

                System.out.println("TMP: " + tmp);

                link = ("https://api.spotify.com/v1/artists/" + tmp);

                System.out.println("LINK: " + link);

                JsonObject jsonResponse = apiQuery.getJson(link);


                System.out.println("JSON RESPONSE: " + jsonResponse.toString());

                int popularity = jsonResponse.get("popularity").getAsInt();

                System.out.println("POP: " + popularity);

                JsonObject followersObject = jsonResponse.get("followers").getAsJsonObject();

                int followers = followersObject.get("total").getAsInt();

                String name = jsonResponse.get("name").getAsString();

                bw.write(tmp + "," + name + "," + popularity + "," + followers +  System.lineSeparator());

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (scanner != null) {
                scanner.close();
            }

            try {

                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Results:");
        alert.setHeaderText("DONE!");
        alert.showAndWait();

    }

    public void checkArtistsByName() {

        if(this.artistsTextArea.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setContentText("You have not inserted any artist's ID!");
            alert.showAndWait();

            return;
        } else if (this.sourceLabel.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setContentText("You have not selected the destination file!");
            alert.showAndWait();
            return;
        }


        String link = "";

        Scanner scanner = null;
        String tmp = "";

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            scanner = new Scanner(artistsTextArea.getText());

            fw = new FileWriter(this.sourceFolderPath);
            bw = new BufferedWriter(fw);

            while (scanner.hasNextLine()) {
                tmp = scanner.nextLine();

                System.out.println("TMP: " + tmp);

                String artistName = String.format("%s", tmp).replaceAll("\\s","%20");

                link = ("https://api.spotify.com/v1/search?q=" + artistName + "&type=artist");

                System.out.println("LINK: " + link);

                JsonObject jsonResponse = apiQuery.getJson(link);

                System.out.println("JSON RESPONSE: " + jsonResponse.toString());


                JsonObject artistsObj = jsonResponse.get("artists").getAsJsonObject();

                JsonArray artistsArray = artistsObj.get("items").getAsJsonArray();



                for (int i = 0; i < artistsArray.size(); i++) {

                    JsonObject jsonObjArr = artistsArray.get(i).getAsJsonObject();

                    String lowerCaseArtist = jsonObjArr.get("name").getAsString().toLowerCase();

                    if(tmp.toLowerCase().equals(lowerCaseArtist)){

                        int popularity = jsonObjArr.get("popularity").getAsInt();

                        JsonObject followersObject = jsonObjArr.get("followers").getAsJsonObject();

                        int followers = followersObject.get("total").getAsInt();

                        bw.write(lowerCaseArtist + ", " + popularity + ", " + followers + System.lineSeparator());

                    } else {

                        bw.write(lowerCaseArtist + ", NOT FOUND" + System.lineSeparator());
                    }


                }




            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (scanner != null) {
                scanner.close();
            }

            try {

                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Results:");
        alert.setHeaderText("DONE!");
        alert.showAndWait();




    }


    public void upcCheck(){

        if(this.upcTextArea.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setContentText("You have not inserted any artist's ID!");
            alert.showAndWait();

            return;
        } else if (this.upcLabel.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setContentText("You have not selected the destination file!");
            alert.showAndWait();
            return;
        }

        /**
         * TODO
         * Check for UPC validity!

        String[] arrayID = this.artistsTextArea.getText().split("\n");

        for(int i= 0; i < arrayID.length;i++){

            if(arrayID[i].length() != 22){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                alert.setContentText("You have entered an INVALID ID:\n\n" + arrayID[i]);
                alert.showAndWait();
                return;

            }
        }
        */


        String link = "";

        Scanner scanner = null;
        String tmp = "";

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            scanner = new Scanner(upcTextArea.getText());

            fw = new FileWriter(this.upcSourceFolderPath);
            bw = new BufferedWriter(fw);

            while (scanner.hasNextLine()) {
                tmp = scanner.nextLine();

                System.out.println("TMP: " + tmp);

                link = ("https://api.spotify.com/v1/search?q=upc:" + tmp + "&type=album");

                System.out.println("LINK: " + link);

                JsonObject jsonResponse = apiQuery.getJson(link);

                System.out.println("JSON RESPONSE: " + jsonResponse.toString());

                JsonObject jsonAlbum = jsonResponse.get("albums").getAsJsonObject();


                int total = jsonAlbum.get("total").getAsInt();


                bw.write(tmp + ", " + total + System.lineSeparator());

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (scanner != null) {
                scanner.close();
            }

            try {

                if (bw != null) {
                    bw.close();
                }

                if (fw != null) {
                    fw.close();
                }

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Results:");
        alert.setHeaderText("DONE!");
        alert.showAndWait();




    }


}
