/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerbalstuff;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;

/**
 *
 * @author Basti
 */
public class ThreadZipAnalyzer extends Thread {

    private Stack<String> createdDirs = new Stack();
    private ThreadDownloadMod tdm;
    private ZipFile zf;
    private final String kspDir, modDir;
    private final FrameLoading fl;
    private final File kspDirFolders[];

    public ThreadZipAnalyzer(ThreadDownloadMod tdm, FrameLoading fl, String path, String modDir, String gamePath) {
        this.tdm = tdm;
        this.kspDir = gamePath;
        kspDirFolders = new File(gamePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory()&&(!pathname.getName().equals("Plugins"));
            }
        });
        this.modDir = modDir;
        this.fl = fl;
        this.fl.setIdle(true);
        this.fl.setDownloadText("Extracting: " + tdm.getModVersion().getName() + "_" + tdm.getModVersion().getFriendlyVersion());
        try {
            zf = new ZipFile(path);
        } catch (IOException ex) {
            Logger.getLogger(ThreadZipAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void extractEntry(ZipFile zipFile, ZipEntry entry, String destDir)
            throws IOException {
        File file = new File(destDir, entry.getName());

        if (entry.isDirectory()) {
            file.mkdirs();
        } else {
            new File(file.getParent()).mkdirs();

            InputStream is = null;
            OutputStream os = null;

            try {
                is = zipFile.getInputStream(entry);
                os = new FileOutputStream(file);
                byte cbuf[] = new byte[16384];
                int len;
                while ((len = is.read(cbuf)) != -1) {
                    os.write(cbuf, 0, len);
                }
            } catch (IOException ex) {

            } finally {
                try {
                    is.close();
                    os.close();
                } catch (NullPointerException ex) {

                }
            }
        }
    }

    private void copyFile(File src, File loc) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(src);
            fos = new FileOutputStream(loc);
            int len;
            byte cbuf[] = new byte[16384];
            while ((len = fis.read(cbuf)) != -1) {
                fos.write(cbuf, 0, len);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ThreadZipAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreadZipAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(ThreadZipAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void copyDir(File src, File loc) {
        if (!new File(loc.getAbsolutePath() + "/" + src.getName()).isDirectory()) {
            createdDirs.add(loc.getAbsolutePath() + "/" + src.getName());
            new File(loc.getAbsolutePath() + "/" + src.getName()).mkdirs();
        }
        int i = 0;
        File in[] = src.listFiles();
        while (i < in.length) {
            if (in[i].isDirectory()) {
                copyDir(in[i], new File(loc.getAbsolutePath() + "/" + in[i].getName()));
            } else {
                copyFile(in[i], new File(loc.getAbsolutePath() + "/" + in[i].getName()));
            }
            i++;
        }
    }

    private File searchForKSPDirs(File dir) {
        Stack<File> s = new Stack();
        File files[];
        int j, i;
        s.add(dir);
        while (!s.empty()) {
            files = s.pop().listFiles();
            i = 0;
            while (i < files.length) {
                if (files[i].isDirectory()) {
                    j = 0;
                    while (j < kspDirFolders.length) {
                        if (kspDirFolders[j].getName().equals(files[i].getName())) {
                            return files[i].getParentFile();
                        }
                        j++;
                    }
                    s.add(files[i]);
                }
                i++;
            }
        }
        return null;
    }

    public void cleanupModuleManager() {
        int i = 0;
        File[] moduleManager = new File(kspDir + "/GameData").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith("ModuleManager") && pathname.getName().endsWith(".dll");
            }
        });
        String versionString[];
        int[] version, maxVersion = {0};
        int j, v1, v2;
        while (i < moduleManager.length) {
            versionString = moduleManager[i].getName().replaceFirst("ModuleManager.", "").replaceFirst(".dll", "").split("\\.");
            System.out.println(moduleManager[i].getName().replaceFirst("ModuleManager.", "").replaceFirst(".dll", ""));
            version = new int[versionString.length];
            j = 0;
            System.out.println(versionString.length);
            while (j < versionString.length) {
                version[j] = Integer.parseInt(versionString[j]);
                j++;
            }
            j = 0;
            boolean done = false;
            while ((j < version.length || j < maxVersion.length) && !done) {
                if (j >= version.length) {
                    v1 = 0;
                } else {
                    v1 = version[j];
                }
                if (j >= maxVersion.length) {
                    v2 = 0;
                } else {
                    v2 = maxVersion[j];
                }
                System.out.println(v1);
                System.out.println(v1);
                if (v2 > v1) {
                    done = true;
                } else {
                    if (v1 > v2) {
                        maxVersion = version;
                        done = true;
                    }
                }
                j++;
            }

            i++;
        }
        j = 0;
        String delMod = "";
        while (j < maxVersion.length) {
            if ("".equals(delMod)) {
                delMod = String.valueOf(maxVersion[j]);
            } else {
                delMod = delMod + "." + maxVersion[j];
            }
            j++;
        }
        delMod = "ModuleManager." + delMod + ".dll";
        System.out.println(delMod);
        j = 0;
        while (j < moduleManager.length) {
            if (!moduleManager[j].getName().equals(delMod)) {
                moduleManager[j].delete();
            }
            j++;
        }
    }

    @Override
    public void run() {

        try {
            int i = 0, len = zf.size();
            //Unnzip file
            Enumeration en = zf.entries();
            ZipEntry entry;
            fl.setIdle(false);
            while (en.hasMoreElements()) {
                entry = (ZipEntry) en.nextElement();
                System.out.println(entry.getName() + ".");
                fl.setStatusbarValue((float) i / (float) len, "File " + i + "/" + len);
                extractEntry(zf, entry, modDir);
                i++;
            }
            zf.close();
            fl.setIdle(true);
            fl.setDownloadText("Analyzing directory structure...");
            File dir = searchForKSPDirs(new File(modDir));
            //Search for Gamefolders
            if (dir != null) {
                //Install the "good" way
                DirectoryFilter df = new DirectoryFilter();
                df.setKSPDirs(kspDirFolders);
                File[] relevantFolders = dir.listFiles(df);
                i = 0;
                fl.setIdle(false);
                fl.setDownloadText("Copying Files");
                while (i < relevantFolders.length) {
                    fl.setStatusbarValue((i+1)/relevantFolders.length,relevantFolders[i].getName());
                    copyDir(relevantFolders[i], new File(kspDir + "/" + relevantFolders[i].getName()));
                    i++;
                }
                cleanupModuleManager();
                tdm.unpackingFinished(createdDirs);
            } else {
                //Show "bad" install dialog
                if (JOptionPane.showConfirmDialog(fl, "Could not find any KSP directories in zip file, try to install everything in gamedata?", "Error!", JOptionPane.ERROR_MESSAGE) == JOptionPane.OK_OPTION) {
                    copyDir(new File(modDir),new File(kspDir+"/GameData"));
                    cleanupModuleManager();
                    tdm.unpackingFinished(createdDirs);
                }else{
                    tdm.clearCFG();
                    tdm.forceFinish();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadZipAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
