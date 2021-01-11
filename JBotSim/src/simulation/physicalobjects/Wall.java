package simulation.physicalobjects;

import gui.renderer.TwoDRendererDebug;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Shape;
import java.io.Serializable;

import net.jafama.FastMath;
import mathutils.MathUtils;
import mathutils.VectorLine;
import simulation.Simulator;
import simulation.physicalobjects.collisionhandling.knotsandbolts.PolygonShape;
import simulation.physicalobjects.collisionhandling.knotsandbolts.RectangularShape;

public class Wall extends PhysicalObject{

	//Can only be in a 0 x and z orientation!!
	
	private double width, lenght, height;

	private Edge leftAbove, rightAbove, topAbove, bottomAbove;
	private Edge leftBellow, rightBellow, topBellow, bottomBellow;
	private Edge[] edges;
	private boolean is3D = false;
	private double topLimit,bottomLimit;
	public Color color = Color.BLACK;
	
	public Wall(Simulator simulator, String name, double x, double y,
			double orientation, double mass, 
			double range, double relativeRotation, double width, double height,
			PhysicalObjectType type) {
		super(simulator, name, x, y,0, 0, 0, orientation, mass, type);
		this.width = width;
		this.lenght = height;
		topLimit = 0.1f; bottomLimit = 0.1f;
		//this.x = x;
		//this.y = y;
		this.setPosition(new VectorLine(x,y));
		initialize2DEdges();
		edges = getEdges();
		
		defineShape(simulator);
		
		if(type == PhysicalObjectType.WALLBUTTON)
			color = Color.RED;
	}
	
	public Wall(Simulator simulator, String name, double x, double y, double z,
			double orientationX,double orientationY, double orientationZ, double mass, 
			double range, double relativeRotation, double width, double lenght,double height,
			PhysicalObjectType type) {
		super(simulator, name, x, y,z, orientationX, orientationY, orientationZ, mass, type);
		this.width = width;
		this.lenght = lenght;
		this.height = height;
		topLimit = 0.1f; bottomLimit = 0.1f;
		//this.x = x;
		//this.y = y;
		this.setPosition(new VectorLine(x,y));
		initialize2DEdges();
		edges = getEdges();
		
		defineShape(simulator);
		
		if(type == PhysicalObjectType.WALLBUTTON)
			color = Color.RED;
	}
	
	public Wall(Simulator simulator, double x, double y, double width, double height) {
		this(simulator, "wall"+simulator.getRandom().nextInt(1000), x, y, 0, 0, 0, 0, width, height, PhysicalObjectType.WALL);
		topLimit = 0.1; bottomLimit = 0.1;
		defineShape(simulator);
	}
	
	public Wall(Simulator simulator, VectorLine p1, double width, double height) {
		this(simulator, "wall"+simulator.getRandom().nextInt(1000), p1.x, p1.y, 0, 0, 0, 0, width, height, PhysicalObjectType.WALL);
		topLimit = 0.1; bottomLimit = 0.1;
		defineShape(simulator);
	}
	
	public Wall(Simulator simulator, VectorLine p1, VectorLine p2, double wallSize) {
		super(simulator, "wall"+simulator.getRandom().nextInt(1000), (p1.x+p2.x)/2, (p1.y+p2.y)/2, 0, 0, PhysicalObjectType.WALL);
		this.width = p1.distanceTo(p2);
		this.lenght = wallSize;
		initializeEdges(p1,p2);
		edges = getEdges();
		topLimit = 0.1; bottomLimit = 0.1;
		defineShape(simulator);
	}

	//3D Creation Only!!! DONE!
	public Wall(Simulator simulator, VectorLine p1, double width, double height, double depth,double orientationZ) {
		this(simulator, "wall"+simulator.getRandom().nextInt(1000), p1.x, p1.y, p1.z,0,0,orientationZ,0, 0, 0, width, height, depth, PhysicalObjectType.WALL);
		topLimit = 0.1; bottomLimit = 0.1;
		defineShape(simulator);
	}	
	
