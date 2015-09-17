package com.rares.graphit;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AdMatrix extends Activity{

	/**Elements to be able to scroll through text*/
	LinearLayout container;
	ScrollView vContainer;
	HorizontalScrollView hContainer;
	
	/**Table which will be the layout of the activity*/
	TableLayout table;
	/**Table configuration*/
	android.widget.TableLayout.LayoutParams tableParams;
	
	/**Table row configuration*/
	android.widget.TableRow.LayoutParams rowParams;
	
	/**Received data from the drawGraph activity*/
	ArrayList<Graph.Node> recNodeList;
	ArrayList<Graph.Edge> recEdgeList;
	
	/**Constants for the type of matrix to be drawn*/
	private static final int ADJACENCY = 1;
	private static final int COST = 2;
	
	/**Flag that will show what to be written: cost or adjacency*/
	int option = 0;
	
	/**Matrix from which the table will be constructed*/
	int[][] adMat = null;
	
	/**Matrix size. Will be n*n, where n - number of nodes*/
	int n;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent called = getIntent();
		option = called.getExtras().getInt("option");
		
		getRes();
		makeTable();
		
		
		hContainer.addView(table);
		/*vContainer.addView(hContainer);*/
		container.addView(vContainer);
		setContentView(hContainer);
	}

	private void makeTable() {
		makeMatrix();
		
		
		
		table = new TableLayout(this);
		tableParams = new android.widget.TableLayout.LayoutParams();
		tableParams.width = LayoutParams.MATCH_PARENT;
		rowParams = new android.widget.TableRow.LayoutParams();
		
		
		tableParams.setMargins(5, 5, 5, 5);
		tableParams.gravity = Gravity.CENTER;
		table.setLayoutParams(tableParams);
		table.setStretchAllColumns(true);
		
		
		//Table's first row: the nodes
		TableRow firstRow = new TableRow(this);
		/*firstRow.setWeightSum(recNodeList.size()+1);*/
		rowParams.setMargins(5, 5, 5, 5);
		rowParams.gravity = Gravity.CENTER;
		rowParams.width = LayoutParams.MATCH_PARENT;
		/*rowParams.weight = 1.0f;*/
		firstRow.setLayoutParams(rowParams);
		
		//The upside-left corner of the Matrix
		TextView aux = new TextView(this);
		StringBuilder cornerText = new StringBuilder(0);
		
		/*int s = recNodeList.size() * 10;
		for(; s > 0; s/=10)*/
			cornerText.append('X');
		
		aux.setText(cornerText.toString());
		aux.setLayoutParams(rowParams);
		/*aux.setWidth(table.getWidth()/recNodeList.size());*/
		
		firstRow.addView(aux);
		
		for(int i = 0; i < recNodeList.size(); i++){
			TextView td = new TextView(this);
			td.setText(Integer.toString(i));
			td.setLayoutParams(rowParams);
			/*td.setWidth(firstRow.getWidth()/recNodeList.size());*/
			firstRow.addView(td);
		}
		table.addView(firstRow);
		
		
		for(int i = 0; i < recNodeList.size(); i++){
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(rowParams);
			
			//First column of every row: the nodes
			TextView firstCol = new TextView(this);
			firstCol.setLayoutParams(rowParams);
			firstCol.setText(Integer.toString(i));
			tr.addView(firstCol);
			
			
			for(int j =0; j < recNodeList.size(); j++){
				TextView td = new TextView(this);
				td.setLayoutParams(rowParams);
				td.setId(i * recNodeList.size() + j);
				td.setText(Integer.toString(adMat[i][j]));
				/*td.setWidth(table.getWidth()/recNodeList.size());*/
				tr.addView(td);
			}
			
			table.addView(tr);
		}
		
	}

	/**Construct the matrix from the passed data*/
	private void makeMatrix() {
		adMat = new int[recNodeList.size()][recNodeList.size()];
		
		switch(option){

		case ADJACENCY:
			for(int i = 0; i < recEdgeList.size(); i++){
				Graph.Edge edge = recEdgeList.get(i);
				adMat[edge.getId1()][edge.getId2()] = 1;
				adMat[edge.getId2()][edge.getId1()] = 1;
			}

			break;
		
		case COST:
			for(int i = 0; i < recEdgeList.size(); i++){
				Graph.Edge edge = recEdgeList.get(i);
				adMat[edge.getId1()][edge.getId2()] = edge.getCost();
				adMat[edge.getId2()][edge.getId1()] = edge.getCost();
			}
			break;
		}
		
		
	}

	private void getRes() {
		
		//get linearlayout from res
		LayoutInflater inflater = getLayoutInflater();
		container = (LinearLayout) inflater.inflate(R.layout.activity_matrix, null);
		
		//instantiate the scrollviews
		vContainer = new ScrollView(this);
		vContainer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(GraphDrawer.windowWidth, GraphDrawer.windowHeight/4));
		hContainer = new HorizontalScrollView(this);
		hContainer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(GraphDrawer.windowWidth, GraphDrawer.windowHeight/4));
		
		recNodeList = GraphDrawer.graph.getGraphNodeList();
		recEdgeList = GraphDrawer.graph.getGraphEdgeList();
		
		
	}

	
}
