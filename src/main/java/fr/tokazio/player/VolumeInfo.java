package fr.tokazio.player;


/**
 * @author rpetit
 */
public class VolumeInfo {

    private float min = -1;
    private float max = -1;
    private float val = -1;

    public VolumeInfo(float minimum, float value, float maximum) {
        this.min = minimum;
        this.max = maximum;
        this.val = value;
    }

    public VolumeInfo() {

    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getVal() {
        return val;
    }

    //@JsonProperty
    public int percent() {
        return (int) (val / (min + max));
    }

}
