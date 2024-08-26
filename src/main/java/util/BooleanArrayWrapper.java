package util;

import java.util.Arrays;

public class BooleanArrayWrapper {
    private boolean[] array;

    public BooleanArrayWrapper(boolean[] array) {
        this.array = array;
    }

    public boolean[] getArray() {
        return array;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BooleanArrayWrapper other = (BooleanArrayWrapper) obj;
        return Arrays.equals(array, other.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}