/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package updatables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import mathutils.VectorLine;

import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import simulation.Simulator;
import simulation.Stoppable;
import simulation.physicalobjects.Line;
import simulation.physicalobjects.PhysicalObject;
import simulation.physicalobjects.Wall;
import simulation.physicalobjects.Wall.Edge;
import simulation.robot.Robot;
import simulation.util.Arguments;

/**
 *
 * @author jorge
 */
public abstract class Tracer implements Stoppable {

    protected File folder = new File("traces");
    protected Color mainColor = Color.YELLOW;
    protected Color altColor = Color.RED;
    protected Color bgColor = Color.WHITE;
    protected int margin = 50;
    protected double scale = 5;
    protected int timeStart = 0;
    protected int timeEnd = -1;
    protected double width, height;
    protected List<Line> lines;
    protected boolean drawGeofence = true;
    protected boolean drawWalls = true;
    protected String name = "";

    protected boolean usePNG = false;
    private boolean isSetup = false;
    protected BufferedImage im;
    protected float lineWidth = 1;
    private int snapshotFrequency = 300;

    public Tracer(Arguments args) {
        margin = args.getArgumentAsIntOrSetDefault("imagemargin", margin);
        timeStart = args.getArgumentAsIntOrSetDefault("timestart", timeStart);
        timeEnd = args.getArgumentAsIntOrSetDefault("timeend", timeEnd);
        scale = args.getArgumentAsDoubleOrSetDefault("scale", scale);
        usePNG = args.getFlagIsTrue("png");
        lineWidth = (float) args.getArgumentAsDouble("linewidth");
        if (args.getArgumentIsDefined("maincolor")) {
            mainColor = parseColor(args.getArgumentAsString("maincolor"));
        }
        if (args.getArgumentIsDefined("altcolor")) {
            altColor = parseColor(args.getArgumentAsString("altcolor"));
        }
        if (args.getArgumentIsDefined("bgcolor")) {
            bgColor = parseColor(args.getArgumentAsString("bgcolor"));
        }        
        if (args.getArgumentIsDefined("folder")) {
            folder = new File(args.getArgumentAsString("folder"));
        }
        if (args.getArgumentIsDefined("drawgeofence")) {
            drawGeofence = args.getFlagIsTrue("drawgeofence");
        }
        if (args.getArgumentIsDefined("drawwalls")) {
            drawWalls = args.getFlagIsTrue("drawwalls");
        }
        
        snapshotFrequency = args.getArgumentAsIntOrSetDefault("snapshotfrequency", snapshotFrequency);

        name = args.getArgumentAsStringOrSetDefault("name", name);
    }

    public class IntPos {

        public int x;
        public int y;

