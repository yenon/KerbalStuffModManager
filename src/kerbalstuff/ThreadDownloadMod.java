/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerbalstuff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Basti
 */
public class ThreadDownloadMod extends Thread {

    private final MainFrame mf;
    private final ModVersion mv;
    private final FrameLoading fdm;
    private final String appDir, kspDir;
    private final boolean end;

    public void unpackingFinished(Stack<String> dirs) {
        if (end) {
            try {
                Properties modProps = new Properties();
                if (new File(appDir + "/modscfg/" + mv.getName() + ".cfg").isFile()) {
                    FileInputStream fis = new FileInputStream(new File(appDir + "/modscfg/" + mv.getName() + ".cfg"));
                    modProps.load(fis);
                    fis.close();
                }
                String dirOut = "";
                while (!dirs.empty()) {
                    if ("".equals(dirOut)) {
                        dirOut = dirs.pop();
                    } else {
                        dirOut = dirOut + "\n" + dirs.pop();
                    }
                }
                System.out.println(dirOut);
                modProps.setProperty("dirs", dirOut);

                FileOutputStream fos = new FileOutputStream(new File(appDir + "/modscfg/" + mv.getName() + ".cfg"));
                modProps.store(fos, null);
                fos.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ThreadDownloadMod.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ThreadDownloadMod.class.getName()).log(Level.SEVERE, null, ex);
            }
            mf.modDownloadFinished();
        }
    }
    
    public void forceFinish(){
        mf.modDownloadFinished();
    }
    
    public void clearCFG(){
        new File(appDir + "/modscfg/" + mv.getName() + ".cfg").delete();
    }
    
    public ModVersion getModVersion() {
        return mv;
    }

    public ThreadDownloadMod(MainFrame mf, FrameLoading fdm, ModVersion mv,boolean end) {
        this.mf = mf;
        this.mv = mv;
        this.appDir = mf.getAppDir();
        this.kspDir = mf.getKspDir();
        this.fdm = fdm;
        this.end = end;
        this.fdm.setDownloadText("Downloading: " + mv.getName() + " " + mv.getFriendlyVersion());
    }

    @Override
    public void run() {
        try {
            if (!new File(appDir + "/temp/" + mv.getName() + "_" + mv.getFriendlyVersion() + ".zip").isFile()) {
                InputStream isr = new URL(mv.getDownloadPath()).openStream();
                FileOutputStream fos = new FileOutputStream(appDir + "/temp/" + mv.getName() + "_" + mv.getFriendlyVersion() + ".zip");
                byte cbuf[] = new byte[16384];
                int i;
                long len, dlen = 0;
                len = new URL(mv.getDownloadPath()).openConnection().getContentLengthLong();
                fdm.setIdle(false);
                while ((i = isr.read(cbuf)) != -1) {
                    fos.write(cbuf, 0, i);
                    dlen = dlen + i;
                    fdm.setStatusbarValue((float) dlen / (float) len, dlen / 1048576 + " Mb/" + len / 1048576 + " Mb");
                }
                isr.close();
                fos.close();
            }
            ThreadZipAnalyzer tzfa = new ThreadZipAnalyzer(this, fdm, appDir + "/temp/" + mv.getName() + "_" + mv.getFriendlyVersion() + ".zip", appDir + "/mods/" + mv.getName() + "_" + mv.getFriendlyVersion(), kspDir);
            tzfa.start();

        } catch (MalformedURLException ex) {
            Logger.getLogger(ThreadDownloadMod.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreadDownloadMod.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
