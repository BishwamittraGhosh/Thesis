/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSTk;

/**
 *
 * @author vacuum
 */
public class MeetingPoint {

    float posX;
    float posY;
    int id;
    public MeetingPoint(float posX, float posY, int id) {
        this.posX = posX;
        this.posY = posY;
        this.id=id;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

}