	//3D Creation Only!!! DONE!
	public Wall(Simulator simulator, VectorLine p1, VectorLine p2, double zValue, double wallLenght, double WallHeight) {
		super(simulator, "wall"+simulator.getRandom().nextInt(1000), (p1.x+p2.x)/2, (p1.y+p2.y)/2, zValue, 0,0,0,0, PhysicalObjectType.WALL);
		is3D = true;
		this.width = p1.distanceTo(p2);
		this.lenght = wallLenght;
		this.height = WallHeight;
		topLimit = this.getPosition().z + WallHeight/2; bottomLimit = this.getPosition().z - WallHeight/2;
		initializeEdges(p1,p2);
		edges = getEdges();
		defineShape(simulator);
	}
	
	
	protected void defineShape(Simulator simulator) {
		double[] xs = new double[4];
		xs[0] = edges[3].getP1().x;
		xs[1] = edges[1].getP1().x;
		xs[2] = edges[2].getP1().x;
		xs[3] = edges[0].getP1().x;
		
		double[] ys = new double[4];
		ys[0] = edges[3].getP1().y;
		ys[1] = edges[1].getP1().y;
		ys[2] = edges[2].getP1().y;
		ys[3] = edges[0].getP1().y;
		
		double[] yz = new double[4];
		ys[0] = 0;
		ys[1] = 0;
		ys[2] = 0;
		ys[3] = 0;
		
		this.shape = new PolygonShape(simulator, name, this, 0, 0, 0, 0, xs, ys, yz);
	}
	
	public void moveWall() {
		if(!is3D)
		initialize2DEdges();
		else
		initialize3DEdges();		
		edges = getEdges();
	}

