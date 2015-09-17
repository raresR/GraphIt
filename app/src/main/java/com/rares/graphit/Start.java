package com.rares.graphit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;



public class Start extends Activity {

	/**Background Image*/
	ImageView backgroundImage = null;
	
	/**Button for Graph Draw activity*/
	Button drawGraph;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        
        
        getRes();
        drawGraph.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent;
				intent = new Intent("com.rares.GRAPHDRAWER");
				startActivity(intent);
			}
		});
        
    }

    
    private void getRes(){
    	backgroundImage = (ImageView)findViewById(R.id.activity_start_image);
    	drawGraph = (Button)findViewById(R.id.activity_start_button_draw);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }
    
}
