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
public class IntermediateSet {

    private ArrayList<Member> memberList;
    private ArrayList<Float> distanceList;
    private float totalDistance;
    private int groupSize;
    private int totalConnectivity;
    private ArrayList<Integer> maxFriend;

    public IntermediateSet() {
        groupSize = 0;
        totalDistance = 0;
        totalConnectivity = 0;
        memberList = new ArrayList<>();
        distanceList = new ArrayList<>();
        maxFriend = new ArrayList<>();
    }

    public int getGroupSize() {
        return groupSize;
    }

    public ArrayList<Integer> getMaxFriend() {
        return maxFriend;
    }

    public IntermediateSet(ArrayList<Member> imemberList, ArrayList<Float> idistanceList, ArrayList<Integer> imaxFriend, float totalDistance, int groupSize, int totalConnectivity) {
        memberList = new ArrayList<>();
        distanceList = new ArrayList<>();
        maxFriend = new ArrayList<>();

        for (Member m : imemberList) {
            this.memberList.add(m);
        }
        for (float f : idistanceList) {
            this.distanceList.add(f);
        }

        for (Integer i : imaxFriend) {
            this.maxFriend.add(i);
        }

        this.totalDistance = totalDistance;
        this.groupSize = groupSize;
        this.totalConnectivity = totalConnectivity;
    }

    public int getTotalConnectivity() {
        return totalConnectivity;
    }

    void popMember(Member member, float distance) {
        memberList.remove(member);
        distanceList.remove(distance);

        groupSize--;
        maxFriend.remove(groupSize);
        totalDistance -= distance;
        for (Member m : memberList) {
            //  System.out.println(m.getMemberId());
            if (member.getFriendList().contains(m.getMemberId())) {
                totalConnectivity -= 2;
                //  System.out.println("kk");
            }
        }

        for (int i = 0; i < groupSize; i++) {
            Member m = memberList.get(i);
            if (member.getFriendList().contains(m.getMemberId())) {
                maxFriend.set(i, maxFriend.get(i) - 1);
            }
        }

        //  System.out.println("total connection: "+totalConnectivity);
    }

    void addMember(Member member, float distance) {
        //System.out.println("adding in vI");
        // System.out.println("total connection: "+totalConnectivity);
        int con = 0;
        for (int i = 0; i < groupSize; i++) {
            Member m = memberList.get(i);
            if (member.getFriendList().contains(m.getMemberId())) {
                con++;
                maxFriend.set(i, maxFriend.get(i) + 1);
            }
        }
        groupSize++;

        totalConnectivity += 2 * con;

//        for (Member m : memberList) {
//            //  System.out.println(m.getMemberId());
//            if (member.getFriendList().contains(m.getMemberId())) {
//                totalConnectivity += 2;
//                //  System.out.println("kk");
//            }
//        }
//        //  System.out.println("total connection: "+totalConnectivity);
        maxFriend.add(con);
        memberList.add(member);
        distanceList.add(distance);
        totalDistance += distance;
    }

    int getSize() {
        return groupSize;
    }

    float getTotalDistance() {
        return totalDistance;
    }

    //problematic ,,,,,,, 
    float nextPossibleConnection(Member member) {
        if (groupSize == 0) {
            return 0;
        }
        int con = 0;
        // System.out.println("existing member number: " + getSize());
        for (Member m : memberList) {
            if (member.getFriendList().contains(m.getMemberId())) {
                con += 2;
            }
        }
        con += totalConnectivity;

        return (float) con / groupSize;
    }

    void showMemberList() {
        System.out.print("intermediate set member list: ");
        for(int i=0;i<groupSize;i++){
            System.out.print(memberList.get(i).getMemberId() + " ");
        }
        System.out.println("");

    }

    int connectionIfAdded(Member member) {
//        int con = 0;
        int allCon = 0;
        // System.out.println("existing member number: " + getSize());
        for (int i = 0; i < groupSize; i++) {
            Member m = memberList.get(i);
            if (member.getFriendList().contains(m.getMemberId())) {
                //if ( member.getFriendList().contains(m.getMemberId())) {
                allCon++;
//                if (maxFriend.get(i) < SSTK.minimumConstraint) {
//                    con++;
//
//                }
            }
        }

        //    System.out.println(con + "   all:  " + allCon);
        return allCon;
    }

    public ArrayList<Member> getMemberList() {
        return memberList;
    }

    public ArrayList<Float> getDistanceList() {
        return distanceList;
    }

}
