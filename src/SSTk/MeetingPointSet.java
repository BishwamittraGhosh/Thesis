/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSTk;

import java.util.ArrayList;

/**
 *
 * @author vacuum
 */
public class MeetingPointSet {

    
    private ArrayList<MeetingPoint> list = new ArrayList<>();
    private int cnt;

    public MeetingPointSet() {
        cnt = 0;
    }

    void addMeetingPOint(float posX, float posY) {
        list.add(new MeetingPoint(posX, posY,cnt));
        cnt++;
    }

    void showMeetingPoints() {
        for (MeetingPoint mp : list) {
            System.out.println(mp.posX + "  " + mp.posY);
        }
    }

    int getMeetingPointSetSize() {
        return cnt;
    }

    public ArrayList<MeetingPoint> getList() {
        return list;
    }

}
