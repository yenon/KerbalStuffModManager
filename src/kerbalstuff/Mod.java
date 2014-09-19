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
public class Mod {
    private ModVersion[] mv;
    private String author,name,shortDescription;
    private int id,followers,downloads,defaultVersionID;
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ModVersion[] getMv() {
        return mv;
    }

    public void setMv(ModVersion[] mv) {
        this.mv = mv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDefaultVersionID() {
        return defaultVersionID;
    }

    public void setDefaultVersionID(int defaultVersionID) {
        this.defaultVersionID = defaultVersionID;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
    
}
