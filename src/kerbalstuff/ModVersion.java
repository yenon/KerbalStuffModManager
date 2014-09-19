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
public class ModVersion {

    private String name, changelog, downloadPath, friendlyVersion, kspVersion;
    private int id;

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getFriendlyVersion() {
        return friendlyVersion;
    }

    public void setFriendlyVersion(String friendlyVersion) {
        this.friendlyVersion = friendlyVersion;
    }

    public String getKspVersion() {
        return kspVersion;
    }

    public void setKspVersion(String kspVersion) {
        this.kspVersion = kspVersion;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
