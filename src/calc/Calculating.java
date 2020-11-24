package calc;

import java.text.DecimalFormat;

public class Calculating {

    private final static double G = 1.5e-1;
    Body[] body;
    double xDif=0, yDif=0, acc=0;
    final DecimalFormat df = new DecimalFormat("#0.0000");

    public Calculating(Body[] bodyPassed) {
        this.body = bodyPassed;
    }

    public void calculateXPos(int bodyIndex) {
       double newSpeed = calculateVelocityX(bodyIndex, 1);
       double xPos = body[bodyIndex].getxPos() + newSpeed;
       
       if (xPos > 1000) {
          // body[bodyIndex].setxPos(990);
          // body[bodyIndex].setvX(-body[bodyIndex].getvX());
    	   body[bodyIndex].setvX(body[bodyIndex].getvX()/50);
    	   body[bodyIndex].setxPos(0);
       } else if (xPos < 0) {
          // body[bodyIndex].setxPos(1);
          // body[bodyIndex].setvX(-body[bodyIndex].getvX());
    	   body[bodyIndex].setvX(body[bodyIndex].getvX()/50);
    	   body[bodyIndex].setxPos(990);
       } else {
           body[bodyIndex].setxPos(xPos);
           body[bodyIndex].setvX(newSpeed);
       }
       
       // System.out.println(body[0].getvX());
    }

    public void calculateYPos(int bodyIndex) {
        double newSpeed = calculateVelocityY(bodyIndex, 1);
        double yPos = body[bodyIndex].getyPos() + newSpeed;
        
        if (yPos > 660) {
        	body[bodyIndex].setyPos(0);
           // body[bodyIndex].setvX(-body[bodyIndex].getvX());
        	body[bodyIndex].setvY(body[bodyIndex].getvY()/50);
        } else if (yPos < 0) {
        	body[bodyIndex].setyPos(660);
           // body[bodyIndex].setvX(-body[bodyIndex].getvX());
        	body[bodyIndex].setvY(body[bodyIndex].getvY()/50);
        } else {
            body[bodyIndex].setyPos(yPos);
            body[bodyIndex].setvY(newSpeed);
        }
        
        // System.out.println(body[0].getvX());
     }

    // return -1 if not found this body
    private int getBodyIndexByID(int bodyID) {
        int bodyIndex = -1;
        for(int i = 0; i < body.length; i++) {
            if (body[i].getBodyID() == bodyID) {
                bodyIndex = i;
            }
        }
        return bodyIndex;
    }

    private double calculateAccX(int bodyIndex) {
        double acc = 0;
        for(Body b: body) {
            xDif = b.getxPos() - body[bodyIndex].getxPos();
            if(body[bodyIndex].getBodyID() != b.getBodyID()) {
            	if( (xDif*xDif + yDif*yDif) >= 3 )
            		acc =  b.getMass()/ (xDif*xDif + yDif*yDif) * Math.signum(xDif);
            }
        }
        //System.out.println("Body "+bodyIndex+": acc X= "+  df.format(acc));
        //System.out.println("Body "+bodyIndex+": pos X= "+  df.format(body[bodyIndex].getxPos()));
        
        if(acc<2)
        	return G * acc;
        else return G*acc/50;
        
        //return acc;
    }

    private double calculateAccY(int bodyIndex) {
        double acc = 0;
    	yDif=0;
        for(Body b: body) {
            yDif = b.getyPos() - body[bodyIndex].getyPos();
            if(body[bodyIndex].getBodyID() != b.getBodyID()) {
            	if( (xDif*xDif + yDif*yDif) >= 3 )
            		acc =  b.getMass() / (xDif*xDif + yDif*yDif) * Math.signum(yDif);
            }
        }
        //System.out.println("Body "+bodyIndex+": acc Y= "+ df.format(acc) );
        if(acc<2)
        	return G * acc;
        else return G*acc/50;
        
        //return acc;
    }

    private double calculateVelocityX(int bodyIndex, int timeStamp) {
        double speed = body[bodyIndex].getvX() + calculateAccX(bodyIndex) * timeStamp;
        /*
        if(speed > 20) return 0;
        if(speed < -20) return 0;
        else return speed;
        */
        //System.out.println("Body "+bodyIndex+": speed X= "+df.format(body[bodyIndex].getvX()));
        return speed;
            
        
    }

    private double calculateVelocityY(int bodyIndex, int timeStamp) {
        double speed = body[bodyIndex].getvY() + calculateAccY(bodyIndex) * timeStamp;
        /*
        if(speed > 20) return 0;
        if(speed < -20) return 0;
        else return speed;
        */
        //System.out.println("Body "+bodyIndex+": speed Y= "df.format(speed));
        return speed;
            
        
    }

}
