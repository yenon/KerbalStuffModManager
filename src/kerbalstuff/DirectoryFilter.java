/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kerbalstuff;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author Basti
 */
public class DirectoryFilter implements FileFilter {

    private File[] ksp;

    public void setKSPDirs(File[] ksp) {
        this.ksp = ksp;
    }

    @Override
    public boolean accept(File pathname) {
        boolean isKSPDir = false;
        int i = 0;
        System.out.println(pathname.getName());
        while (!isKSPDir && i < ksp.length) {
            isKSPDir = ksp[i].getName().equals(pathname.getName());
            i++;
        }
        return isKSPDir;
    }
}
