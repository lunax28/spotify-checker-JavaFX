import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ApiQueryUtils  {

    private String link;
    private JsonObject jsonObject;
    private String responseTrimmed;
    private JSONArray jArray;
    private String albumsJson;
    private String tokenString;
    private int responseCode;

    public ApiQueryUtils() {
        this.link = "";
        this.responseTrimmed = "";
        this.jsonObject = null;
        this.tokenString = "";
        this.responseCode = 0;
    }

    public String getToken() {
        String json_response = "";

        try {
            URL url = new URL("https://accounts.spotify.com/api/token");
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            String basicAuth = "Basic NTM0NzYyN2JkYzQ0NGEwYzg3ZWI4NGFkZTkwMTc0YzI6NDQ5NjNjMDViY2FjNDlmNGIwZDU1YWE4ZWY5YWY0NDk=";
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Authorization", basicAuth);

            OutputStreamWriter out = new OutputStreamWriter(
                    httpCon.getOutputStream());
            out.write("grant_type=client_credentials");
            out.flush();
            out.close();

            InputStreamReader in = new InputStreamReader(httpCon.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String text = "";
            while ((text = br.readLine()) != null) {
                json_response += text;
            }

        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException!!");
        } catch (ProtocolException ex) {
            System.out.println("ProtocolException!!");
        } catch (IOException ex) {
            System.out.println("IOException!!");
        }

        JsonObject token = new JsonParser().parse(json_response).getAsJsonObject();
        return token.get("access_token").getAsString();

    }

    public String getLink() {
        return this.link;
    }

    public JsonObject getJson(String link) {
        String response = "";
        try {

            URL url = new URL(link);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

            if (this.tokenString.isEmpty()) {
                this.tokenString = this.getToken();
                System.out.println("###\nREQUESTED NEW TOKEN!!!\n###");
            }

            String basicAuth = "Bearer " + this.tokenString; //"BQBERpBRRUeuZ4tjxtRBq__FrTpEaecFUmTCd9gwgvwmcGqie5SVMum-RQRATj5FMlsyeg5WuWj6W7qkUnjFwQ"; //getToken();//"BQD5PgY20-9WFB0xWoKAKF8Lip7z_it6HG__w0lxzdRaS8NGhtx-AfGhumYKK3sO5Zn3tEBjcBqWxxFRlum7bA"; //+ token;

            httpCon.setRequestMethod("GET");
            httpCon.setRequestProperty("Authorization", basicAuth);

            this.responseCode = httpCon.getResponseCode();
            if (this.responseCode != 200) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning!");
                //alert.setHeaderText("Alberto Vecchi");
                alert.setContentText("An error has occured. Rate limit probably reached");

                alert.showAndWait();

                return null;

            }
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);


            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpCon.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response += inputLine;
            }
            in.close();
        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException!!");
        } catch (ProtocolException ex) {
            System.out.println("ProtocolException!!");
        } catch (IOException ex) {
            System.out.println("IOException!!");
        }

        this.responseTrimmed = response.trim();

        this.jsonObject = new JsonParser().parse(responseTrimmed).getAsJsonObject();
        System.out.println("jsonobj: " + this.jsonObject.toString());

        return this.jsonObject;

    }




    public String isAlbum(String link) {
        this.link = link;

        JsonObject jsonIsAlbum = getJson(link);

        if (jsonIsAlbum == null) {
            return "RATE LIMIT";

        }

        JsonObject jsonId = new JsonParser().parse(this.responseTrimmed).getAsJsonObject();
        this.albumsJson = jsonId.get("albums").toString();
        jsonId = new JsonParser().parse(this.albumsJson).getAsJsonObject();

        String total = jsonId.get("total").toString();
        System.out.println("TOTAL: " + total);
        this.responseTrimmed = "";
        if(total.equals("1")){
            return "1";

        } else {
            return "0";
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

}
