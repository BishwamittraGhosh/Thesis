/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSTk;

import java.util.ArrayList;

/**
 *
 * @author Vacuum Tube
 */
public class MemberSet {
    private ArrayList<Member> list=new ArrayList<>();
    private int cnt;
    public MemberSet() {
        cnt=0;
    }
    
    void addMember(float posX,float posY, int memberId){
        list.add(new Member(posX,posY, memberId));
        cnt++;
    }

    public ArrayList<Member> getList() {
        return list;
    }
    
    void showMembers(){
        for(Member mp: list){
            System.out.println(mp.getPosX()+"  "+mp.getPosY());
        }
    }
    
    
    void addConnection(int id1,int id2){
        list.get(id1).addFriend(id2);
        list.get(id2).addFriend(id1);
    }
    
    int getMemberSetSize(){
        return cnt;
    }
    
    
    void showGraphConnection(){
        for(Member m : list){
            m.showFriendList();
        }
    }
    
    
    
}
