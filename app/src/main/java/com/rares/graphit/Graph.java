package com.rares.graphit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import android.graphics.Color;
import android.graphics.Paint;




/**Graph structure and manipulator. Momentarily, it will be an unoriented graph*/

public class Graph {
	
	/**Circle for drawing purposes*/
	public static class Circle{
		/**Circle Radius setting*/
		public static float RADIUS_DEFAULT = 30;
		
		/**Circle coordinates*/
		float x, y;
		float R;
		
		/**Circle style*/
		Paint circlePaint = null;
		/*Circle default color*/
		public int DEFAULT_COLOR = Color.BLUE;
		public int drawColor;
		
		
		/**Creates a circle with the Point as center, r as radius, and the default color
		 * of a node*/
		public Circle(float x, float y, float r){
			this.x = x;
			this.y = y;
			this.R = r;
			
			this.circlePaint = new Paint();
			this.circlePaint.setColor(DEFAULT_COLOR);
			drawColor = DEFAULT_COLOR;
		}
		
		
	}
	
	/**Graph node structure*/
	public static class Node{
		/**For drawing purposes*/
		private Circle drawingCircle;
		
		/**Node ID in the graph*/
		private int ID;
		
		/**Flag that shows whether the node is selected*/
		private boolean selected = false;
		
		/**Flag that shows whether the nod has been processed
		 * It is currently used for breadth search*/
		private boolean processed = false;
		
		public Node(float x, float y){
			/*this.ID = numberOfNodes;*/
			this.drawingCircle = new Circle(x, y, Circle.RADIUS_DEFAULT);
			
			
		}
		
		/**Getters and setters for members*/
		
		public Circle getDrawingCircle() {
			return drawingCircle;
		}

		public void setDrawingCircle(Circle drawingCircle) {
			this.drawingCircle = drawingCircle;
		}

		public int getID() {
			return ID;
		}

		public void setID(int iD) {
			this.ID = iD;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean isSelected) {
			this.selected = isSelected;
		}

		public boolean isProcessed(){
			return processed;
		}
		
		public void setProcessed(boolean p){
			this.processed = p;
		}
		
		public void setColor(int color){
			this.drawingCircle.circlePaint.setColor(color);
		}
		
		
	}
	
	/**Graph edge structure*/
	public static class Edge{
		/**Nodes between whom the edge will be drawn
		 * id1 - the lower number
		 * id2 - the higher number
		 * */
		private int id1, id2;
		private int cost = 100;
		
		/**Width of the line that will be drawn by the GraphPainter*/
		public static float STROKE_WIDTH = 20;
		
		
		/**Getters and setters for the IDs and Cost*/
		public int getId1() {
			return id1;
		}

		public void setId1(int id1) {
			this.id1 = id1;
		}

		public int getId2() {
			return id2;
		}

		public void setId2(int id2) {
			this.id2 = id2;
		}

		public int getCost(){
			return this.cost;
		}
		
		public void setCost(int cost2){
			this.cost = cost2;
		}
		
		/**Constructor that will set the nodes of the edge*/
		public Edge(int a, int b){
			if(a > b){
				this.id1 = b; this.id2 = a;
			}
			else{
				this.id1 = a; this.id2 = b;
			}
		}
		
		/**Copy Constructor*/
		public Edge(Edge e){
			this.id1 = e.id1;
			this.id2 = e.id2;
		}
	}
	
	/**A sensitivity corrector to detect intersection better*/
	private static float sensitivityCorrector;
	
	/**Default time for pause between each step of process*/
	public static long stepTime = 1000;
	
	/**Node default colors*/
	public static int NODE_COLOR_UNSELECTED = Color.BLUE;
	public static int NODE_COLOR_SELECTED = Color.RED;
	public static int NODE_COLOR_PROCESSED = Color.GREEN;
	
	/**Maximum number of nodes accepted
	 * Is currently set to 50*/
	public final static int MAX_NODES = 50;
	
	/**List of vicinity of the graph*/
	private static ArrayList<Node> graphNodeList;
	
	/**Current number of nodes*/
	private static int numberOfNodes = 0;
	
	/**List of the edges of the graph*/
	private static ArrayList<Edge> graphEdgeList;
	
	
	/**The constructor is only for initialization purposes.
	 * All elements will be added through addNode() or addEdge() methods*/
	public Graph(){
		numberOfNodes = 0;
		sensitivityCorrector = Math.min(Circle.RADIUS_DEFAULT, 20);
		graphNodeList = new ArrayList<Graph.Node>(0);
		graphEdgeList = new ArrayList<Graph.Edge>(0);
	}
	
	
	/**List of vicinity getter*/
	public ArrayList<Node> getGraphNodeList(){
		return graphNodeList;
	}
		
	/**List of Edges getter*/
	public ArrayList<Edge> getGraphEdgeList(){
		return graphEdgeList;
	}
	
