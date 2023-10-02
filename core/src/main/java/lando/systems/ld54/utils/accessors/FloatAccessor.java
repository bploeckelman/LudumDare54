package lando.systems.ld54.utils.accessors;

import aurelienribon.tweenengine.TweenAccessor;

public class FloatAccessor implements TweenAccessor<Float> {
    public static final int VALUE = 1;

    @Override
    public int getValues(Float target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case VALUE:
                returnValues[0] = target; // Set the float value to returnValues[0]
                return 1; // We're returning a single value
            default:
                return 0; // Invalid tween type
        }
    }

    @Override
    public void setValues(Float target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case VALUE:
                target = newValues[0]; // Set the float value to the new value
                break;
            default: assert false;
        }
    }
}