        IntPos(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    protected IntPos transform(double x, double y) {
        return new IntPos(margin + (int) Math.round((x + width / 2) * scale),
                margin + (int) Math.round((height / 2 - y) * scale));
    }

    protected IntPos transformNoShift(double x, double y) {
        return new IntPos(margin + (int) Math.round(x * scale),
                margin + (int) Math.round((height - y) * scale));
    }

    public static Color parseColor(String str) {
        String[] split = str.split("-|\\.|,|;");
        if (split.length == 1) {
            try {
                Field field = Color.class.getField(split[0]);
                return (Color) field.get(null);
            } catch (Exception ex) {
                return Color.decode(split[0]);
            }
        } else if (split.length == 3) {
            return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        } else if (split.length == 4) {
            return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        }
        return null;
    }

    protected Graphics2D createCanvas(Simulator sim) {
        int w = (int) (width * scale) + margin * 2;
        int h = (int) (height * scale) + margin * 2;

        if (usePNG) {
            im = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics g = im.getGraphics();
            return (Graphics2D) g;
        } else {
            SVGGraphics2D gr = new SVGGraphics2D(w, h);
            gr.setPaint(bgColor);
            gr.fillRect(0, 0, w, h);
            return gr;
        }
    }

    protected void writeGraphics(Graphics2D gr, Simulator sim, String filePrefix) {
        if (drawGeofence) {
            drawGeofence(gr, sim);
        }
        
        if(drawWalls)
        	drawWalls(gr,sim);
        
        if(!folder.exists())
        	folder.mkdirs();
        
        File out = new File(folder, (name.isEmpty() ? filePrefix : name) + (usePNG ? ".png" : ".svg"));
        try {
            if (usePNG) {
                ImageIO.write(im, "PNG", out);
            } else {
                SVGUtils.writeToSVG(out, ((SVGGraphics2D) gr).getSVGElement());
            }
        } catch (IOException ex) {
            Logger.getLogger(Tracer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void drawRobots(Graphics2D gr, Simulator sim, boolean useSquare, Color c) {
        for (Robot r : sim.getRobots()) {
            drawRobot(gr, r, null, useSquare, c);
        }
    }

    protected void drawRobot(Graphics2D gr, Robot r, VectorLine position, boolean useSquare, Color c) {
        gr.setPaint(c);
        if (position == null) {
            position = r.getPosition();
        }
        int size = (int) Math.round(r.getRadius() * 2 * scale * lineWidth);
        IntPos iPos = transform(position.x, position.y);
        if (useSquare) {
            gr.fillRect(iPos.x - size / 2, iPos.y - size / 2, size, size);
        } else {
            gr.fillOval(iPos.x - size / 2, iPos.y - size / 2, size, size);
        }
    }
    
    protected void drawWalls(Graphics2D gr, Simulator sim) {
    	
    	for(PhysicalObject p : sim.getEnvironment().getAllObjects()) {
    		if(p instanceof Wall) {
    			Wall w = (Wall)p;
    			gr.setColor(w.color);
    			
    			Edge[] edges = w.getEdges();
    			
    			int[] xs = new int[4];
    			int[] ys = new int[4];
    			
    			IntPos pos = transform(edges[3].getP1().x,edges[3].getP1().y);
    			xs[0] = pos.x;
    			ys[0] = pos.y;
    			
    			pos = transform(edges[1].getP1().x,edges[1].getP1().y);
    			xs[1] = pos.x;
    			ys[1] = pos.y;
    			
    			pos = transform(edges[2].getP1().x,edges[2].getP1().y);
    			xs[2] = pos.x;
    			ys[2] = pos.y;
    			
    			pos = transform(edges[0].getP1().x,edges[0].getP1().y);
    			xs[3] = pos.x;
    			ys[3] = pos.y;
    			
    			gr.fillPolygon(xs, ys, 4);
    			
    			gr.setColor(Color.BLACK);
    		}
    	}
    	
    }

    protected void drawGeofence(Graphics2D gr, Simulator sim) {
        if (lines == null) {
            //setupGeofence(sim);
        }
        if (lines != null) {
            gr.setPaint(altColor);
            BasicStroke dashed = new BasicStroke(lineWidth,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, new float[]{10f}, 0.0f);
            gr.setStroke(dashed);
            for (Line l : lines) {
                VectorLine pointA = l.getPointA();
                VectorLine pointB = l.getPointB();
                IntPos pa = transform(pointA.x, pointA.y);
                IntPos pb = transform(pointB.x, pointB.y);
                gr.drawLine(pa.x, pa.y, pb.x, pb.y);
            }
        }
    }

    /*
    protected void setupGeofence(Simulator sim) {
    	//TODO
    }

    protected List<Line> getLines(LinkedList<Waypoint> waypoints, Simulator simulator) {
        List<Line> linesList = new ArrayList<Line>();
        for (int i = 1; i < waypoints.size(); i++) {

            Waypoint wa = waypoints.get(i - 1);
            Waypoint wb = waypoints.get(i);
            commoninterface.mathutils.Vector2d va = CoordinateUtilities.GPSToCartesian(wa.getLatLon());
            commoninterface.mathutils.Vector2d vb = CoordinateUtilities.GPSToCartesian(wb.getLatLon());

            simulation.physicalobjects.Line l = new simulation.physicalobjects.Line(simulator, "line" + i, va.getX(), va.getY(), vb.getX(), vb.getY());
            linesList.add(l);
        }

        Waypoint wa = waypoints.get(waypoints.size() - 1);
        Waypoint wb = waypoints.get(0);
        commoninterface.mathutils.Vector2d va = CoordinateUtilities.GPSToCartesian(wa.getLatLon());
        commoninterface.mathutils.Vector2d vb = CoordinateUtilities.GPSToCartesian(wb.getLatLon());

        simulation.physicalobjects.Line l = new simulation.physicalobjects.Line(simulator, "line0", va.getX(), va.getY(), vb.getX(), vb.getY());
        linesList.add(l);
        return linesList;
    }*/

    protected boolean insideTimeframe(Simulator sim) {
        return sim.getTime() >= timeStart && (timeEnd == -1 || sim.getTime() <= timeEnd);
    }
    
    public abstract void snapshot(Simulator simulator);
    
    public void setup(Simulator sim) {
    	double maxXAbs = 0, maxYAbs = 0;

        //setupGeofence(sim);

        if (lines != null) {

            for (Line l : lines) {
                maxXAbs = Math.max(maxXAbs, Math.abs(l.getPointA().x));
                maxYAbs = Math.max(maxYAbs, Math.abs(l.getPointA().y));
            }

            width = Math.max(maxXAbs, maxYAbs) * 2;
            height = width;
        } else {
            width = sim.getEnvironment().getWidth();
            height = width;
        }
    }
    
    @Override
    public void update(Simulator sim) {
    	
    	 if (!isSetup) {
             setup(sim);
             isSetup = true;
         }

    	 if (sim.getTime() % snapshotFrequency == 0 && insideTimeframe(sim)) {
            snapshot(sim);
        }
    }
}
