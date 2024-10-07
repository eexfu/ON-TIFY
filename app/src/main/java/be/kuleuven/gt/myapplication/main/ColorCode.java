package be.kuleuven.gt.myapplication.main;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import be.kuleuven.gt.myapplication.R;

public class ColorCode extends AppCompatActivity {

    //instantiations

    private Button missed;
    private Button tomorrow;
    private Button days7;
    private Button weeks;
    private Button nodeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_code);

        //initialize different colors

        missed = findViewById(R.id.missed_deadline);
        int color_missed = Color.parseColor("#BE5750");
        missed.setBackgroundTintList(ColorStateList.valueOf(color_missed));

        tomorrow = findViewById(R.id.tomorrow);
        int color_tomorrow = Color.parseColor("#E57C0E");
        tomorrow.setBackgroundTintList(ColorStateList.valueOf(color_tomorrow));

        days7 = findViewById(R.id.days7);
        int color_days7 = Color.parseColor("#D6CD41");
        days7.setBackgroundTintList(ColorStateList.valueOf(color_days7));

        weeks = findViewById(R.id.weeks);
        int color_weeks = Color.parseColor("#6EA96E");
        weeks.setBackgroundTintList(ColorStateList.valueOf(color_weeks));

        nodeadline = findViewById(R.id.nodeadline);
        int color_nodeadline = Color.parseColor("#ACB4AC");
        nodeadline.setBackgroundTintList(ColorStateList.valueOf(color_nodeadline));
    }
    public void onClick_btn_exit(View Caller){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}