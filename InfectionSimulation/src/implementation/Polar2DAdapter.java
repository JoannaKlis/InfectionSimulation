package implementation;

import interfaces.IPolar2D;
import interfaces.IVector;

public class Polar2DAdapter implements IPolar2D, IVector {
    private final Vector2D srcVector;

    public Polar2DAdapter(Vector2D srcVector) {
        this.srcVector = srcVector;
    }

    @Override
    public double abs() {
        return srcVector.abs();
    }

    @Override
    public double getAngle() {
        double[] c = srcVector.getComponents();
        double x = c[0];
        double y = c[1];
        double angle = Math.atan2(y, x);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    public double cdot(IVector param) {
        double[] c = param.getComponents();

        if (c.length != 2) {
            throw new IllegalArgumentException("Polar2DAdapter może wykonać iloczyn skalarny tylko z innym wektorem 2D.");
        }
        return srcVector.cdot(param);
    }

    @Override
    public double[] getComponents() {
        return srcVector.getComponents();
    }
}