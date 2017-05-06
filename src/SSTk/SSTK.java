/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSTk;

import gnu.trove.procedure.TIntProcedure;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

/**
 *
 * @author vacuum
 */
public class SSTK {

    /**
     * @param args the command line arguments
     *
     *
     */
    static int minimumConstraint = 1;
    static int minimumMember = 4;
    static ArrayList<ResultGroup> resultLists = new ArrayList<>();
    static int groupFound = 0;
    static int topK = 4;
    private static long start;
    private static int totalCalled = 0;

    private static class AssistantClass {

        RestSet restSet;
        double minDistance;
        MeetingPoint mp;
        double avgDistance;
        double avgDistanceUpToN;

        public AssistantClass(RestSet restSet, double minDistance, MeetingPoint mp, double avgDistance, double avgDistanceUpToN) {
            this.restSet = restSet;
            this.minDistance = minDistance;
            this.mp = mp;
            this.avgDistance = avgDistance;
            this.avgDistanceUpToN = avgDistanceUpToN;
        }

        Integer getminDistance() {
            return (int) (minDistance * 1000);
        }

        Integer getavgDistance() {
            return (int) (avgDistance * 1000);
        }

        Integer getavgDistanceUpToN() {
            return (int) (avgDistanceUpToN * 1000);
        }

    }

    public static void main(String[] args) {

        MemberSet memberSet = new MemberSet();
        MeetingPointSet mPointSet = new MeetingPointSet();
//        for synthetic data

        insertMember(memberSet);
        insertGraphConnection(memberSet);
        insertMeetingPoints(mPointSet);

//        for practical data
//        insertBrightKiteMeetingPoints(mPointSet);
//        insertMember(memberSet);
//        insertBrightKiteGraphConnection(memberSet);
        makeResult(memberSet, mPointSet);
    }

    private static void makeResult(MemberSet memberSet, MeetingPointSet mPointSet) {

        int it = mPointSet.getMeetingPointSetSize();
        start = System.currentTimeMillis();
        ArrayList<AssistantClass> as = new ArrayList<>();
        for (int q = 0; q < it; q++) {
            SpatialIndex si = new RTree();
            si.init(null);
            int cnt = 0;
            ArrayList<Rectangle> rects = new ArrayList<>();
            for (Member m : memberSet.getList()) {
                Rectangle r = new Rectangle(m.getPosX(), m.getPosY(), m.getPosX(), m.getPosY());
                rects.add(r);
                si.add(r, cnt);
                cnt++;
            }
            RestSet vRest = new RestSet();

            final Point p = new Point(mPointSet.getList().get(q).getPosX(), mPointSet.getList().get(q).getPosY());

            for (int j = 0; j < 10; j++) {
                si.nearest(p, new TIntProcedure() {
                    public boolean execute(int i) {
                        //                  System.out.println("Rectangle " + i + " " + rects.get(i) + ", distance=" + rects.get(i).distance(p));
                        si.delete(rects.get(i), i);
                        vRest.addMember(memberSet.getList().get(i), rects.get(i).distance(p));

                        return true;
                    }
                }, (float) 10);

            }
            vRest.pruneUnqualifiedMembers();
            if (vRest.getSize() != 0) {
                as.add(new AssistantClass(vRest, vRest.getMinimumDistance(0), mPointSet.getList().get(q),
                        (double) vRest.getTotalDistance() / vRest.getSize(), (double) vRest.getTotalDistanceUpTon() / minimumMember));

            }

//            System.out.println(vRest.getSize());
        }
        as.sort((a, b) -> a.getminDistance().compareTo(b.getminDistance()));
        it = as.size();
        int c = 0;
        for (int i = 0; i < it; i++) {
            AssistantClass ac = as.get(i);
            IntermediateSet vIntermediateSet = new IntermediateSet();
            int l = ac.restSet.getMaxDegree();
//            System.out.println(ac.restSet.getSize());
//            findGroupBaseline(ac.restSet, vIntermediateSet, 0, ac.mp);
//            System.out.println(groupFound);
            findGroup(ac.restSet, vIntermediateSet, 0, ac.mp, l, false);
            c += totalCalled;
            totalCalled = 0;

        }
        long now = System.currentTimeMillis();
        for (ResultGroup r : resultLists) {

            System.out.println(r.score);
            r.showMembers();
        }

        PrintWriter pw;

        try {
            File f = new File("result.csv");
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(SSTK.class.getName()).log(Level.SEVERE, null, ex);
            }
            pw = new PrintWriter(f);
            for (ResultGroup r : resultLists) {

//                System.out.println(r.score);
                StringBuilder sb = new StringBuilder();

                sb.append(r.score * 10000000);
                sb.append(',');

                sb.append(r.getGroupSize());
                sb.append(',');
                for (int i = 0; i < r.getGroupSize(); i++) {
                    sb.append(r.getMemberList().get(i).getMemberId() + " ");

                }

                sb.append('\n');

                pw.write(sb.toString());

            }

//            System.out.println("time    " + (now - start));
//            System.out.println("\n\ntotal iteration: " + c);
            StringBuilder sb = new StringBuilder();
            sb.append(now - start);
            sb.append('\n');
            sb.append(c);
            sb.append('\n');
            pw.write(sb.toString());
            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSTK.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(c);
        System.out.println(now - start);

        System.out.println(adP);

    }
    static int adP = 0;

