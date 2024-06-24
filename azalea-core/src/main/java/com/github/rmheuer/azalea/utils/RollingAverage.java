package com.github.rmheuer.azalea.utils;

public final class RollingAverage {
    private final float[] values;
    private int writeIdx;
    private boolean filled;

    public RollingAverage(int historyLen) {
        values = new float[historyLen];
        writeIdx = 0;
        filled = false;
    }

    public void recordValue(float value) {
        values[writeIdx++] = value;
        if (writeIdx >= values.length) {
            writeIdx = 0;
            filled = true;
        }
    }

    public float getAverage() {
        int count;
        float total = 0;
        if (filled) {
            count = values.length;
            for (float f : values)
                total += f;
        } else {
            count = writeIdx;
            for (int i = 0; i < count; i++)
                total += values[i];
        }
        return total / count;
    }
}
