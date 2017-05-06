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
public class Member {

    private float posX;
    private float posY;
    private ArrayList<Integer> friendList = new ArrayList<>();
    private int memberId;

    public ArrayList<Integer> getFriendList() {
        return friendList;
    }

    public int getMemberId() {
        return memberId;
    }

    public Member(float posX, float posY,int memberId) {
        this.posX = posX;
        this.posY = posY;
        this.memberId=memberId+1;
    }

    void addFriend(int friend) {
        friendList.add(friend+1);
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
    
    
    void showFriendList(){
        System.out.print(memberId+"  : ");
        for(Integer i:friendList){
            System.out.print(i+"  ");
        }
        System.out.println("");
    }
}
