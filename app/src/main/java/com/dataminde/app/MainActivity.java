package com.dataminde.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private DiagramView diagramView;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(16, 17, 22));

        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setPadding(dp(16), dp(10), dp(12), dp(10));
        toolbar.setBackgroundColor(Color.rgb(25, 27, 35));

        TextView title = new TextView(this);
        title.setText("Dataminde");
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setTypeface(null, 1);
        toolbar.addView(title, new LinearLayout.LayoutParams(0, dp(44), 1));

        Button undoButton = makeButton("↶");
        undoButton.setOnClickListener(v ->
                Toast.makeText(this, "История действий будет добавлена следующим этапом", Toast.LENGTH_SHORT).show());
        toolbar.addView(undoButton);

        Button resetButton = makeButton("⌂");
        resetButton.setOnClickListener(v -> {
            if (diagramView != null) diagramView.resetCamera();
        });
        toolbar.addView(resetButton);

        root.addView(toolbar);

        diagramView = new DiagramView(this);
        root.addView(diagramView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER_VERTICAL);
        bottom.setPadding(dp(12), dp(8), dp(12), dp(8));
        bottom.setBackgroundColor(Color.rgb(25, 27, 35));

        statusText = new TextView(this);
        statusText.setText("Перетаскивай блоки · двигай холст · масштабируй двумя пальцами");
        statusText.setTextColor(Color.rgb(165, 171, 188));
        statusText.setTextSize(11);
        bottom.addView(statusText, new LinearLayout.LayoutParams(0, dp(48), 1));

        Button addButton = makeButton("+ Добавить");
        addButton.setTextColor(Color.WHITE);
        addButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(55, 105, 240)));
        addButton.setOnClickListener(v -> {
            diagramView.addNode();
            statusText.setText("Добавлен новый блок");
        });
        bottom.addView(addButton);

        root.addView(bottom);
        setContentView(root);

        String engine = NativeBridge.engineInfo();
        Toast.makeText(this, engine, Toast.LENGTH_SHORT).show();
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(13);
        button.setMinWidth(dp(48));
        button.setMinHeight(dp(44));
        return button;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
                          }
          
