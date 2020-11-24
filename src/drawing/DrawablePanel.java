package drawing;

import calc.Body;
import calc.Calculating;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DrawablePanel extends JPanel {
    Color[] bodyColorList = {
            Color.black, Color.red, Color.green, Color.blue, Color.cyan, Color.yellow, Color.orange,
            Color.DARK_GRAY, Color.lightGray};

	private static final long serialVersionUID = 1L;
	static private final int WIDTH=1000, HEIGHT=660;
    BufferedImage image;
    Graphics2D g2image;
    Body[] body;
    Calculating calculating;
    int temp=0;

    // variables for database system
    Vector<Integer> bodyIDListVector;
    Vector<Integer> bodyMassVector;
    Vector<Double> xPosListVector;
    Vector<Double> yPosListVector;
    Vector<Double> vXListVector;
    Vector<Double> vYListVector;

    public DrawablePanel(Body[] bodyPassed) {
        this.setBackground(Color.GREEN);
        this.body = bodyPassed;

        // create class for calculating new position
        calculating = new Calculating(bodyPassed);
    }

    public void startSimulation(){
        for(int i = 0; i < body.length; i++) {
            calculating.calculateXPos(i);
            calculating.calculateYPos(i);

            // adding data to vector, this vector will be saved in database system
            bodyIDListVector.add(body[i].getBodyID());
            bodyMassVector.add(body[i].getMass());
            xPosListVector.add(body[i].getxPos());
            yPosListVector.add(body[i].getyPos());
            vXListVector.add(body[i].getvX());
            vYListVector.add(body[i].getvY());
        }
    }

    public void initializeVectorForNewSimulation() {
        bodyIDListVector = new Vector<>();
        bodyMassVector = new Vector<>();
        xPosListVector = new Vector<>();
        yPosListVector = new Vector<>();
        vXListVector = new Vector<>();
        vYListVector = new Vector<>();
    }

    public void beginDrawing() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        //creating images
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);	
        g2image = image.createGraphics();
        
        scheduler.scheduleAtFixedRate( (new Runnable() {
            @Override
            public void run() {
                //drawing background
                g2image.setColor(Color.white);
                g2image.fillRect(0, 0, image.getWidth(), image.getHeight());

                //drawing X and Y lines
                g2image.setColor(Color.black);
                g2image.drawLine(0,330,1000,330);
                g2image.drawLine(500,0,500,660);
                
                //drawing points
                g2image.setColor(Color.black);
                for (int i = 0; i < body.length; i++) {
                    g2image.setColor(bodyColorList[i]);
                    g2image.fillOval((int)Math.round(body[i].getxPos()), (int)Math.round(body[i].getyPos()), 8, 8);
                }
                repaint();

            }
        }), 0, 50, MILLISECONDS);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D)g;
        graphics2D.drawImage(image, null, 0, 0);
    }

    public Vector<Integer> getBodyIDListVector() {
        return bodyIDListVector;
    }

    public Vector<Integer> getBodyMassListVector() {
        return bodyMassVector;
    }

    public Vector<Double> getxPosListVector() {
        return xPosListVector;
    }

    public Vector<Double> getyPosListVector() {
        return yPosListVector;
    }

    public Vector<Double> getvXListVector() {
        return vXListVector;
    }

    public Vector<Double> getvYListVector() {
        return vYListVector;
    }

    public Dimension getPreferredSize() {
        return new Dimension(WIDTH, HEIGHT);
    }
}