	/**Graph size getter*/
	public int getNumberOfNodes(){
		return numberOfNodes;
	}
	
	
	/**Adds a node into the graph, with the canvas coordinates: x and y*/
	public void addNode(float x, float y){
		numberOfNodes++;
		Node newNode = new Node(x, y);
		newNode.ID = graphNodeList.size();
		graphNodeList.add(newNode);
		newNode = null;
	}
	
	
	/**Deletes a node from the graph */
	public void deleteNode(int id){
		graphNodeList.remove(id);
		deleteEdges(id);
		for(int i = id; i < graphNodeList.size(); ++i){
			int aux = graphNodeList.get(i).ID;
			
			if(aux != i)
				resetEdgeNodes(aux, i);
			
			graphNodeList.get(i).ID = i;
		}
		
		numberOfNodes--;
		
	}
	
	/**Verifies if a point described by its coordinates intersects one if the nodes on 
	 * the screen.
	 * Returns the ID of the node if true
	 * -1 otherwise*/
	public int pointIntersectsNode(float X, float Y){
		for(int i = 0; i < graphNodeList.size(); i++){
			float centerX = graphNodeList.get(i).drawingCircle.x;
			float centerY = graphNodeList.get(i).drawingCircle.y;
			float circleRadius = graphNodeList.get(i).drawingCircle.R + sensitivityCorrector;
			if((centerX-X) * (centerX-X) + (centerY-Y)*(centerY-Y) < circleRadius*circleRadius)
				return i;
		}
	
		return -1;
	}

	
	/**Marks node as selected and will paint it the COLOR_SELECTED color*/
	public void markNodeAsSelected(int id){
		graphNodeList.get(id).setColor(NODE_COLOR_SELECTED);
		graphNodeList.get(id).setSelected(true);
	}
	

	/**Marks the node back as unselected, with its default color*/
	public void markNodeAsUnselected(int id) {
		graphNodeList.get(id).drawingCircle.circlePaint.setColor(NODE_COLOR_UNSELECTED);
		graphNodeList.get(id).setSelected(false);
		graphNodeList.get(id).setProcessed(false);
	}

	
	/**Marks the node as processed by coloring it the COLOR_PROCESSED color*/
	public void markNodeAsProcessed(int id){
		graphNodeList.get(id).drawingCircle.circlePaint.setColor(NODE_COLOR_PROCESSED);
		graphNodeList.get(id).setProcessed(true);
	}

	/**Sets the position of the node with the id ID at the (X,Y) point*/
	public void setNodePosition(int id, float X, float Y) {
		graphNodeList.get(id).drawingCircle.x = X;
		graphNodeList.get(id).drawingCircle.y = Y;
		
		
	}
	
	
	/**Adds an edge between two nodes*/
	public void addEdge(int id1, int id2){
		Edge edge = new Edge(id1, id2);
		graphEdgeList.add(edge);
	}
	
	
	/**Verifies if an edge between nodes id1 and id2 exists*/
	public boolean isEdge(int id1, int id2){
		for(int i = 0; i < graphEdgeList.size(); ++i){
			if(id1 == graphEdgeList.get(i).id1 && id2 == graphEdgeList.get(i).id2)
				return true;
		}
		return false;
	}

	
	/**Deletes an edge from the list, by the nodes IDs
	 * id1 is the node with the minimum id
	 * id2 is the node with the maximum id
	 * */
	public void deleteEdge(int id1, int id2){
		for(int i = 0; i < graphEdgeList.size(); ++i)
			if(id1 == graphEdgeList.get(i).id1 && id2 == graphEdgeList.get(i).id2){
				graphEdgeList.remove(i);
				return;
			}
		
	}

	/**Set the cost of the edge between id1 and id2 nodes (id1 < id2)
	 * c is the cost value*/
	public void setEdgeCost(int id1, int id2, int cost){
		for(int i = 0; i < graphEdgeList.size(); ++i)
			if(id1 == graphEdgeList.get(i).id1 && id2 == graphEdgeList.get(i).id2){
				graphEdgeList.get(i).setCost(cost);
			}
			
	}
	
	/**Deletes the edges which has one of the nodes with its ID set to id*/
	public void deleteEdges(int id){
		for(int i = 0; i < graphEdgeList.size(); ++i){
			if(id == graphEdgeList.get(i).id1 || id == graphEdgeList.get(i).id2){
				graphEdgeList.remove(i);
				i--;
			}
		}
	}