	//2D Only
	private void initializeEdges(VectorLine p1, VectorLine p2) {
		
		if(p1.x > p2.x) {
			VectorLine temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		double angle = Math.atan2(p1.y-p2.y,p1.x-p2.x);
		angle+=Math.PI/2;
		
		double var = this.lenght;
		
		VectorLine p1a = new VectorLine(p1.x,p1.y,topLimit);	VectorLine p5a = new VectorLine(p1.x,p1.y,-topLimit);
		VectorLine p1b = new VectorLine(p2.x,p2.y,topLimit);	VectorLine p5b = new VectorLine(p2.x,p2.y,-topLimit);
		VectorLine side = new VectorLine(var*Math.cos(angle),var*Math.sin(angle));
		
		topAbove = new Edge(p1b,p1a);
		topBellow = new Edge(p5a,p5b);
		
		VectorLine p2a = new VectorLine(p1b);	VectorLine p6a = new VectorLine(p5a);
		VectorLine p2b = new VectorLine(p1b);	VectorLine p6b = new VectorLine(p5b);
		p2b.add(side);	p6b.add(side);
		
		rightAbove = new Edge(p2b,p2a);
		rightBellow = new Edge(p6b,p6a);
		
		VectorLine p3a = new VectorLine(p2b);	VectorLine p7a = new VectorLine(p6a);
		VectorLine p3b = new VectorLine(p1a);	VectorLine p7b = new VectorLine(p6b);
		p3b.add(side);	p7b.add(side);
		
		bottomAbove = new Edge(p3b, p3a);
		bottomBellow = new Edge(p7b, p7a);
		
		VectorLine p4a = new VectorLine(p3b);	VectorLine p8a = new VectorLine(p7a);
		VectorLine p4b = new VectorLine(p3b);	VectorLine p8b = new VectorLine(p7b);
		p4b.sub(side); p8b.sub(side);
		leftAbove = new Edge(p4b,p4a);
		leftBellow = new Edge(p4b,p4a);
	}
	
	//3D Version (DONE?)
	private void initialize3DEdges(VectorLine p1, VectorLine p2) {
		
		if(p1.x > p2.x) {
			VectorLine temp = p1;
			p1 = p2;
			p2 = temp;
		}
		
		double angle = Math.atan2(p1.y-p2.y,p1.x-p2.x);
		angle+=Math.PI/2;
		
		double var = this.lenght;
		double varheight = this.height;
		
		VectorLine p1a = new VectorLine(p1);
		VectorLine p1b = new VectorLine(p2);
		VectorLine side = new VectorLine(var*Math.cos(angle),var*Math.sin(angle));
		
		topAbove = new Edge(p1b,p1a);
		
		VectorLine p2a = new VectorLine(p1b);
		VectorLine p2b = new VectorLine(p1b);
		p2b.add(side);
		
		rightAbove = new Edge(p2b,p2a);
		
		VectorLine p3a = new VectorLine(p2b);
		VectorLine p3b = new VectorLine(p1a);
		p3b.add(side);
		
		bottomAbove = new Edge(p3b, p3a);

		VectorLine p4a = new VectorLine(p3b);
		VectorLine p4b = new VectorLine(p3b);
		p4b.sub(side);
		leftAbove = new Edge(p4b,p4a);
		
	}
	
	private void initialize2DEdges() {
		VectorLine topLeft = new VectorLine(getTopLeftX(), getTopLeftY(),0),
				topRight = new VectorLine(getTopLeftX() + getWidth(), getTopLeftY(),0),
				bottomLeft = new VectorLine(getTopLeftX(), getTopLeftY() - getLenght(),0),
				bottomRight = new VectorLine(getTopLeftX() + getWidth(), 
						getTopLeftY() - getLenght(),0);
		topAbove    = new Edge(topLeft, topRight);
		rightAbove  = new Edge(topRight, bottomRight);
		leftAbove   = new Edge(bottomLeft, topLeft);
		bottomAbove = new Edge(bottomRight, bottomLeft);
	}
	
	private void initialize3DEdges() {
		VectorLine topLeft = new VectorLine(getTopLeftX(), getTopLeftY(),0),
				topRight = new VectorLine(getTopLeftX() + getWidth(), getTopLeftY(),0),
				bottomLeft = new VectorLine(getTopLeftX(), getTopLeftY() - getLenght(),0),
				bottomRight = new VectorLine(getTopLeftX() + getWidth(), 
						getTopLeftY() - getLenght(),0);
		topAbove    = new Edge(topLeft, topRight);
		rightAbove  = new Edge(topRight, bottomRight);
		leftAbove   = new Edge(bottomLeft, topLeft);
		bottomAbove = new Edge(bottomRight, bottomLeft);
	}

	public double getWidth() {
		return width;
	}

	public double getLenght() {
		return lenght;
	}

	public double getTopLeftX() {
		return getPosition().getX() - width/2;
	}

	public double getTopLeftY(){
		return getPosition().getY() + lenght/2;
	}

	public Edge[] getEdges() {
		return new Edge[]{leftAbove, rightAbove, bottomAbove, topAbove};
	}

	public Edge[] get3DEdges() {
		return new Edge[]{leftAbove, rightAbove, bottomAbove, topAbove, leftBellow, rightBellow, bottomBellow, topBellow};
	}
	
	public double getHeight() {
		return height;
	}
	
	public class Edge implements Serializable{
		
		private VectorLine p1, p2;
		private VectorLine normal;
		
		public Edge(VectorLine p1, VectorLine p2){
			this.p1 = p1;
			this.p2 = p2;
			this.normal = new VectorLine();		
			this.normal.x = -(p2.y-p1.y);
			this.normal.y = (p2.x-p1.x);
		}

		public VectorLine getP1() {
			return p1;
		}

		public VectorLine getP2(){
			return p2;
		}
		
		public VectorLine getNormal() {
			return normal;
		}
	}
	
	public VectorLine intersectsWithLineSegment(VectorLine p1, VectorLine p2) {
		VectorLine closestPoint      = null;
		VectorLine lineSegmentVector = new VectorLine(p2);
		lineSegmentVector.sub(p1);
		
		for (Edge e : edges) {
			double dot = e.getNormal().dot(lineSegmentVector);
			if (dot < 0) {
				VectorLine e1 = e.getP1();
				VectorLine e2 = e.getP2();
				closestPoint = MathUtils.intersectLines(p1, p2, e1, e2);
				if(closestPoint != null) {
					break;
				}
			}
		}
		return closestPoint;
	}
	
	public VectorLine intersectsWithLineSegment(VectorLine p1, VectorLine p2, double maxReflectionAngle) {
		VectorLine closestPoint      = null;
		VectorLine lineSegmentVector = new VectorLine(p2);
		lineSegmentVector.sub(p1);
		
		for (Edge e : edges) {
//			double dot = e.getNormal().dot(lineSegmentVector);
//			if (dot < 0) {
				VectorLine e1 = e.getP1();
				VectorLine e2 = e.getP2();
				VectorLine intersection = MathUtils.intersectLines(p1, p2, e1, e2);
				if(intersection != null) {

//					double a = lineSegmentVector.angle(e.getNormal()) -Math.PI;
					
//					if(Math.abs(a) >= maxReflectionAngle)
//						intersection = null;
					
					if(closestPoint != null) {
						if(intersection.distanceTo(p1) < closestPoint.distanceTo(p1))
							closestPoint = intersection;
					} else {
						closestPoint = intersection;
					}
//					break;
//				}
			}
		}
		return closestPoint;
	}
	
	public double getMinDist(VectorLine pos) {
		double d = Double.MAX_VALUE;
		for (Edge e : edges) {
			VectorLine e1 = new VectorLine(e.getP1());
			VectorLine e2 = new VectorLine(e.getP2());
			d = Math.min(d,distToSegment(new VectorLine(pos), e1, e2));
		}
		return d;
	}
	/**
	 * "Shortest distance between a point and a line segment" by Grumdrig
	 * http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
	 */
	public static double distToSegment(VectorLine p, VectorLine v, VectorLine w) {

	       double l2 = FastMath.pow2(v.x - w.x) + FastMath.pow2(v.y - w.y);
	       if (l2 == 0.0) {
	           return p.distanceTo(v);   // v == w case
	       }
	       // Consider the line extending the segment, parameterized as v + t (w - v).
	       // We find projection of point p onto the line. 
	       // It falls where t = [(p-v) . (w-v)] / |w-v|^2
	       
	       VectorLine pp = new VectorLine(p); 
	       VectorLine vv = new VectorLine(v);
	       VectorLine ww = new VectorLine(w);
	       
	       pp.sub(v);
	       ww.sub(v);
	       double t = pp.dot(ww) / l2;
	       
	       if (t < 0.0) {
	           return p.distanceTo(v); // Beyond the 'v' end of the segment
	       } else if (t > 1.0) {
	           return p.distanceTo(w);  // Beyond the 'w' end of the segment
	       }
	       
	    // Projection falls on the segment

	       pp = new VectorLine(p);
	       vv = new VectorLine(v);
	       ww = new VectorLine(w);
	       
	       ww.sub(v);
	       ww.multiply(t);
	       vv.add(ww); 
	       
	       return p.distanceTo(vv);
	   }
	
	public static VectorLine debug(VectorLine p, VectorLine v, VectorLine w) {

	       double l2 = FastMath.pow2(v.x - w.x) + FastMath.pow2(v.y - w.y);
	       if (l2 == 0.0) {
	           return v;   // v == w case
	       }
	       // Consider the line extending the segment, parameterized as v + t (w - v).
	       // We find projection of point p onto the line. 
	       // It falls where t = [(p-v) . (w-v)] / |w-v|^2
	       
//	       double t = p.sub(v).dot(w.sub(v)) / l2;
	       
	       VectorLine pp = new VectorLine(p);
	       VectorLine vv = new VectorLine(v);
	       VectorLine ww = new VectorLine(w);
	       
	       pp.sub(v);
	       ww.sub(v);
	       double t = pp.dot(ww) / l2;
	       
	       if (t < 0.0) {
	           return v; // Beyond the 'v' end of the segment
	       } else if (t > 1.0) {
	           return w;  // Beyond the 'w' end of the segment
	       }
	       
	    // Projection falls on the segment
//	       Vector2d projection = v.add((w.sub(v)).multiply(t));
	       
	       pp = new VectorLine(p);
	       vv = new VectorLine(v);
	       ww = new VectorLine(w);
	       
	       ww.sub(v);
	       ww.multiply(t);
	       vv.add(ww); 
	       
	       return vv;
	   }
	
	
	@Override
	public double getDistanceBetween(VectorLine fromPoint) {
		
		VectorLine light = new VectorLine(position);
		lightDirection.set(light.getX()-fromPoint.getX(),light.getY()-fromPoint.getY());

		VectorLine intersection = intersectsWithLineSegment(lightDirection,fromPoint);
		if(intersection != null) {
			return intersection.length();
		}
		return fromPoint.distanceTo(position);
	}
}