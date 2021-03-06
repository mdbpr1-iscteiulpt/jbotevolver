package updatables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mathutils.VectorLine;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.util.Arguments;

/**
 *
 * @author jorge
 */
public class PathTracer extends Tracer {

    private boolean hideStart = false;
    private boolean hideFinal = false;
    private boolean fade = false;
    private int steps = 10;
    private HashMap<Robot, List<VectorLine>> points = null;
    private int frameCount = 0;

    public PathTracer(Arguments args) {
        super(args);
        hideStart = args.getFlagIsTrue("hidestart");
        hideFinal = args.getFlagIsTrue("hidefinal");
        fade = args.getFlagIsTrue("fade");
        steps = args.getArgumentAsIntOrSetDefault("steps", steps);
    }

    @Override
    public void update(Simulator simulator) {
        if (points == null) { // first step -- setup
            width = simulator.getEnvironment().getWidth();
            height = simulator.getEnvironment().getHeight();
            points = new HashMap<>();
        }

        if (insideTimeframe(simulator)) {
            // RECORD PATHS
            for (Robot r : simulator.getRobots()) {
                if (!points.containsKey(r)) {
                    points.put(r, new ArrayList<VectorLine>());
                }
                points.get(r).add(new VectorLine(r.getPosition()));
            }
        }
        super.update(simulator);
    }
    
    public void snapshot(Simulator simulator) {
    	// FIND BOUNDARIES
        double maxAbsX = 0, maxAbsY = 0;
        for (List<VectorLine> l : points.values()) {
            for (VectorLine v : l) {
                maxAbsX = Math.max(maxAbsX, Math.abs(v.x));
                maxAbsY = Math.max(maxAbsY, Math.abs(v.y));
            }
        }
        width = Math.max(Math.max(maxAbsX, maxAbsY) * 2,width);
        height = width;

        Graphics2D gr = createCanvas(simulator);

        // DRAW PATHS
        for (Robot r : points.keySet()) {
            List<VectorLine> pts = points.get(r);
            if (!fade) {
                int[] xs = new int[pts.size()];
                int[] ys = new int[pts.size()];
                for (int i = 0; i < pts.size(); i++) {
                    IntPos t = transform(pts.get(i).x, pts.get(i).y);
                    xs[i] = t.x;
                    ys[i] = t.y;
                }
                gr.setPaint(mainColor);
                gr.setStroke(new BasicStroke(1.0f));
                gr.drawPolyline(xs, ys, pts.size());
            } else {
                if (steps == 0) {
                    steps = pts.size();
                }
                int stepSize = (int) Math.ceil(pts.size() / (double) steps);
                for (int s = 0; s < steps; s++) {
                    // GET POINTS FOR SEGMENT
                    int start = s * stepSize;
                    int end = Math.min(start + stepSize, pts.size() - 1);
                    
                    if(end < start)
                    	continue;
                    
                    LinkedList<IntPos> polyLine = new LinkedList<>();
                    for (int i = 0; i <= end - start; i++) {
                        IntPos t = transform(pts.get(start + i).x, pts.get(start + i).y);
                        if (polyLine.isEmpty() || polyLine.getLast().x != t.x || polyLine.getLast().y != t.y) {
                            polyLine.add(t);
                        }
                    }
                    // CONVERT TO ARRAYS FORMAT
                    int[] xs = new int[polyLine.size()];
                    int[] ys = new int[xs.length];
                    int i = 0;
                    for (IntPos t : polyLine) {
                        xs[i] = t.x;
                        ys[i++] = t.y;
                    }

                    // DRAW POLYLINE
                    int alpha = Math.max(50, (int) Math.round((double) (s + 1) / steps * 255));
                    Color c = new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), alpha);

                    gr.setPaint(c);
                    gr.setStroke(new BasicStroke(lineWidth));
                    gr.drawPolyline(xs, ys, xs.length);
                }
            }
        }
        
        //size
//        gr.drawLine(0, 0, (int)(5*scale), 0);

        Color color;
        
        // DRAW INITIAL POSITIONS
        if (!hideStart) {
           
            
            for (Robot r : points.keySet()) {
            	
            	color = fade ? new Color(mainColor.getRed(), mainColor.getGreen(), mainColor.getBlue(), 50) : mainColor;
            	
                drawRobot(gr, r, points.get(r).get(0), true, color);
            }
        }

        // DRAW FINAL POSITIONS
        if (!hideFinal) {
            for (Robot r : points.keySet()) {
            	
            	color = mainColor;
            	
                drawRobot(gr, r, points.get(r).get(points.get(r).size() - 1), false, color);
            }
        }

        writeGraphics(gr, simulator, name.isEmpty() ? ""+frameCount++ : name);
    }

    @Override
    public void terminate(Simulator simulator) {
        snapshot(simulator);
    }
}
