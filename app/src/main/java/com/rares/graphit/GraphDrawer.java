package com.rares.graphit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

public class GraphDrawer extends Activity implements OnTouchListener {

	/**Request code for the startActivityForResult*/
	private static final int REQUEST_EDGE_ACTION = 1;
	/**The activity's window dimensions*/
	public static int windowHeight;
	public static int windowWidth;
	
	/**The minimum height a Node can be drawn at*/
	public static float drawStartHeight;
	
	/**The ratio at which the elements will be drawn*/
	private static float ELEMENTS_RATIO = 1/20.0f;
	
	
	/**FrameLayout which will hold the GraphPainter and the button for the menu popup*/
	FrameLayout surface;
	
	/**LinearLayout and Button for the menu popup*/
	LinearLayout buttonHolder;
	Button menuPopper;
	
	/** Surface Manipulator */
	GraphPainter gp;

	/** Graph object */
	public static Graph graph = null;

	/** Touch coordinates */
	private static float touchX = 0;
	private static float touchY = 0;

	/** Drag state */
	private boolean isDragging = false;

	/**Selection state for the breadth search*/
	private boolean isSelectingBreadth = false;
	
	/**Selection state for the depth search*/
	private boolean isSelectingDepth = false;
	
	
	/**The node which will be set as the starting node of breadth or depth search*/
	private int startNodeId = 0;
	
	/** Drag coordinates */
	private static float dragX = 0;
	private static float dragY = 0;

	/**
	 * Id of the node that is currently selected Is -1 if no node is selected
	 * during a touch event
	 */
	private static int currentId = -1;

	/**
	 * Id of a node that was selected before and an action between 2 nodes or on
	 * a node that needs to be deleted or moved
	 */
	private static int firstId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		/* Set window to full screen* */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		gp = new GraphPainter(this);
		gp.setOnTouchListener(this);
		setup();
		
