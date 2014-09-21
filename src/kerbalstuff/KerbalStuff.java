/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerbalstuff;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.DefaultListModel;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author Basti
 */
public class KerbalStuff {

    private MainFrame mf;
    private Mod modlist[];

    public KerbalStuff(MainFrame mf, boolean ignoreCertificate) {
        this.mf = mf;
        if (ignoreCertificate) {
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
    }

    private InputStreamReader download(String site) {
        try {
            URL url = new URL(site);
            URLConnection con = url.openConnection();
            return new InputStreamReader(con.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(KerbalStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Mod getMod(int id) {
        return modlist[id];
    }

    public void searchMod(String mod) {
        try {
            long starttimefetch = System.currentTimeMillis();
            ScriptEngine se = new ScriptEngineManager().getEngineByName("JavaScript");
            ModVersion mv[];
            JSObject version, current, main = (JSObject) se.eval(download("https://www.kerbalstuff.com/api/search/mod?query=" + URLEncoder.encode(mod, "UTF-8")));
            System.out.println(System.currentTimeMillis() - starttimefetch);
            long starttimeparse = System.currentTimeMillis();
            int ilen = main.values().size(), i = 0, j, jlen;
            modlist = new Mod[ilen];
            DefaultListModel lm = new DefaultListModel();
            while (i < ilen) {
                current = (JSObject) main.getSlot(i);
                modlist[i] = new Mod();
                modlist[i].setAuthor((String) current.getMember("author"));
                modlist[i].setDefaultVersionID((int) current.getMember("default_version_id"));
                modlist[i].setDownloads((int) current.getMember("downloads"));
                modlist[i].setFollowers((int) current.getMember("followers"));
                modlist[i].setID((int) current.getMember("id"));
                modlist[i].setName((String) current.getMember("name"));
                modlist[i].setShortDescription((String) current.getMember("short_description"));
                current = (JSObject) current.getMember("versions");
                jlen = (int) current.values().size();
                j = 0;
                mv = new ModVersion[jlen];
                while (j < jlen) {
                    version = (JSObject) current.getSlot(j);
                    mv[j] = new ModVersion();
                    mv[j].setChangelog((String) version.getMember("changelog"));
                    mv[j].setDownloadPath("https://kerbalstuff.com" + (String) version.getMember("download_path"));
                    mv[j].setFriendlyVersion((String) version.getMember("friendly_version"));
                    mv[j].setID((int) version.getMember("id"));
                    mv[j].setKspVersion((String) version.getMember("ksp_version"));
                    mv[j].setName(modlist[i].getName());
                    j++;
                }
                modlist[i].setMv(mv);
                lm.add(i, modlist[i].getName());
                mf.setResults(lm);
                System.out.println(i);
                i++;
            }
            System.out.println(System.currentTimeMillis() - starttimeparse);
            System.out.println("ok");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KerbalStuff.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(KerbalStuff.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
