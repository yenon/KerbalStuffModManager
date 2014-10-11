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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author Basti
 */
public class ThreadDownloadMod extends Thread {

    private MainFrame mf;
    private ModVersion mv;
    private FrameLoading fdm;
    private String appDir, kspDir;
    private boolean end;

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
                    if (dirOut == "") {
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

    public ThreadDownloadMod(MainFrame mf, FrameLoading fdm, ModVersion mv, String appDir, String kspDir, boolean end) {
        this.mf = mf;
        this.mv = mv;
        this.appDir = appDir;
        this.kspDir = kspDir;
        this.fdm = fdm;
        this.end = end;
        this.fdm.setDownloadText("Downloading: " + mv.getName() + " " + mv.getFriendlyVersion());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (KeyManagementException ex) {
            Logger.getLogger(KerbalStuff.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(KerbalStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
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
