package practicaltest02.eim.systems.cs.pub.ro.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PracticalTest02MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button startButton;
    Button stopButton;
    EditText query_edit_text;
    Button queryButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

       startButton  = (Button) findViewById(R.id.start_button);
       stopButton = (Button) findViewById(R.id.stop_button);
       query_edit_text = (EditText) findViewById(R.id.query_text_view);
       queryButton = (Button)findViewById(R.id.query_button);
    }

    @Override
    public void onClick(View view) {
        if (view == startButton){
            /*
             * Start the server
             */
        }else if (view == stopButton){
            /*
             * Stop the server
             */
        } else if (view == queryButton){
            /*
             * Send the query to the server
             */
        }
    }
}
