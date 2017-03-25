package com.companionfree.numberanalyzer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.simplify.ink.InkView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ink)
    InkView inkView;

    @BindView(R.id.prediction)
    TextView predictionTextView;

    private static final String TAG = "Main Activity";

    private Analyzer analyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        inkView.setColor(ContextCompat.getColor(this, android.R.color.black));
        inkView.setMinStrokeWidth(20.0f);
        inkView.setMaxStrokeWidth(20.0f);

        analyzer = new Analyzer(this);

    }

    @OnClick(R.id.btn_clear)
    public void onClearClick() {
        inkView.clear();
        predictionTextView.setText("");
    }

    @OnClick(R.id.btn_calc)
    public void onCalcClick() {
        Log.d(TAG, "Checking");
        Bitmap[][] bitmaps = BitmapHelper.splitBitmap(inkView.getBitmap(), 28, 28);

        analyzer.analyze(bitmaps, new Analyzer.AnalyzerListener() {
            @Override
            public void onAnalysisComplete(int prediction) {
                predictionTextView.setText(String.valueOf(prediction));
            }
        });
    }
}
