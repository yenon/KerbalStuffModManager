/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerbalstuff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Basti
 */
public class ModAutoUpdater extends Thread{
    private FrameLoading fl;
    private String path;
    public ModAutoUpdater(FrameLoading fl,String path){
        this.fl=fl;
        this.path=path;
    }
    @Override
    public void run(){
        int i=0;
        File files[] = new File(path+"/modscfg").listFiles();
        while(i<files.length){
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(files[i].getAbsoluteFile()));
                Mod m = KerbalStuff.findModById(Integer.parseInt(p.getProperty("id")));
                if(Utilities.compareVersions(new String[]{m.getMv()[0].getFriendlyVersion(),p.getProperty("")})==0){
                    
                }
            } catch (IOException ex) {
                Logger.getLogger(ModAutoUpdater.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
    }
}