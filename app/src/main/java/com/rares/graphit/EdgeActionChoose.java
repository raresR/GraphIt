package com.rares.graphit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EdgeActionChoose extends Activity{

	/**The nodes of the edge*/
	int id1, id2;
	
	/**EditText to get data from the */
	EditText costSetter;
	
	/**Buttons to set the cost of the edge or to delete the edge*/
	Button setEdgeCost;
	Button deleteEdge;
	
	/**The set cost of the edge. If not set, the default value is 100*/
	int edgeCost = 100;
	
	/**The type of action that has been chosen.
	 * -1 	: no action
	 * 0  	: delete
	 * 1	: set cost
	 * */
	int actionFlag = -1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_edge_action_choose);
		
		getRes();
		
		Intent i = getIntent();
		id1 = i.getExtras().getInt("id1");
		id2 = i.getExtras().getInt("id2");
		
		
		
		
		deleteEdge.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				actionFlag = 0;
				finish();
				
			}
		});

		
		setEdgeCost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				actionFlag = 1;
				String number;
				if(costSetter.getText().length() > 0){
					number = costSetter.getText().toString();
					edgeCost = Integer.parseInt(number);
				}
				else
					actionFlag = -1;
				
				finish();
				
				
			}
		});
		
	
	}
	
	private void getRes(){
		costSetter = (EditText) findViewById(R.id.activity_edge_action_choose_editTextSetCost);
		setEdgeCost = (Button) findViewById(R.id.activity_edge_action_choose_buttonSetCost);
		deleteEdge = (Button) findViewById(R.id.activity_edge_action_choose_buttonDelete);
	}

	@Override
	public void finish() {
		Intent returnData = new Intent();
		
		returnData.putExtra("action_type", actionFlag);
		returnData.putExtra("edge_cost", edgeCost);
		returnData.putExtra("id1", id1);
		returnData.putExtra("id2", id2);
		
		setResult(RESULT_OK, returnData);
		
		
		super.finish();
	}
	
	
	
	
}
