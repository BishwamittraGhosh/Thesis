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
public class ResultGroup {

    final private ArrayList<Member> memberList;
    final private ArrayList<Float> distanceList;
    private float totalDistance;
    private int groupSize;
    private int totalConnectivity;
    double score;
    MeetingPoint mp;
    private ArrayList<Integer> memberIdList;

    
    void showConnection(){
        System.out.println("\n\n\n\n\n");
        for (int i = 0; i < groupSize; i++) {
            Member m = memberList.get(i);
            int c = 0;
            System.out.print(m.getMemberId() + "  : ");
            for (int friend : m.getFriendList()) {

                if (memberIdList.contains(friend)) {
                    System.out.print(friend + " ");
                    c++;
                }

            }
            System.out.println("");
         
        }
    }
    int getMaxDegree() {
        int max = 0;
        for (int i = 0; i < groupSize; i++) {
            Member m = memberList.get(i);
            int c = 0;
//            System.out.print(m.getMemberId() + "  : ");
            for (int friend : m.getFriendList()) {

                if (memberIdList.contains(friend)) {
//                    System.out.print(friend + " ");
                    c++;
                }

            }
//            System.out.println("");
            if (c > max) {
                max = c;
            }
        }
//        System.out.println(max);
        return max;
    }

    public ResultGroup(ArrayList<Member> imemberList,
            ArrayList<Float> idistanceList, float totalDistance,
            int groupSize, int totalConnectivity, double score, MeetingPoint mp) {

        this.totalDistance = totalDistance;
        this.groupSize = groupSize;
        this.totalConnectivity = totalConnectivity;
        this.score = score;
        this.mp = mp;
        this.memberList = new ArrayList<>();
        this.distanceList = new ArrayList<>();
        this.memberIdList = new ArrayList<>();
        for (Member m : imemberList) {
            this.memberList.add(m);
        }
        for (float f : idistanceList) {
            this.distanceList.add(f);
        }
        for (Member m : imemberList) {
            this.memberIdList.add(m.getMemberId());
        }
    }

    public ArrayList<Member> getMemberList() {
        return memberList;
    }

    public ArrayList<Float> getDistanceList() {
        return distanceList;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public int getTotalConnectivity() {
        return totalConnectivity;
    }

    public double getScore() {
        return score;
    }

    public MeetingPoint getMp() {
        return mp;
    }

    public Integer getIntegerScore() {
        //  System.out.println((int)( score * 1000000));

        return (int) (score * 1000000);
    }

    void showMembers() {
        System.out.println("Members are: ");
        for (int i = 0; i < groupSize; i++) {
            System.out.print(memberList.get(i).getMemberId() + " ");
        }
        System.out.println("");
    }

    void showMeetingPoint() {
        System.out.println(mp.id);
    }

    int getMinDegree() {
        int min = groupSize;
        for (int i = 0; i < groupSize; i++) {
            Member m = memberList.get(i);
            int c = 0;
            // System.out.print(m.getMemberId() + "  : ");
            for (int friend : m.getFriendList()) {

                if (memberIdList.contains(friend)) {
                    //     System.out.print(friend + " ");
                    c++;
                }

            }
            //   System.out.println("");
            if (c < min) {
                min = c;
            }
        }
        // System.out.println(min);
        return min;
    }

}
