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
public class RestSet {

    private ArrayList<Member> memberList;
    private ArrayList<Float> distanceList;
    private ArrayList<Boolean> visited;
    private ArrayList<Integer> memberIdList;
    private int cnt;
    private double totalDistance = 0;
    private double totalDistanceUpTon = 0;

    public RestSet() {
        cnt = 0;
        memberList = new ArrayList<>();
        memberIdList = new ArrayList<>();
        distanceList = new ArrayList<>();
        visited = new ArrayList<>();
    }

    public ArrayList<Float> getDistanceList() {
        return distanceList;
    }

    public ArrayList<Boolean> getVisited() {
        return visited;
    }

    public RestSet(ArrayList<Member> imemberList, ArrayList<Float> idistanceList, ArrayList<Boolean> ivisited, int cnt) {
        memberList = new ArrayList<>();
        distanceList = new ArrayList<>();
        visited = new ArrayList<>();
        memberIdList = new ArrayList<>();

        for (Member m : imemberList) {
            this.memberList.add(m);
            this.memberIdList.add(m.getMemberId());
        }
        for (float f : idistanceList) {
            this.distanceList.add(f);
        }

        for (boolean b : ivisited) {
            this.visited.add(b);
        }

//        this.memberList = memberList;
//        this.distanceList = distanceList;
//        this.visited = visited;
        this.cnt = cnt;
    }

    int getMaxDegree() {
        int max = 0;
        for (int i = 0; i < cnt; i++) {
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

    void addMember(Member member, Float distance) {
        cnt++;

        memberList.add(member);
        distanceList.add(distance);
        visited.add(Boolean.FALSE);
        totalDistance += distance;
        memberIdList.add(member.getMemberId());
        if (cnt <= SSTK.minimumMember) {
            totalDistanceUpTon += distance;
        }
    }

    void popMember(int index) {
        // System.out.println("#########################################");
        // showMemberList();
        cnt--;

//        System.out.println("index:  " + index);
        totalDistance -= distanceList.get(index);
        Member m = memberList.get(index);
        memberIdList.remove((Integer) memberIdList.get(index));
        Float f = distanceList.get(index);
        distanceList.remove((Float) f);
        visited.remove(index);
        memberList.remove(m);

        //System.out.println("after delete ");
        // showMemberList();
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public ArrayList<Member> getMemberList() {
        return memberList;
    }

    double getMemberDistance() {
        return distanceList.get(0);
    }

    boolean isEmpty() {
        if (cnt == 0) {
            return true;
        }
        return false;
    }

    int getSize() {
        //for(int i)
        return cnt;
    }

    void showMemberList() {
//        for (int i = 0; i < cnt; i++) {
//            if (!visited.get(i)) {
//                System.out.print(memberList.get(i).getMemberId() + "  ");
//            }
//        }
//        System.out.println("");
        for (int i = 0; i < cnt; i++) {

            System.out.print(memberList.get(i).getMemberId() + "  ");

        }
        System.out.println("");
        for (int i = 0; i < cnt; i++) {

            System.out.print(memberIdList.get(i) + "  ");

        }
        System.out.println("");
    }

    float getMinimumDistance(int index) {
        float dMin = distanceList.get(index);
        return dMin;
    }

    void markAllUnVisited() {
        for (int i = 0; i < cnt; i++) {
            visited.set(i, Boolean.FALSE);
        }
    }

    void markVisited(int index) {
        visited.set(index, Boolean.TRUE);
    }

    void markUnVisited(int index) {
        visited.set(index, Boolean.FALSE);
    }

    boolean foundUnvisitedVertex() {
        for (boolean b : visited) {
            if (b == false) {
                return true;
            }
        }
        return false;
    }

    Member topMember() {
        int c = 0;
        for (boolean b : visited) {
            if (b == false) {
                return memberList.get(c);
            }
            c++;
        }
        System.err.println("ekhane asbe na, ");
        return memberList.get(0);

    }

    int getLocalMemberIndex(Member m) {
        int index = memberList.indexOf(m);
        return index;
    }

    int getUnvisitedMemberCount() {
        int c = 0;
        System.out.println("visited size " + visited.size());
        for (int i = 0; i < cnt; i++) {
            if (visited.get(i) == false) {
                c++;
            }
        }
        return c;
    }

    public double getTotalDistanceUpTon() {
        return totalDistanceUpTon;
    }

    void pruneUnqualifiedMembers() {
        boolean adjust = false;
//        System.out.println("once");
//        System.out.println("size: " + cnt);
        ArrayList<Integer> deletedIndex = new ArrayList<>();
        for (int i = 0; i < cnt; i++) {
            Member m = memberList.get(i);
            int c = 0;
//            System.out.print(m.getMemberId()+"  : ");
            for (int friend : m.getFriendList()) {

                if (memberIdList.contains(friend)) {
//                    System.out.print(friend+" ");
                    c++;
                }

            }
            if (c < SSTK.minimumConstraint) {
                adjust = true;
                deletedIndex.add(i);
            }
        }
        int d=0;
        for (Integer i : deletedIndex) {
        //    System.out.print(i + "   ");
            popMember(i-d);
            d++;
        }
//        System.out.println("\n\n");
//        getMaxDegree();
        if (adjust) {
            pruneUnqualifiedMembers();
        }

    }

}