    private static boolean findGroup(RestSet vRestSet, IntermediateSet vIntermediateSet,
            int counter, MeetingPoint mp, int maxDegree, boolean parentIncludedInResult) {
        totalCalled++;
        Score s = new Score(vIntermediateSet);
        int size = vRestSet.getSize() + vIntermediateSet.getSize();
        while (size >= minimumMember && !vRestSet.isEmpty()) {
            boolean resultUpdated = false;

            Member m = null;
            float dMin = 100;
            int index;
            if (vRestSet.foundUnvisitedVertex()) {
                m = vRestSet.topMember();
                index = vRestSet.getLocalMemberIndex(m);
                vRestSet.markVisited(index);
                dMin = vRestSet.getMinimumDistance(index);
                System.out.println("member id: " + m.getMemberId() + "  distance : " + dMin + "           index:    " + index);

            } else {
                return false;
            }

            if (vIntermediateSet.getSize() < minimumMember) {

                double newCon = vIntermediateSet.connectionIfAdded(m);
//                double rightSide = (double) (minimumConstraint * vIntermediateSet.getSize()) / (minimumMember - 1.0);

                double rightSide = Math.floor((double) (minimumConstraint * vIntermediateSet.getSize()) / (minimumMember - 1.0));
                if (newCon >= rightSide) {

                    if (resultLists.size() == topK) {
                        ResultGroup r = resultLists.get(topK - 1);
                        double pruningDistance = s.advancePruningDistance(r.getTotalConnectivity(),
                                r.getGroupSize(), r.getTotalDistance());
                        if (pruningDistance < (double) dMin * (minimumMember - vIntermediateSet.getSize())) {
                            System.out.println("\n\n\n\ntermination advance");
                            return false;
                        }
                    }
                    vIntermediateSet.addMember(m, dMin);
                    vRestSet.popMember(index);

                    RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
                            vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
                    IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
                            vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
                            vIntermediateSet.getTotalDistance(),
                            vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());
                    System.out.println("before");
                    vIntermediateSet.showMemberList();
                    boolean updated = false;
                    if (vIntermediateSet.getSize() == minimumMember) {
                        ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
                                vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
                                vIntermediateSet.getSize(),
                                vIntermediateSet.getTotalConnectivity(), s.score(), mp);
                        int minCon = resultGroup.getMinDegree();
                        if (minCon < minimumConstraint) {
                            vIntermediateSet.popMember(m, dMin);

//                            System.out.println("\n\n\n\n\n ekhane\n\n\n\n");
                            //    resultGroup.getMaxDegree();
                            continue;
                        }
                        if (groupFound < topK) {
                            groupFound++;
                            updated = true;
                            resultLists.add(resultGroup);
                            Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                            resultGroup.getMaxDegree();

//                            resultGroup.showConnection();
                        } else {
                            ResultGroup r = resultLists.get(topK - 1);
                            if (r.score < resultGroup.score) {
                                updated = true;
                                resultLists.remove(topK - 1);
                                resultLists.add(resultGroup);
                                Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                                resultGroup.getMaxDegree();
//                                resultGroup.showConnection();
                            }

                        }

                    }

                    boolean b = findGroup(newRestSet, newIntermediateSet, counter + 1, mp, maxDegree, updated);
                    //System.out.println("counter "+counter);

                    vIntermediateSet.popMember(m, dMin);
                    System.out.println("after");
                    vIntermediateSet.showMemberList();

                }
                else{
                    System.out.println("bad");
                }
            } else {

                //FOR PREVIOUS GROUP
                int newCon = vIntermediateSet.connectionIfAdded(m);
                System.out.println("over");
                if (newCon >= minimumConstraint) {

                    //NO CHECKING FOR TERMINATION......
                    if (groupFound < topK) {
//                        System.out.println("\nmember kom\n");
                        vIntermediateSet.addMember(m, dMin);
                        vRestSet.popMember(index);

                        RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
                                vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
                        IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
                                vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
                                vIntermediateSet.getTotalDistance(),
                                vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());
                        System.out.println("before");
                        vIntermediateSet.showMemberList();

                        ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
                                vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
                                vIntermediateSet.getSize(),
                                vIntermediateSet.getTotalConnectivity(), s.score(), mp);

                        groupFound++;
                        resultLists.add(resultGroup);
                        Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                        resultGroup.getMaxDegree();

                        findGroup(newRestSet, newIntermediateSet, counter + 1, mp, maxDegree, true);
                        //System.out.println("counter "+counter);

                        vIntermediateSet.popMember(m, dMin);
                        System.out.println("after");
                        vIntermediateSet.showMemberList();
                        continue;
                    }

