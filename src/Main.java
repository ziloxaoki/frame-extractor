import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static File getNextMacro(String path) {
        File macro = new File(path+"Macro.jim");
        int sufix = 1;
        while(macro.exists()) {
            macro = new File(path+"Macro-"+sufix+".jim");
            sufix++;
        }
        return macro;
    }

    public static void generateMacro(File macro) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        String path = macro.getAbsolutePath().replace(macro.getName(),"");

        try {

            String content = "run(\"Image Sequence...\", \"open="+ path.replace("\\","\\\\") +"scene00001.jpg sort\");\n" +
                    "run(\"8-bit\");\n" +
                    "//setTool(\"line\");\n" +
                    "makeLine(118, 200, 118, 226);\n" +
                    "run(\"Set Scale...\", \"distance=25.917 known=1 pixel=1 unit=cm global\");\n" +
                    "//setTool(\"rectangle\");\n" +
                    "makeRectangle(202, 57, 196, 398);\n" +
                    "run(\"Brightness/Contrast...\");\n" +
                    "setMinAndMax(195, 197);\n" +
                    "run(\"Apply LUT\", \"stack\");\n" +
                    "setAutoThreshold(\"Default dark\");\n" +
                    "run(\"Threshold...\");\n" +
                    "setThreshold(36, 255);\n" +
                    "setOption(\"BlackBackground\", false);\n" +
                    "run(\"Convert to Mask\", \"method=Default background=Dark\");\n" +
                    "run(\"Close\");\n" +
                    "run(\"Analyze Particles...\", \"size=0.4-Infinity show=Outlines display exclude stack\");\n" +
                    "if (isOpen(\"Results\")) { \n" +
                    "   selectWindow(\"Results\"); \n" +
                    "   saveAs(\"Results\", \"" + path.replace("\\","\\\\") + "Results.txt\");\n" +
                    "} ";

            fw = new FileWriter(macro);
            bw = new BufferedWriter(fw);
            bw.write(content);

            System.out.println("Done");

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

    public static void extractFrames(File folder) {
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {

            if (listOfFiles[i].isFile()) {

                File f = listOfFiles[i];

                String name = f.getName();
                String path = f.getAbsolutePath().replace(f.getName(),"");

                String cmd = "ffmpeg.exe -i \"" + path + name +
                        "\" -vf fps=5 \"" + path + "images\\scene%05d.jpg\" -hide_banner";

                if(name.contains("Half_Lifetime") && name.endsWith(".mp4")) {

                    try {
                        System.out.println(cmd);
                        File imgFolder = new File(path + "images");
                        if (!imgFolder.exists()) {
                            imgFolder.mkdir();
                        }
                        Process vlc = Runtime.getRuntime().exec(cmd);
                        vlc.waitFor();
                        //File macro = getNextMacro(path + "images\\");
                        //generateMacro(macro);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                extractFrames(listOfFiles[i]);
            }
        }
    }



    public static void main(String[] argv) throws IOException {
        //xcopy "1-Octanol" "g:\octanol" /exclude:exc.txt /e /i /y /s
        //Trocar o folder abaixo ------------------------------------------------------------------------
        extractFrames(new File("N:\\UNSW\\Experiments\\Froth_analysis\\Frothers\\PPG1000"));
//----------------------------------------------------------------------------------------------------
        System.out.println("Conversion is completed.");
    }

}

