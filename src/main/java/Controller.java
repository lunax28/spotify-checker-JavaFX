import com.google.gson.JsonObject;
import com.sun.xml.internal.bind.v2.TODO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {

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
    private Label sourceLabel;



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


    public void checkArtists(){

        String link = "";

        Scanner scanner = null;
        String tmp = "";

        BufferedWriter bw = null;
        FileWriter fw = null;

        //this.logFile = new File(this.sourceFolderPath);
        //System.out.println("LOGFILE: " + this.logFile);

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

                bw.write(tmp + "," + name + "," + popularity + "," + followers + "\n");

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

        //JOptionPane.showMessageDialog(this, "DONE!");

    }

}