                    if (parentIncludedInResult) {
//                        System.out.println("\n baap included hoise\n");
                        vIntermediateSet.addMember(m, dMin);
                        vRestSet.popMember(index);

                        RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
                                vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
                        IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
                                vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
                                vIntermediateSet.getTotalDistance(),
                                vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());
                        System.out.println("before");
                        vIntermediateSet.showMemberList();

                        ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
                                vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
                                vIntermediateSet.getSize(),
                                vIntermediateSet.getTotalConnectivity(), s.score(), mp);
                        ResultGroup r = resultLists.get(topK - 1);
                        boolean updated = false;
                        if (r.score < resultGroup.score) {
                            resultLists.remove(topK - 1);
                            updated = true;
                            resultLists.add(resultGroup);
                            Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                            resultGroup.getMaxDegree();
                        }

                        findGroup(newRestSet, newIntermediateSet, counter + 1, mp, maxDegree, updated);

                        vIntermediateSet.popMember(m, dMin);
                        System.out.println("after");
                        vIntermediateSet.showMemberList();

                    } else {

//                        System.out.println("\n baap included hoini **************\n");
                        double terminatingDistance = s.getDistanceTermination(maxDegree);
//                        System.out.println("terminating distance: " + terminatingDistance);

                        if (dMin > terminatingDistance) {
                            System.out.println("termination due to distance upper bound");
                            return false;
                        }
//                        System.out.println("upper distance for member: " + s.getUpperDistanceForMember());
                        if (dMin < s.getUpperDistanceForMember()) {
//                            System.out.println("distance lemma");

                            vIntermediateSet.addMember(m, dMin);
                            vRestSet.popMember(index);

                            RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
                                    vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
                            IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
                                    vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
                                    vIntermediateSet.getTotalDistance(),
                                    vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());
                            System.out.println("before");
                            vIntermediateSet.showMemberList();

                            ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
                                    vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
                                    vIntermediateSet.getSize(),
                                    vIntermediateSet.getTotalConnectivity(), s.score(), mp);
                            ResultGroup r = resultLists.get(topK - 1);
                            boolean updated = false;
                            if (r.score < resultGroup.score) {
                                resultLists.remove(topK - 1);
                                updated = true;
                                resultLists.add(resultGroup);
                                Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                                resultGroup.getMaxDegree();
                            }

                            findGroup(newRestSet, newIntermediateSet, counter + 1, mp, maxDegree, updated);

                            vIntermediateSet.popMember(m, dMin);
                            System.out.println("after");
                            vIntermediateSet.showMemberList();
                            continue;
                        }

                        double conLower = s.getLowerBoundOnConnection(dMin);
