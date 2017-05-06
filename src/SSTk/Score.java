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
public class Score {

    private IntermediateSet vI;
    private double alpha = (double) .33;
    private double beta = (double) .33;
    private double gamma = (double) .33;
    private int minConstraint = SSTK.minimumConstraint;
    private int maxPosMember = 20;
    private int minMember = SSTK.minimumMember;
    private double maxDistance = 10;

    public Score(IntermediateSet vI) {
        this.vI = vI;
    }

    double score() {
//        System.out.println(socialScore());
//        System.out.println(sizeScore());
//        System.out.println(spatialScore());
        return alpha * socialScore() + beta * spatialScore() + gamma * sizeScore();
    }

    double socialScore() {
        int s = vI.getSize();
//        System.out.println(s);
        return (double) vI.getTotalConnectivity() / (s * (s - 1));
    }

    double spatialScore() {
        //System.out.println(vI.getTotalDistance());
        return 1 - (double) vI.getTotalDistance() / (maxDistance * vI.getSize());
    }

    double sizeScore() {
        int s = vI.getSize();
        if (s < minMember) {
            return 0;
        } else {
            return (double) s / maxPosMember;
        }

    }

    double getUpperDistanceForMember() {
        int s = vI.getSize();

//        System.out.println("member size: "+s);
//        System.out.println("maxDistance "+maxDistance);
//        System.out.println("connectivity: "+vI.getTotalConnectivity());
//        System.out.println("distance: "+vI.getTotalDistance());
//        System.out.println((2 * SSTK.minimumConstraint
//                - (double) (2 * vI.getTotalConnectivity()) / (s - 1)));
        //System.out.println("....................................");
        return ((double) (maxDistance * (s + 1)) / beta) * ((double) alpha / (s * (s + 1)) * (2 * SSTK.minimumConstraint
                - (double) (2 * vI.getTotalConnectivity()) / (s - 1))
                + ((double) gamma / maxPosMember)) + (double) vI.getTotalDistance() / s;
    }

    double getLowerBoundOnConnection(double dMin) {
        int s = vI.getSize();
        //      s=3;
//        System.out.println(" connection lower calculation");
//        System.out.println("member size: " + s);
//        System.out.println("maxDistance " + maxDistance);
//        System.out.println("connectivity: " + vI.getTotalConnectivity());
//        System.out.println("distance: "+vI.getTotalDistance());
        double con = (double) ((s * (s + 1)) / alpha) * (((double) beta / (maxDistance
                * (s + 1))) * (dMin - (double) vI.getTotalDistance() / s) - (double) gamma / maxPosMember)
                + (double) (2 * vI.getTotalConnectivity()) / (s - 1);
//        double con = (double)((s * (s + 1)) / alpha) * (((double)beta/(15 * 
//                (s + 1)))*(14 - (double) 35 / s)-(double) gamma / 5)
//                +(double)(2*4)/(s-1);

//        System.out.println(con);
        return con;
    }

    double getDistanceTermination(int s) {
        s = vI.getSize();
        //System.out.println(s);

        //System.out.println("....................................");
        return ((double) (maxDistance * (s + 1)) / beta) * ((double) alpha / (s * (s + 1)) * (2 * s
                - (double) (2 * vI.getTotalConnectivity()) / (s - 1))
                + ((double) gamma / maxPosMember)) + (double) vI.getTotalDistance() / s;

    }

    double advancePruningDistance(int fOld, int nOld, double dOld) {

        int s = vI.getSize();
        int cNew = (minMember - s) * (minMember + s - 1);
//        double dist=minMember*(((double)maxDistance/beta)*(alpha*
//                ((double)(vI.getTotalConnectivity()+cNew)/(minMember*(minMember-1))-
//                (double)fOld/(nOld*(nOld-1)))+(double)((minMember-s)*gamma)/maxPosMember)
//                -(double)dOld/nOld)-vI.getTotalDistance();
//        

        double dist = (double) minMember * ((double) ((double) maxDistance / beta) * ((double) alpha
                * ((double) (vI.getTotalConnectivity() + cNew) / (minMember * (minMember - 1))
                - (double) fOld / (nOld * (nOld - 1))) + (double) ((minMember - s) * gamma) / maxPosMember)
                + (double) dOld / nOld) - vI.getTotalDistance();
        return dist;

    }

}
