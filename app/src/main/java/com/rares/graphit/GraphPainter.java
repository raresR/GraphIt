package com.rares.graphit;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GraphPainter extends SurfaceView implements Runnable {
	
	/**ArrayLists for drawing the elements of the graph*/
	private ArrayList<Graph.Node> listNode;
	private ArrayList<Graph.Edge> listEdge;
	
	/**Holder of surface*/
	private SurfaceHolder holder;
	
	/**Drawing thread*/
	private Thread drawThread;
	
	/**Canvas for drawing*/
	
	private Canvas canvas = null;
	
	/**Bool for running state*/
	private boolean isRunning = false;
	
	public GraphPainter(Context context){
		super(context);
		holder = getHolder();
		drawThread = new Thread(this);
		
		
		isRunning = true;
		drawThread.start();
		}
	
	@Override
	public void run(){
		
		
		while (isRunning) {
			if (holder.getSurface().isValid()) {
				canvas = holder.lockCanvas();
				/**Draw background*/
				canvas.drawRGB(255, 255, 255);

				/**Draw Graph*/
				drawGraph();
				
				
				/**Print to screen*/
				holder.unlockCanvasAndPost(canvas);
			}
		
		}
		
	}
	
	
	public void drawGraph(){
			
		/**Draw Edges*/
		Paint linePaint;
		linePaint = new Paint();
		linePaint.setColor(Graph.NODE_COLOR_UNSELECTED);
		linePaint.setStrokeWidth(Graph.Edge.STROKE_WIDTH);
		
		listEdge = GraphDrawer.graph.getGraphEdgeList();
		for(int i = 0; i < listEdge.size(); ++i){
			float startX = listNode.get(listEdge.get(i).getId1()).getDrawingCircle().x;
			float startY = listNode.get(listEdge.get(i).getId1()).getDrawingCircle().y;
			
			float stopX = listNode.get(listEdge.get(i).getId2()).getDrawingCircle().x;
			float stopY = listNode.get(listEdge.get(i).getId2()).getDrawingCircle().y;
			
			canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		}
		linePaint = null;
		
		/**Draw Nodes*/
		
		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(Graph.Circle.RADIUS_DEFAULT);
		listNode = GraphDrawer.graph.getGraphNodeList();
		for(int i = 0; i < listNode.size(); ++i){
			Graph.Circle circleToDraw = listNode.get(i).getDrawingCircle();
			canvas.drawCircle(circleToDraw.x, circleToDraw.y, circleToDraw.R, circleToDraw.circlePaint);
			canvas.drawText(Integer.toString(i), circleToDraw.x - Graph.Circle.RADIUS_DEFAULT/3,
					circleToDraw.y + Graph.Circle.RADIUS_DEFAULT/3, textPaint);
		}
		
	}
	
	
	/**Will be called from GraphDrawer when onPause() will occur*/
	public void pause(){
		isRunning = false;
		
		try {
			drawThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		drawThread = null;
	}
	
	/**Will be called from GraphDrawer when onResume() will occur*/
	public void resume(){
		drawThread = new Thread(this);
		isRunning = true;
		drawThread.start();
		
	}
	
}
