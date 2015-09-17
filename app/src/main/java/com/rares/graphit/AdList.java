package com.rares.graphit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rares.graphit.Graph.Edge;

public class AdList extends Activity{

	LinearLayout layout;
	
	ArrayList<Edge> graphEdgeList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	
		LayoutInflater inflater = getLayoutInflater();
		layout = (LinearLayout) inflater.inflate(R.layout.activity_ad_list, null);
		
		graphEdgeList = GraphDrawer.graph.getGraphEdgeList();
		Collections.sort(graphEdgeList, new Comparator<Edge>(){

			@Override
			public int compare(Edge mic, Edge mare) {
				int id1Dif = mic.getId1() - mare.getId1();
				int id2Dif = mic.getId2() - mare.getId2();
				if(id1Dif != 0)
					return id1Dif;
				return id2Dif;
			}
			
		});
		
		
		for(int i = 0; i < graphEdgeList.size(); ++i){
			Edge edge = graphEdgeList.get(i);
			TextView tv = new TextView(this);
			tv.setText(edge.getId1() + "<->" + edge.getId2());
			layout.addView(tv);
		}
		setContentView(layout);
		
	}

	
}