		graph = new Graph();
		setContentView(surface);

	}

	/**Sets up the initial, default, values of the following elements:
	 * Window width and height
	 * Circle Radius
	 * 
	 * Sets the layout of the screen*/
	
	
	/**Class that implements onClickListener for the menuPopper*/
	public class MenuListener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			PopupMenu menu = new PopupMenu(getApplicationContext(),  menuPopper);
			menu.getMenuInflater().inflate(com.rares.graphit.R.menu.graph_drawer, menu.getMenu());
			
			
			menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent doProcess;
					switch(item.getItemId()){
					
					case com.rares.graphit.R.id.activity_draw_menu_item_ad_list:
						doProcess = new Intent("com.rares.graphit.ADLIST");
						startActivity(doProcess);
						break;
					
					case com.rares.graphit.R.id.activity_draw_menu_item_ad_matrix:
						doProcess = new Intent("com.rares.graphit.ADMATRIX");
						doProcess.putExtra("option", 1);
						startActivity(doProcess);
						break;
						
					case com.rares.graphit.R.id.activity_draw_menu_item_cost_matrix:
						doProcess = new Intent("com.rares.graphit.ADMATRIX");
						doProcess.putExtra("option", 2);
						startActivity(doProcess);
						break;
						
					case com.rares.graphit.R.id.activity_draw_menu_item_breadth:
						closeOptionsMenu();
						
						isSelectingBreadth = true;
						Handler h = new Handler();
						h.post(new Runnable() {			
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "Choose the starting node for the algorithm!", Toast.LENGTH_LONG).show();	
							}
						});
						
						
						break;
						
					case com.rares.graphit.R.id.activity_draw_menu_item_depth:
						isSelectingDepth = true;
						
						Handler h2 = new Handler();
						h2.post(new Runnable() {			
							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "Choose the starting node for the algorithm!", Toast.LENGTH_LONG).show();	
							}
						});
						
						break;
					
					}
					return true;
				}
			});
			
			menu.show();
		}
		
	}
	
	/**MenuListener to manage menu selections*/
	MenuListener menuListener = new MenuListener();
	
	public void setup(){
		
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		windowHeight = dm.heightPixels;
		windowWidth = dm.widthPixels;
		Graph.Circle.RADIUS_DEFAULT = Math.min(windowWidth, windowHeight) * ELEMENTS_RATIO;
		Graph.Edge.STROKE_WIDTH = Graph.Circle.RADIUS_DEFAULT/3;
		
		//Setup the layout of the screen
		
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, windowHeight/10);
		menuPopper = new Button(this);
		/*menuPopper.setLayoutParams(buttonParams);*/
		menuPopper.setWidth(windowWidth);
		menuPopper.setText("Choose Algorithm!");
		menuPopper.setTextSize(Math.min(windowHeight, windowWidth)/20);
		menuPopper.setTextColor(Color.BLACK);
		menuPopper.setBackgroundColor(Color.WHITE);
		menuPopper.setOnClickListener(menuListener);
		menuPopper.setLayoutParams(buttonParams);
		
		buttonHolder = new LinearLayout(this);
		buttonHolder.setLayoutParams(buttonParams);
		buttonHolder.addView(menuPopper);
		
		/*gp.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, windowHeight * 5/6));*/
		surface = new FrameLayout(this);
		surface.setBackgroundColor(Color.WHITE);
		
		surface.addView(gp);
		surface.addView(buttonHolder);
		
		
		//set the drawStartHeight
		drawStartHeight = windowHeight/6 + Graph.Circle.RADIUS_DEFAULT/2;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchX = event.getX();
			touchY = event.getY();
			currentId = graph.pointIntersectsNode(touchX, touchY);
			break;

		case MotionEvent.ACTION_MOVE:
			dragX = event.getX();
			dragY = event.getY();
			/**
			 * Function will return -1 if there is no intersection or node ID if
			 * drag start intersects the node
			 */
			/** Selected node will be red on the screen */
			if ((dragX - touchX) * (dragX - touchX) + (dragY - touchY)
					* (dragY - touchY) > Graph.Circle.RADIUS_DEFAULT) {
				if (currentId != -1 && dragY >= drawStartHeight) {
					synchronized(graph){
						graph.setNodePosition(currentId, dragX, dragY);
					}
				}

				isDragging = true;
			}
			break;

		case MotionEvent.ACTION_UP:
			synchronized (graph) {
				if(isSelectingBreadth && graph.pointIntersectsNode(touchX, touchY) != -1){
					startNodeId = currentId;
					graph.startBreadthSearch(startNodeId);
					isSelectingBreadth = false;
					isDragging = false;
					touchX = touchY = dragX = dragY = 0;
					firstId = currentId = -1;
					return true;
				}
				
				if(isSelectingDepth && graph.pointIntersectsNode(touchX, touchY) != -1){
					startNodeId = currentId;
					graph.startDepthSearch(startNodeId);
					isSelectingDepth = false;
					isDragging = false;
					touchX = touchY = dragX = dragY = 0;
					firstId = currentId = -1;
					return true;
				}
				
				if (!isDragging) {
					if (currentId == -1 && touchY >= drawStartHeight){
						graph.addNode(touchX, touchY);
						if(firstId != -1){
							graph.markNodeAsUnselected(firstId);
							firstId = -1;
						}
					}
					else {
						if (currentId != -1 && !graph.getGraphNodeList().get(currentId).isSelected()) {
							graph.markNodeAsSelected(currentId);

							if (firstId == -1)
								firstId = currentId;
							else {
								if (graph.isEdge(Math.min(firstId, currentId),
										Math.max(firstId, currentId))) {
									
									//Start an activity to chose what to do with the edge
									Intent iChoseEdgeAction = new Intent("com.rares.graphit.EDGEACTIONCHOOSE");
									iChoseEdgeAction.putExtra("id1", Math.min(firstId, currentId));
									iChoseEdgeAction.putExtra("id2", Math.max(firstId, currentId));
									
									startActivityForResult(iChoseEdgeAction, REQUEST_EDGE_ACTION);
									
								}
								else{
									graph.addEdge(Math.min(firstId, currentId),
											Math.max(firstId, currentId));
									
								}
								graph.markNodeAsUnselected(currentId);
								graph.markNodeAsUnselected(firstId);
								firstId = -1;
							}
						} else {
							/* graph.markNodeAsDeselected(id1); */
							if (firstId == currentId) {
								graph.deleteNode(currentId);
							}
							currentId = -1;
							firstId = -1;
						}
					}
				}
				/*
				 * else if(id != -1){ graph.markNodeAsDeselected(id); id = -1; }
				 */
			}
			// Reset values
			isDragging = false;
			touchX = touchY = dragX = dragY = 0;
			break;

		}

		return true;
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		gp.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		gp.pause();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(com.rares.graphit.R.menu.graph_drawer, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent doProcess;
		switch(item.getItemId()){
		case com.rares.graphit.R.id.activity_draw_menu_item_ad_list:
			doProcess = new Intent("com.rares.graphit.ADLIST");
			startActivity(doProcess);
			
			break;
		
		case com.rares.graphit.R.id.activity_draw_menu_item_ad_matrix:
			doProcess = new Intent("com.rares.graphit.ADMATRIX");
			doProcess.putExtra("option", 1);
			startActivity(doProcess);
			break;

		case com.rares.graphit.R.id.activity_draw_menu_item_cost_matrix:
			doProcess = new Intent("com.rares.graphit.ADMATRIX");
			doProcess.putExtra("option", 2);
			startActivity(doProcess);
			
			break;
			
		case com.rares.graphit.R.id.activity_draw_menu_item_breadth:
			closeOptionsMenu();
				
			isSelectingBreadth = true;
			//TODO: put a toast to suggest the selection of the start node
			Handler h = new Handler();
			h.post(new Runnable() {			
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Choose the starting node for the algorithm!", Toast.LENGTH_LONG).show();	
				}
			});
			
			
			
			break;
			
		case com.rares.graphit.R.id.activity_draw_menu_item_depth:
			closeOptionsMenu();
			
			isSelectingDepth = true;
			
			Handler hh = new Handler();
			hh.post(new Runnable() {			
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), "Choose the starting node for the algorithm!", Toast.LENGTH_LONG).show();	
				}
			});
			break;
		
		}
		
		return true;
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//the edge manipulator was called
		if(resultCode == RESULT_OK && requestCode == REQUEST_EDGE_ACTION){
			int actionType = data.getExtras().getInt("action_type");
			int cost = data.getExtras().getInt("edge_cost");
			int id1 = data.getExtras().getInt("id1");
			int id2 = data.getExtras().getInt("id2");
			
			if(actionType == 0){ //delete the edge
				graph.deleteEdge(id1, id2);
				return;
			}
			
			if(actionType == 1){ //set edge cost
				graph.setEdgeCost(id1, id2, cost);
			}
		}
		
		
	}
	
	
	
	

	
	
}
