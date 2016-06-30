package org.icetools.convertmats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ConvertMats {

    private File sourceFile;
    private File destDir;

    public static void main(String[] args) throws Exception {
        ConvertMats m = new ConvertMats();
        m.setSourceFile(new File(args[0]));
        m.setDestDir(new File(args[1]));
        m.go();
    }

    public File getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }

    public File getDestDir() {
        return destDir;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void go() throws IOException {
        InputStream in = new FileInputStream(sourceFile);
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            JMEMaterial mat = null;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.startsWith("//")) {
                } else if (line.startsWith("material ")) {
                    if (mat != null) {
                        writeMaterial(mat);
                    }
                    mat = new JMEMaterial();

                    // Name / dir
                    String matPath = line.substring(9);
                    int idx = matPath.lastIndexOf('/');
                    String dirName = null;
                    if (idx != -1) {
                        dirName = matPath.substring(0, idx);
                        mat.dir = new File(destDir, dirName.replace('/', File.separatorChar));
                        mat.name = matPath.substring(idx + 1);
                    } else {
                        mat.dir = destDir;
                        mat.name = matPath;
                    }
                } else if (mat != null) {
                    if (line.startsWith("scene_blend")) {
                        String[] a = line.split("\\s+");
                        if (a.length > 1) {
                            if (a[1].equals("add")) {
                                mat.sceneBlend = "Additive";
                            } else if (a[1].equals("alpha_blend")) {
                                mat.sceneBlend = "Alpha";
                            } else {
                                throw new IOException("Unknown scene_blend " + a[1]);
                            }
                        }
                        else {                            
                                throw new IOException("No scene_blend");
                        }
                    } else if (line.startsWith("point_sprites ")) {
                        mat.pointSprites = getBool(line);
                    } else if (line.startsWith("depth_write ")) {
                        mat.depthWrite = getBool(line);
                    } else if (line.startsWith("texture ")) {
                        String[] a = line.split("\\s+");
                        if (a.length > 1) {
                            mat.texture = a[1];
                        } else {
                            throw new IOException("Expected texture");
                        }
                    }
                } else {
                    throw new IOException("Material parameters before name.");
                }
            }
            if (mat != null) {
                writeMaterial(mat);
            }
        } finally {
            in.close();
        }
    }

    private Boolean getBool(String line) throws IOException {
        String[] a = line.split("\\s+");
        if (a.length > 1 && a[1].equals("on")) {
            return Boolean.TRUE;
        } else if (a.length > 1 && a[1].equals("off")) {
            return Boolean.FALSE;
        }
        throw new IOException("Expected boolean");
    }

    private void writeMaterial(JMEMaterial mat) throws IOException {
        System.err.println(mat.name);
        File f = new File(mat.dir, mat.name + ".j3m");
        System.err.println("   " + f);
        PrintWriter pw = new PrintWriter(f);
        try {
            // TODU customise actual mat definition
            pw.println("Material " + mat.name + " :  Common/MatDefs/Misc/Particle.j3md {");
            pw.println("\tMaterialParameters {");
            // TODO texture paths
            pw.println("\t\tTexture : Flip Textures/Effects/" + mat.texture);
            if (mat.pointSprites != null) {
                pw.println("\t\tPointSprite : " + mat.pointSprites);
            }
            pw.println("\t}");
            pw.println("\tAdditionalRenderState {");
            if (mat.pointSprites != null) {
                pw.println("\t\tPointSprite " + (mat.depthWrite ? "On" : "Off"));
            }
            if (mat.depthWrite != null) {
                pw.println("\t\tDepthWrite " + (mat.depthWrite ? "On" : "Off"));
            }
            if (mat.sceneBlend != null) {
                pw.println("\t\tBlend " + mat.sceneBlend);
            }
            pw.println("\t\tFaceCull Off");
            pw.println("\t}");
            pw.println("}");

        } finally {
            pw.close();
        }
    }

    public class JMEMaterial {

        private File dir;
        private String name;
        private Boolean depthWrite;
        private String sceneBlend;
        private String texture;
        private Boolean pointSprites;
    }
}
