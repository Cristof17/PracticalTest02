package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

public class PracticalTest02MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button startButton;
    private Button stopButton;
    private EditText query_edit_text;
    private Button queryButton;
    private static final int PORT = 40000;
    private static boolean isRunning;
    private ServerSocket serverSocket;

    private String address = "http://autocomplete.wunderground.com/aq=?query=";
    private String query_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

       startButton  = (Button) findViewById(R.id.start_button);
       stopButton = (Button) findViewById(R.id.stop_button);
       query_edit_text = (EditText) findViewById(R.id.query_text_view);
        queryButton = (Button) findViewById(R.id.query_button);

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        queryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        ServerThread server = null;
        if (view == startButton){
            /*
             * Start the server
             */
            server = new ServerThread();
            server.startServer();
        }else if (view == stopButton){
            /*
             * Stop the server
             */
            if (server != null)
                server.stopServer();
        } else if (view == queryButton){
            /*
             * Send the query to the server
             */
            //connect to server

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                        Socket client = new Socket("127.0.0.1", PORT);
                        /*
                         * get the text from textview
                         */
                            String text = query_edit_text.getText().toString();
                        /*
                         * get the output stream to send the query string to the server
                         */
                            PrintStream printStream = new PrintStream(client.getOutputStream());
                        /*
                         * write the query string to the outputstream
                         */
                            printStream.print(text);
                        /*
                         * TODO Wait for the response from the server
                         */

                        }catch(Exception e){
                            e.printStackTrace();
                            String message = e.getMessage();
                            Log.d("THREAD", "String = " + e.getMessage());
                        }
                    }
                };
                Thread clientThread = new Thread(runnable);
                clientThread.start();


        }
    }

    private class ServerThread extends Thread {

        public void startServer() {
            isRunning = true;
            start();
        }

        public void stopServer() {
            isRunning = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (serverSocket != null) {
                            serverSocket.close();
                        }
                        Log.v("THREAD", "stopServer() method invoked "+serverSocket);
                    } catch(IOException ioException) {
                        Log.e("THREAD", "An exception has occurred: "+ioException.getMessage());
                    }
                }
            }).start();
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);
                StringBuilder response = null;
                while (isRunning) {
                    Socket socket = serverSocket.accept();

                    /*
                     * Get the query
                     */
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    String query = in.readLine();
                    String query  = query_edit_text.getText().toString();
                    /* TODO
                     * Make the HTTP Request
                     */
                    HttpURLConnection httpURLConnection = null;
                    StringBuilder result = new StringBuilder();
                    String error = null;
                    try {
                        String webPageAddress = address;
                        webPageAddress += "mama";
                        if (webPageAddress == null || webPageAddress.isEmpty()) {
                            error = "Web Page address cannot be empty";
                        }

                        URL url = new URL(webPageAddress);
                        result.append("Protocol: " + url.getProtocol() + "\n");
                        result.append("Host: " + url.getHost() + "\n");
                        result.append("Port: " + url.getPort() + "\n");
                        result.append("File: " + url.getFile() + "\n");
                        result.append("Reference: " + url.getRef() + "\n");
                        result.append("==========\n");
                        URLConnection urlConnection = url.openConnection();
                        if (urlConnection instanceof HttpURLConnection) {
                            httpURLConnection = (HttpURLConnection) urlConnection;
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            int currentLineNumber = 0, numberOfOccurrencies = 0;
                            String currentLineContent;
                            response = new StringBuilder();
                            while ((currentLineContent = bufferedReader.readLine()) != null) {
                                currentLineNumber++;
                                response.append(currentLineContent);
                            }
                            result.append("Number of occurrencies: " + numberOfOccurrencies + "\n");
                        }
                    } catch (MalformedURLException malformedURLException) {
                        malformedURLException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    } finally {
                        if (httpURLConnection != null) {
                            httpURLConnection.disconnect();
                        }
                    }

                    /*
                     * Parse the string response
                     */
                    try {
                        JSONObject object = new JSONObject(response.toString());
                        JSONArray array = object.getJSONArray("RESULTS");
                        for (int i = 0; i < array.length(); ++i){
                            JSONObject curr = array.getJSONObject(i);
                            String name = curr.getString("name");
                        }
                    }catch (Exception e){
                        e.printStackTrace();;
                    }
                    /*
                     * TODO
                     * Print the response from the server
                     */
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                    printWriter.println(query_edit_text.getText().toString());
                    socket.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
