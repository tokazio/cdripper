package fr.tokazio.player;

import javax.sound.sampled.*;

public class Devices {

    public static String AnalyzeControl(Control thisControl) {
        String type = thisControl.getType().toString();
        if (thisControl instanceof BooleanControl) {
            return "    Control: " + type + " (boolean)";
        }
        if (thisControl instanceof CompoundControl) {
            System.out.println("    Control: " + type +
                    " (compound - values below)");
            String toReturn = "";
            for (Control children :
                    ((CompoundControl) thisControl).getMemberControls()) {
                toReturn += "  " + AnalyzeControl(children) + "\n";
            }
            return toReturn.substring(0, toReturn.length() - 1);
        }
        if (thisControl instanceof EnumControl) {
            return "    Control:" + type + " (enum: " + thisControl.toString() + ")";
        }
        if (thisControl instanceof FloatControl) {
            return "    Control: " + type + " (float: from " +
                    ((FloatControl) thisControl).getMinimum() + " to " +
                    ((FloatControl) thisControl).getMaximum() + ")";
        }
        return "    Control: unknown type";
    }

    public void list() throws LineUnavailableException {
        System.out.println("OS: " + System.getProperty("os.name") + " " +
                System.getProperty("os.version") + "/" +
                System.getProperty("os.arch") + "\nJava: " +
                System.getProperty("java.version") + " (" +
                System.getProperty("java.vendor") + ")\n");
        int i = 0;
        for (Mixer.Info thisMixerInfo : AudioSystem.getMixerInfo()) {
            System.out.println("Mixer #" + i + ": " + thisMixerInfo.getDescription() +
                    " [" + thisMixerInfo.getName() + "]");
            Mixer thisMixer = AudioSystem.getMixer(thisMixerInfo);
            for (Line.Info thisLineInfo : thisMixer.getSourceLineInfo()) {
                if (thisLineInfo.getLineClass().getName().equals(
                        "javax.sound.sampled.Port")) {
                    Line thisLine = thisMixer.getLine(thisLineInfo);
                    thisLine.open();
                    System.out.println("  Source Port: "
                            + thisLineInfo.toString());
                    for (Control thisControl : thisLine.getControls()) {
                        System.out.println(AnalyzeControl(thisControl));
                    }
                    thisLine.close();
                }
            }
            for (Line.Info thisLineInfo : thisMixer.getTargetLineInfo()) {
                if (thisLineInfo.getLineClass().getName().equals(
                        "javax.sound.sampled.Port")) {
                    Line thisLine = thisMixer.getLine(thisLineInfo);
                    thisLine.open();
                    System.out.println("  Target Port: "
                            + thisLineInfo.toString());
                    for (Control thisControl : thisLine.getControls()) {
                        System.out.println(AnalyzeControl(thisControl));
                    }
                    thisLine.close();
                }
            }
            i++;
        }
    }
}
