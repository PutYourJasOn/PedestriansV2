package me.json.pedestrians.ui;

public enum StickFunction {

    ADD, REMOVE, SELECT, CONNECTION;

    public static StickFunction defaultStickFunction() {
        return SELECT;
    }
    private static final StickFunction[] values = values();

    public StickFunction next() {
        int i = (this.ordinal() + 1) % values.length;
        return StickFunction.values[i];
    }

}
