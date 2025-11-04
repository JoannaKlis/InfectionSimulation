package implementation;

import interfaces.IVector;

public class Vector2D implements IVector {
    protected double x;
    protected double y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Nowa metoda: dodawanie wektora
    public Vector2D add(Vector2D other) {
        this.x += other.x;
        this.y += other.y;
        return this; // Zwracamy obiekt, aby móc łączyć operacje (chaining)
    }

    // Nowa metoda: skalowanie wektora przez skalar
    public Vector2D scale(double scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    // Nowa metoda: ustawienie nowych komponentów
    public void setComponents(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Nowa metoda: tworzenie kopii (niezbędne, aby nie zmieniać referencji)
    public Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }

    // Zabezpieczenie przed błędem z przesłanego pliku: Wektor musi być mutable do symulacji
    public double getX() { return x; }
    public double getY() { return y; }

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