//                        System.out.println("connection lower for user: " + conUpper);

                        if ((float) 2 * newCon >= conLower) {
//                            System.out.println("connection Lemma");

                            vIntermediateSet.addMember(m, dMin);
                            vRestSet.popMember(index);
                            RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
                                    vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
                            IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
                                    vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
                                    vIntermediateSet.getTotalDistance(),
                                    vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());
                            System.out.println("before");
                            vIntermediateSet.showMemberList();

                            ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
                                    vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
                                    vIntermediateSet.getSize(),
                                    vIntermediateSet.getTotalConnectivity(), s.score(), mp);
                            ResultGroup r = resultLists.get(topK - 1);
                            boolean updated = false;
                            if (r.score < resultGroup.score) {
                                resultLists.remove(topK - 1);
                                updated = true;
                                resultLists.add(resultGroup);
                                Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
                                //                  resultGroup.getMaxDegree();
                            }

                            findGroup(newRestSet, newIntermediateSet, counter + 1, mp, maxDegree, updated);

                            vIntermediateSet.popMember(m, dMin);
                            System.out.println("after");
                            vIntermediateSet.showMemberList();

                        } else {
//                            System.out.println("nothing");

                            //JUST CHECKING IF ANY PRUNING CAN BE DONE
                            int maxDeg = vRestSet.getMaxDegree();
//                            System.out.println((double) 2.0 * maxDeg + "  <  " + conUpper);

                            if ((double) 2.0 * maxDeg < conLower) {
                                adP++;
                                System.out.println("termination due to connection loweer bound");

                                return false;
                            }
                        }

                    }

                }
                System.out.println("excluded for connection");

            }
        }System.out.println("amni return");
        return false;

    }

    static void insertMeetingPoints(MeetingPointSet mps) {
        String csvFile = "meetingPoints.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] strings = line.split(cvsSplitBy);
                mps.addMeetingPOint(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        mps.showMeetingPoints();
    }

    private static void insertMember(MemberSet memberSet) {
        String csvFile = "member.csv";
//        String csvFile = "E:/_thesis/Data/Brightkite/Brightkite_location.csv";

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int cnt = 0;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] strings = line.split(cvsSplitBy);
                memberSet.addMember(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), cnt);
                cnt++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        memberSet.showMembers();
//        System.out.println(memberSet.getMemberSetSize());
    }

    private static void insertGraphConnection(MemberSet memberSet) {
        String csvFile = "connection.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        int cnt = 0;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] strings = line.split(cvsSplitBy);
                for (String s : strings) {
                    int cnt2 = Integer.parseInt(s) - 1;
                    memberSet.addConnection(cnt, cnt2);
                }
                cnt++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        memberSet.showGraphConnection();
    }

    private static boolean findGroupBaseline(RestSet vRestSet, IntermediateSet vIntermediateSet, int counter, MeetingPoint mp) {
        // System.out.println(mp.id);
        totalCalled++;
//        System.out.println(counter);
        Score s = new Score(vIntermediateSet);
        boolean resultUpdated = false;
        int size = vRestSet.getSize() + vIntermediateSet.getSize();
        while (size >= minimumMember && !vRestSet.isEmpty()) {
//            System.out.println("bal");
            Member m = null;
            float dMin = 100;
            int index;
            if (vRestSet.foundUnvisitedVertex()) {
                m = vRestSet.topMember();
                index = vRestSet.getLocalMemberIndex(m);
                vRestSet.markVisited(index);
                dMin = vRestSet.getMinimumDistance(index);

            } else {
                return false;
            }

//            if (vIntermediateSet.getSize() < minimumMember) {
            vIntermediateSet.addMember(m, dMin);
            vRestSet.popMember(index);

            RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
                    vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
            IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
                    vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
                    vIntermediateSet.getTotalDistance(),
                    vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());

            if (vIntermediateSet.getSize() >= minimumMember) {

                ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
                        vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
                        vIntermediateSet.getSize(),
                        vIntermediateSet.getTotalConnectivity(), s.score(), mp);
                int minCon = resultGroup.getMinDegree();
                if (minCon < minimumConstraint) {
                    vIntermediateSet.popMember(m, dMin);

                    continue;
                }
                if (groupFound < topK) {
//                        System.out.println("never");
                    groupFound++;
                    resultLists.add(resultGroup);
                    Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
                    resultGroup.getMaxDegree();
                } else {
                    ResultGroup r = resultLists.get(topK - 1);
                    if (r.score < resultGroup.score) {
                        resultLists.remove(topK - 1);
                        resultLists.add(resultGroup);
                        Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
                        resultGroup.getMaxDegree();
                    }

                }

            }

            boolean b = findGroupBaseline(newRestSet, newIntermediateSet, counter + 1, mp);

            vIntermediateSet.popMember(m, dMin);