	/**Resets the List of Edges when a node is deleted*/
	public void resetEdgeNodes(int oldId, int newId){
		for(int i = 0; i < graphEdgeList.size(); ++i){
			int nodeId1 = graphEdgeList.get(i).id1;
			int nodeId2 = graphEdgeList.get(i).id2;
			if(nodeId1 == oldId){
				graphEdgeList.get(i).id1 = newId;
			}
			else
				if(nodeId2 == oldId){
					graphEdgeList.get(i).id2 = newId;
				}
			
			/*If the new nodes are not in order, swap them**/
			if(graphEdgeList.get(i).id1 > graphEdgeList.get(i).id2){
				int aux = graphEdgeList.get(i).id1;
				graphEdgeList.get(i).id1 = graphEdgeList.get(i).id2;
				graphEdgeList.get(i).id2 = aux;
			}
		}
		
	}


	/**Adjacency Matrix for searches*/
	private int[][] mat;
	
	/**Breadth search function. Will be called from MenuInflater.
	 * x is the start node of the search.
	 * */
	public void startBreadthSearch(int x){
		
		//Reset all the nodes to the unselected state
				for(int i = 0; i < numberOfNodes; ++i)
					markNodeAsUnselected(i);
		
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
		
		//Creates an adjacency matrix which will be used to do the search
		mat = new int[numberOfNodes][numberOfNodes];
		
		//Fill the matrix with values of 0 or 1 : 1 - there is an edge between i and j nodes
		for(int i = 0; i < numberOfNodes; ++i)
			for(int j = 0; j < numberOfNodes; ++j)
				if(isEdge(Math.min(i, j), Math.max(i, j)))
					mat[i][j] = 1;
				else
					mat[i][j] = 0;
		
		
		LinkedList<Integer> queue = new LinkedList<Integer>();
		
		//Process the starting node, x
		markNodeAsProcessed(x);
		try{
			Thread.sleep(stepTime);
		}catch(Exception e){
			
		}
		
		for(int j = 0; j < numberOfNodes; ++j)
			if(mat[x][j] == 1){
				queue.add(j);
				markNodeAsProcessed(j);
				
				try{
					Thread.sleep(stepTime);
				}catch(Exception e){
					
				}
			}
		//If the starting node is an isolated one, we must add to the queue the first node that
		//has not been processed
		//Modified: searches must nod go through multiple connected components
		/*if(queue.isEmpty())
			for(int i = 0; i < numberOfNodes; ++i)
				if(!graphNodeList.get(i).isProcessed()){
					queue.add(i);
					break;
				}*/
			
		//Process the other nodes
		while(!queue.isEmpty()){
			
			//Processing of a connected component
			while(!queue.isEmpty()){
				x = queue.poll();
				
				for(int j = 0; j < numberOfNodes; ++j)
					if(mat[x][j] == 1 && !graphNodeList.get(j).isProcessed()){
						//The node is connected to the currently processed list and has
						//not been processed
						queue.add(j);
						markNodeAsProcessed(j);
						
						try{
							Thread.sleep(stepTime);
						}catch(Exception e){
							
						}
					}
			}
			
			
			//When a connected component is processed, we verify if there are any nodes,
			//hence components, left - nullified
			/*for(int i = 0; i < numberOfNodes; ++i)
				if(!graphNodeList.get(i).isProcessed()){
					queue.add(i);
					break;
				}*/
		} //end of the first while
	
	
		//Reset all the nodes to the unselected state
		for(int i = 0; i < numberOfNodes; ++i)
			markNodeAsUnselected(i);
	}
	
	/**Depth search function. Will be called from startDepthSearch*/
	private void depthSearch(int x){
		markNodeAsProcessed(x);
		
		try{
			Thread.sleep(stepTime);
		}catch(Exception e){
			
		}
		
		for(int j = 0; j < numberOfNodes; ++j)
			if(mat[x][j] == 1 && !graphNodeList.get(j).isProcessed())
				depthSearch(j);
	}
	
	/**Start the Depth Search. Will be called from MenuInflater*/
	public void startDepthSearch(int x){
		
		//Set up the matrix
		mat = new int[numberOfNodes][numberOfNodes];
		for(int i = 0; i < numberOfNodes; ++i)
			for(int j = 0; j < numberOfNodes; ++j)
				if(isEdge(Math.min(i, j), Math.max(i, j)))
					mat[i][j] = 1;
				else
					mat[i][j] = 0;
		
		//Reset nodes' status
		for(int i = 0; i < numberOfNodes; ++i)
			markNodeAsUnselected(i);
		
		depthSearch(x);
		
		//Flag to indicate if all the components have been processed
		/*boolean finished = false;
		*/
		
		//Has been removed so that only one connected component would be processed
		/*while(!finished){
			finished = true;
			for(int i = 0; i < numberOfNodes; ++i)
				if(!graphNodeList.get(i).isProcessed()){
					depthSearch(i);
					finished = false;
					break;
				}
			
		}*/
		
		//Reset the nodes
		for(int i = 0; i < numberOfNodes; ++i)
			markNodeAsUnselected(i);
		
	}
	
	
	/**Will calculate the minimum distance from one node to all others*/
	public void dijkstra(int x){
		
		
	}
	
	
}
