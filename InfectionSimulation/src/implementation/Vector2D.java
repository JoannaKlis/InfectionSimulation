package implementation;

import interfaces.IVector;

public class Vector2D implements IVector {
    protected double x;
    protected double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double abs() {
        return Math.sqrt(x * x + y * y);
    }

    @Override
    public double cdot(IVector param) {
        double[] c = param.getComponents();
        if (c.length < 2) {
            throw new IllegalArgumentException("Wektor do iloczynu skalarnego musi mieć co najmniej 2 wymiary.");
        }
        return this.x * c[0] + this.y * c[1]; // iloczyn skalarny dla 2D: x1*x2 + y1*y2
    }


    @Override
    public double[] getComponents() {
        return new double[]{x, y};
    }
}