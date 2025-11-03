package implementation;

import interfaces.IVector;

public class Vector3DDecorator implements IVector {
    private final IVector srcVector;
    private final double z;

    public Vector3DDecorator(IVector srcVector, double z) {
        if (srcVector.getComponents().length != 2) {
            throw new IllegalArgumentException("Wektor źródłowy dla dekoratora 3D musi być 2D (mieć 2 komponenty).");
        }
        this.srcVector = srcVector;
        this.z = z;
    }

    @Override
    public double abs() {
        double abs2D = srcVector.abs();
        return Math.sqrt(abs2D * abs2D + z * z);
    }

    @Override
    public double cdot(IVector param) {
        double[] c = param.getComponents();
        double[] c1_2D = srcVector.getComponents();

        double x1 = c1_2D[0];
        double y1 = c1_2D[1];
        double z1 = this.z;

        if (c.length == 3) {
            double x2 = c[0];
            double y2 = c[1];
            double z2 = c[2];
            return x1 * x2 + y1 * y2 + z1 * z2;
        } else if (c.length == 2) {
            double x2 = c[0];
            double y2 = c[1];
            double z2 = 0;
            return x1 * x2 + y1 * y2 + z1 * z2;
        } else {
            throw new IllegalArgumentException("Wektor do iloczynu skalarnego musi mieć 2 lub 3 wymiary.");
        }
    }

    @Override
    public double[] getComponents() {
        double[] c2 = srcVector.getComponents();
        return new double[]{c2[0], c2[1], z};
    }

    public Vector3DDecorator cross(IVector other) {
        double[] c = other.getComponents();
        if (c.length != 3) {
            throw new IllegalArgumentException("Iloczyn wektorowy jest zdefiniowany tylko dla dwóch wektorów 3D.");
        }
        double x1 = this.getComponents()[0];
        double y1 = this.getComponents()[1];
        double z1 = this.z;
        double x2 = c[0];
        double y2 = c[1];
        double z2 = c[2];

        double crossX = y1 * z2 - z1 * y2;
        double crossY = z1 * x2 - x1 * z2;
        double crossZ = x1 * y2 - y1 * x2;

        IVector result2D = new Vector2D(crossX, crossY);
        return new Vector3DDecorator(result2D, crossZ);
    }

    public IVector getSrcV() {
        return srcVector;
    }
}