package implementation;

import inheritance.Vector3DInheritance;
import interfaces.IPolar2D;
import interfaces.IVector;

public class Main {

    private static void displayVectorInfo(String name, IVector v) {
        // współrzędne kartezjańskie
        double[] components = v.getComponents();
        System.out.print("\n" + name + " - Kartezjańskie: (");
        for (int i = 0; i < components.length; i++) {
            System.out.printf("%.2f%s", components[i], (i < components.length - 1 ? ", " : ""));
        }
        System.out.print(")");

        // współrzędne biegunowe (tylko dla wektorów zaimplementowanych z IPolar2D/Vector2D)
        if (components.length == 2 && (v instanceof Vector2D || (v instanceof Polar2DAdapter))) {
            IPolar2D polar;
            if (v instanceof Polar2DAdapter) {
                polar = (IPolar2D) v;
            } else {
                polar = new Polar2DAdapter(new Vector2D(components[0], components[1]));
            }
            double angleDeg = Math.toDegrees(polar.getAngle());
            System.out.printf(" | Biegunowe: |v|=%.2f, Kąt=%.2f°", polar.abs(), angleDeg);
        } else {
            System.out.printf(" | Biegunowe: N/A (Wektor %dD)", components.length);
        }
        System.out.println();
    }

    private static void displayDotProduct(IVector v1, IVector v2, String name1, String name2) {
        try {
            double dotProduct = v1.cdot(v2);
            System.out.printf("\nIloczyn Skalarny (%s · %s): %.2f", name1, name2, dotProduct);
        } catch (IllegalArgumentException e) {
            System.out.printf("\nIloczyn Skalarny (%s · %s): BŁĄD - %s", name1, name2, e.getMessage());
        }
    }

    private static void displayCrossProduct(IVector v1, IVector v2, String name1, String name2) {
        IVector v3D1 = v1.getComponents().length == 2 ? new Vector3DDecorator(new Vector2D(v1.getComponents()[0], v1.getComponents()[1]), 0.0) : v1;

        IVector v3D2 = v2.getComponents().length == 2 ? new Vector3DDecorator(new Vector2D(v2.getComponents()[0], v2.getComponents()[1]), 0.0) : v2;

        try {
            IVector crossProduct = null;
            if (v3D1 instanceof Vector3DInheritance) {
                crossProduct = ((Vector3DInheritance) v3D1).cross(v3D2);
            } else if (v3D1 instanceof Vector3DDecorator) {
                crossProduct = ((Vector3DDecorator) v3D1).cross(v3D2);
            } else {
                throw new UnsupportedOperationException("Pierwszy wektor 3D nie ma zaimplementowanej metody cross.");
            }

            double[] components = crossProduct.getComponents();
            System.out.printf("\nIloczyn Wektorowy (%s x %s) - Kartezjańskie: (%.2f, %.2f, %.2f)",
                    name1, name2, components[0], components[1], components[2]);

        } catch (IllegalArgumentException e) {
            System.out.printf("\nIloczyn Wektorowy (%s x %s): BŁĄD - %s", name1, name2, e.getMessage());
        } catch (UnsupportedOperationException e) {
            System.out.printf("\nIloczyn Wektorowy (%s x %s): BŁĄD - %s", name1, name2, e.getMessage());
        }
    }

    public static void main(String[] args) {
        IVector v1 = new Vector2D(3.0, 4.0);
        IVector v2 = new Vector3DInheritance(-1, 5, 2);
        IVector v3 = new Vector3DDecorator(new Vector2D(0, -3), 1);

        System.out.println("\n\n## Wyświetlanie Współrzędnych ##");
        displayVectorInfo("V1: ", v1);
        displayVectorInfo("V2: ", v2);
        displayVectorInfo("V3: ", v3);

        System.out.println("\n\n## Wyniki Iloczynu Skalarnego ##");
        displayDotProduct(v1, v2, "V1", "V2");
        displayDotProduct(v1, v3, "V1", "V3");
        displayDotProduct(v2, v3, "V2", "V3");

        System.out.println("\n\n## Wyniki Iloczynu Wektorowego ##");
        displayCrossProduct(v1, v2, "V1", "V2");
        displayCrossProduct(v1, v3, "V1", "V3");
        displayCrossProduct(v2, v1, "V2", "V1");
        displayCrossProduct(v2, v3, "V2", "V3");
        displayCrossProduct(v3, v1, "V3", "V1");
        displayCrossProduct(v3, v2, "V3", "V2");
    }
}