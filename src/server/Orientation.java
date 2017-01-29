package server;

public enum Orientation {
    HORIZONTAL,
    VERTICAL;

    public static Orientation getByValue(String value) {
        if (value.startsWith("v")) {
            return Orientation.VERTICAL;
        } else if (value.startsWith("h")) {
            return Orientation.HORIZONTAL;
        }
        return null;
    }
}