//                System.out.println("after");
//                vIntermediateSet.showMemberList();

//            } else {
//
//                vIntermediateSet.addMember(m, dMin);
//                vRestSet.popMember(index);
//
//                ResultGroup resultGroup = new ResultGroup(vIntermediateSet.getMemberList(),
//                        vIntermediateSet.getDistanceList(), vIntermediateSet.getTotalDistance(),
//                        vIntermediateSet.getSize(),
//                        vIntermediateSet.getTotalConnectivity(), s.score(), mp);
//                int minCon = resultGroup.getMinDegree();
//                if (minCon < minimumConstraint) {
////                    System.out.println("false");
//                    vIntermediateSet.popMember(m, dMin);
//
//                    continue;
//                }
////                System.out.println("before");
////                vIntermediateSet.showMemberList();
////                System.out.println(s.score());
//
//                if (groupFound < topK) {
//                    groupFound++;
//                    resultLists.add(resultGroup);
//                    Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                    //    resultGroup.getMaxDegree();
////                    System.out.println("upadted in empty group");
////                    System.out.println(s.score());
//                } else {
//                    ResultGroup rr = resultLists.get(topK - 1);
//                    if (rr.score < s.score()) {
//                        resultLists.remove(topK - 1);
//                        resultLists.add(resultGroup);
//                        Collections.sort(resultLists, (a, b) -> b.getIntegerScore().compareTo(a.getIntegerScore()));
//                        //      resultGroup.getMaxDegree();
////                        System.out.println("upadted in existing group");
////                        System.out.println(s.score());
//                    }
//
//                }
//                RestSet newRestSet = new RestSet(vRestSet.getMemberList(),
//                        vRestSet.getDistanceList(), vRestSet.getVisited(), vRestSet.getSize());
//                IntermediateSet newIntermediateSet = new IntermediateSet(vIntermediateSet.getMemberList(),
//                        vIntermediateSet.getDistanceList(), vIntermediateSet.getMaxFriend(),
//                        vIntermediateSet.getTotalDistance(),
//                        vIntermediateSet.getGroupSize(), vIntermediateSet.getTotalConnectivity());
//
//                findGroupBaseline(newRestSet, newIntermediateSet, counter + 1, mp);
//                //System.out.println("counter "+counter);
//
//                vIntermediateSet.popMember(m, dMin);
////                System.out.println("after");
////                vIntermediateSet.showMemberList();
//
//            }
        }
//        System.out.println("no unvisited node found");
        return false;

    }

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Entry<T, Integer> max = null;

        for (Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue()) {
                max = e;
            }
        }

        return max.getKey();
    }

    private static class Location {

        float longitude;
        float latitude;

        public Location(float longitude, float latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }

        public void show() {
            System.out.println(longitude + "  " + latitude);
        }

        public float getLongitude() {
            return longitude;
        }

        public float getLatitude() {
            return latitude;
        }

    }

    static ArrayList<Integer> vertexNoLocation = new ArrayList<>();

    private static void insertBrightKiteMember(MemberSet memberSet) {

        List<Location> list = new ArrayList<>();

        String csvFile = "E:/_thesis/Data/Brightkite/Brightkite_totalCheckins.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\s+";
        int cnt = 0;
        boolean newMember = false;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] strings = line.split(cvsSplitBy);
