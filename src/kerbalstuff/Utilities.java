/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kerbalstuff;

/**
 *
 * @author Basti
 */
public class Utilities {
    public static int compareVersions(String[] versions){
        int[][] versionNumbers = new int[versions.length][1];
        String[] versionSplitted;
        String version,versionConverted;
        int i=0,j,max=0,a,b;
        boolean end;
        char c;
        while(i<versions.length){
            version=versions[i];
            versionConverted="";
            j=0;
            while(j<version.length()){
                c=version.charAt(j);
                if((c>47 && c<58) || c==46){
                    versionConverted=versionConverted+c;
                }
                j++;
            }
            System.out.println(versionConverted);
            versionSplitted=versionConverted.split("\\.");
            versionNumbers[i]=new int[versionSplitted.length];
            j=0;
            while(j<versionSplitted.length){
                versionNumbers[i][j]=Integer.parseInt(versionSplitted[j]);
                j++;
            }
            j=0;
            end=false;
            while((j<versionNumbers[i].length||j<versionNumbers[max].length)&&!end){
                if(j<versionNumbers[i].length){
                    a=versionNumbers[i][j];
                }else{
                    a=-1;
                }
                if(j<versionNumbers[max].length){
                    b=versionNumbers[max][j];
                }else{
                    b=-1;
                }
                if(a<b){
                    end=true;
                }else{
                    if(a>b){
                        max=i;
                        end=true;
                    }
                }
                j++;
            }
            i++;
        }
        return max;
    }
}
