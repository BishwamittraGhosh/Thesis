/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SSTk;

import gnu.trove.procedure.TIntProcedure;
import net.sf.jsi.Point;
import net.sf.jsi.Rectangle;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

/**
 *
 * @author vacuum
 */
public class Additional {

    public Additional() {
        // TODO code application logic here
        int rowCount = 10;
        int columnCount = 10;
        int count = rowCount * columnCount;
        long start, end;

        final Rectangle[] rects = new Rectangle[count];
        int id = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < rowCount; column++) {
                rects[id++] = new Rectangle(row, column, row + 0.5f, column + 0.5f); // 
            }
        }

        
        SpatialIndex si = new RTree();
        si.init(null);
        for (id = 0; id < count; id++) {
            si.add(rects[id], id);
        }

        final Point p = new Point(6.3f, 4.3f);

        for (int j = 0; j < 10; j++) {
            System.out.println("iteration: "+j);
            si.nearest(p, new TIntProcedure() {
                public boolean execute(int i) {
                    System.out.println("Rectangle " + i + " " + rects[i] + ", distance=" + rects[i].distance(p));
                    si.delete(rects[i], i);
                    return true;
                }
            }, (float) 1);
            

        }
    }
    
}