//                memberSet.addMember(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), cnt);
//                if (cnt > 2) {
//                    break;
//                }
                int l = Integer.parseInt(strings[0]);
                if (l > cnt) {
//                        mostCommon(list).show();
                    Location location = mostCommon(list);

                    list.clear();

                    for (int i = 0; i < l - cnt; i++) {
                        memberSet.addMember(location.getLongitude(), location.getLatitude(), cnt + i);

                    }
                    cnt = l;

                }

                try {
                    if (strings.length >= 3) {
                        list.add(new Location(Float.parseFloat(strings[2]), Float.parseFloat(strings[3])));

                    } else {
                        System.out.println(cnt);
                        for (String s : strings) {
                            System.out.println(s);
                        }
                        System.out.println("");
                    }

                } catch (Exception e) {
                    for (String s : strings) {
                        System.err.println(s);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Location location = mostCommon(list);

        memberSet.addMember(location.getLongitude(), location.getLatitude(), cnt);

        System.out.println(vertexNoLocation.size());
        System.out.println(memberSet.getMemberSetSize());

        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("E:/_thesis/Data/Brightkite/Brightkite_location.csv"));
            for (Member m : memberSet.getList()) {
                StringBuilder sb = new StringBuilder();

                sb.append(m.getPosX());
                sb.append(',');
                sb.append(m.getPosY());
                sb.append(',');
                sb.append(m.getMemberId());

                sb.append('\n');

                pw.write(sb.toString());

            }
            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSTK.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pw = new PrintWriter(new File("E:/_thesis/Data/Brightkite/vertexNoLocation.csv"));
            for (Integer i : vertexNoLocation) {
                StringBuilder sb = new StringBuilder();

                sb.append(i);
                sb.append('\n');

                pw.write(sb.toString());

            }
            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSTK.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("done!");
    }

    private static void insertBrightKiteGraphConnection(MemberSet memberSet) {
        String csvFile = "E:/_thesis/Data/Brightkite/Brightkite_edges.txt";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = "\\s+";
        int cnt = 0;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                String[] strings = line.split(cvsSplitBy);
                memberSet.addConnection(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
                cnt++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        PrintWriter pw;
        try {
            pw = new PrintWriter(new File("E:/_thesis/Data/Brightkite/Brightkite_location.csv"));
            for (Member m : memberSet.getList()) {
                StringBuilder sb = new StringBuilder();

                sb.append(m.getPosX());
                sb.append(',');
                sb.append(m.getPosY());
                sb.append(',');
                sb.append(m.getMemberId());

                sb.append('\n');

                pw.write(sb.toString());

            }
            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSTK.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pw = new PrintWriter(new File("E:/_thesis/Data/Brightkite/brightKiteCOnnection.csv"));
            for (Member m : memberSet.getList()) {
                StringBuilder sb = new StringBuilder();

                int l = m.getFriendList().size();
                for (int i = 0; i < l; i++) {
                    sb.append(m.getFriendList().get(i));
                    if (i == l - 1) {
                        break;
                    }
                    sb.append(',');
                }

                sb.append("\n");
                pw.write(sb.toString());

            }

            pw.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(SSTK.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.out.println("done!");
//        memberSet.showGraphConnection();
    }

    static void insertBrightKiteMeetingPoints(MeetingPointSet mps) {
        String csvFile = "E:/_thesis/Data/Brightkite/Brightkite_location.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        float maxLongitude = -1000;
        float minLongitude = 1000;
        float maxLatitude = -1000;
        float minLatitude = 1000;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] strings = line.split(cvsSplitBy);
//                mps.addMeetingPOint(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]));
                float latitude = Float.parseFloat(strings[0]);
                float longitude = Float.parseFloat(strings[1]);
                if (latitude > maxLatitude) {
                    maxLatitude = latitude;
                }
                if (latitude < minLatitude) {
                    minLatitude = latitude;
                }
                if (longitude > maxLatitude) {
                    maxLongitude = longitude;
                }
                if (longitude < minLongitude) {
                    minLongitude = longitude;
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        System.out.println(maxLatitude);
//        System.out.println(minLatitude);
//        System.out.println(maxLongitude);
//        System.out.println(minLongitude);
        for (int i = 0; i < 20; i++) {
            float posX = minLatitude + i * (maxLatitude - minLatitude) / 20;
            float posY = minLongitude + i * (maxLongitude - minLongitude) / 20;
//            System.out.println("\n");
//            System.out.println(posX);
//            System.out.println(posY);
            mps.addMeetingPOint(posX, posY);

        }

        mps.showMeetingPoints();
    }
}
