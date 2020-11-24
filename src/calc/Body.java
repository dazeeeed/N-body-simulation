package calc;

public class Body {
    static int bodyCounter = 0;
    private int bodyID = -1; // -1 is default option where body was not initialized with constructor!
    private double xPos = 500;
    private double yPos = 330;
    private double vX = 150;
    private double vY = 150;
    private int mass = 10;

    public Body(int xPos, int yPos, int vX, int vY, int mass) {

        this.xPos = xPos;
        this.yPos = yPos;
        this.vX = vX;
        this.vY = vY;
        this.mass = mass;
        bodyID = bodyCounter;
        bodyCounter ++;
    }

    public void setxPos(double xPos) {
        this.xPos = xPos;
    }

    public double getxPos() {
        return xPos;
    }

    public void setyPos(double yPos) {
        this.yPos = yPos;
    }

    public double getyPos() {
        return yPos;
    }

    public void setvX(double vX) {
        this.vX = vX;
    }

    public double getvX() {
        return  vX;
    }

    public void setvY(double vY) {
        this.vY = vY;
    }

    public double getvY() {
        return vY;
    }

    public void setMass(int mass) {
        this.mass = mass;
    }

    public int getMass() {
        return mass;
    }

    public int getBodyID() {
        return bodyID;
    }
}
