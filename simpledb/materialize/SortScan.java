package simpledb.materialize;

import simpledb.record.RID;
import simpledb.query.*;
import java.util.*;

/**
 * The Scan class for the <i>sort</i> operator.
 * @author Edward Sciore
 */
/**
 * @author sciore
 *
 */
public class SortScan implements Scan {
   private UpdateScan s1, s2, s3, s4=null, currentscan=null;
   private RecordComparator comp;
   private boolean hasmore1, hasmore2, hasmore3, hasmore4=false;
   private List<RID> savedposition;

   /**
    * Creates a sort scan, given a list of 1 or 2 runs.
    * If there is only 1 run, then s2 will be null and
    * hasmore2 will be false.
    * @param runs the list of runs
    * @param comp the record comparator
    */
   public SortScan(List<TempTable> runs, RecordComparator comp) {
      this.comp = comp;
      s1 = (UpdateScan) runs.get(0).open();
      hasmore1 = s1.next();
      if (runs.size() > 1) {
         s2 = (UpdateScan) runs.get(1).open();
         hasmore2 = s2.next();
         if (runs.size() > 2) {
            s3 = (UpdateScan) runs.get(2).open();
            hasmore3 = s3.next();
            if (runs.size() > 3) {
               s4 = (UpdateScan) runs.get(3).open();
               hasmore4 = s4.next();
            }
         }
      }
   }

   /**
    * Positions the scan before the first record in sorted order.
    * Internally, it moves to the first record of each underlying scan.
    * The variable currentscan is set to null, indicating that there is
    * no current scan.
    * @see simpledb.query.Scan#beforeFirst()
    */
   public void beforeFirst() {
      currentscan = null;
      s1.beforeFirst();
      hasmore1 = s1.next();
      if (s2 != null) {
         s2.beforeFirst();
         hasmore2 = s2.next();
      }
      if (s3 != null) {
         s3.beforeFirst();
         hasmore3 = s3.next();
      }
      if (s4 != null) {
         s4.beforeFirst();
         hasmore4 = s4.next();
      }
   }

   /**
    * Moves to the next record in sorted order.
    * First, the current scan is moved to the next record.
    * Then the lowest record of the two scans is found, and that
    * scan is chosen to be the new current scan.
    * @see simpledb.query.Scan#next()
    */
   public boolean next() {
      if (currentscan != null) {
        if (currentscan == s1) {
            hasmore1 = s1.next();
        } else if (currentscan == s2) {
            hasmore2 = s2.next();
        } else if (currentscan == s3) {
            hasmore3 = s3.next();
        } else if (currentscan == s4) {
            hasmore4 = s4.next();
        }
      }

      if (!hasmore1 && !hasmore2 && !hasmore3 && !hasmore4) {
          return false;
      }
      else if (hasmore1 && hasmore2 && hasmore3 && hasmore4) {
          if (comp.compare(s1, s2) < 0) {
              if (comp.compare(s3, s4) < 0) {
                  if (comp.compare(s1, s3) < 0) {
                      currentscan = s1;
                  } else {
                      currentscan = s3;
                  }
              } else {
                  if (comp.compare(s1, s4) < 0) {
                      currentscan = s1;
                  } else {
                      currentscan = s4;
                  }
              }
          } else {
              if (comp.compare(s3, s4) < 0) {
                  if (comp.compare(s2, s3) < 0) {
                      currentscan = s2;
                  } else {
                      currentscan = s3;
                  }
              } else {
                  if (comp.compare(s2, s4) < 0) {
                      currentscan = s2;
                  } else {
                      currentscan = s4;
                  }
              }
          }
      }
      else if (hasmore1 && hasmore2 && hasmore3) {
         if (comp.compare(s1, s2) < 0) {
             if (comp.compare(s1, s3) < 0) {
                 currentscan = s1;
             } else {
                 currentscan = s3;
             }
         }
         else {
             if (comp.compare(s2, s3) < 0) {
                 currentscan = s2;
             } else {
                 currentscan = s3;
             }
         }
      }
      else if (hasmore1 && hasmore2 && hasmore4) {
         if (comp.compare(s1, s2) < 0) {
             if (comp.compare(s1, s4) < 0) {
                 currentscan = s1;
             } else {
                 currentscan = s4;
             }
         }
         else {
             if (comp.compare(s2, s4) < 0) {
                 currentscan = s2;
             } else {
                 currentscan = s4;
             }
         }
      }
      else if (hasmore1 && hasmore3 && hasmore4) {
         if (comp.compare(s1, s3) < 0) {
             if (comp.compare(s1, s4) < 0) {
                 currentscan = s1;
             } else {
                 currentscan = s4;
             }
         }
         else {
             if (comp.compare(s3, s4) < 0) {
                 currentscan = s3;
             } else {
                 currentscan = s4;
             }
         }
      }
      else if (hasmore2 && hasmore3 && hasmore4) {
         if (comp.compare(s2, s3) < 0) {
             if (comp.compare(s2, s4) < 0) {
                 currentscan = s2;
             } else {
                 currentscan = s4;
             }
         }
         else {
             if (comp.compare(s3, s4) < 0) {
                 currentscan = s3;
             } else {
                 currentscan = s4;
             }
         }
      }
      else if (hasmore1 && hasmore2) {
         if (comp.compare(s1, s2) < 0)
            currentscan = s1;
         else
            currentscan = s2;
      }
      else if (hasmore1 && hasmore3) {
         if (comp.compare(s1, s3) < 0)
            currentscan = s1;
         else
            currentscan = s3;
      }
      else if (hasmore1 && hasmore4) {
         if (comp.compare(s1, s4) < 0)
            currentscan = s1;
         else
            currentscan = s4;
      }
      else if (hasmore2 && hasmore3) {
         if (comp.compare(s2, s3) < 0)
            currentscan = s2;
         else
            currentscan = s3;
      }
      else if (hasmore2 && hasmore4) {
         if (comp.compare(s2, s4) < 0)
            currentscan = s2;
         else
            currentscan = s4;
      }
      else if (hasmore3 && hasmore4) {
         if (comp.compare(s3, s4) < 0)
            currentscan = s3;
         else
            currentscan = s4;
      }
      else if (hasmore1) {
          currentscan = s1;
      }
      else if (hasmore2) {
          currentscan = s2;
      }
      else if (hasmore3) {
          currentscan = s3;
      }
      else if (hasmore4) {
          currentscan = s4;
      }
      return true;
   }

   /**
    * Closes the two underlying scans.
    * @see simpledb.query.Scan#close()
    */
   public void close() {
      s1.close();
      if (s2 != null) {
          s2.close();
      }
      if (s3 != null) {
          s3.close();
      }
      if (s4 != null) {
          s4.close();
      }
   }

   /**
    * Gets the Constant value of the specified field
    * of the current scan.
    * @see simpledb.query.Scan#getVal(java.lang.String)
    */
   public Constant getVal(String fldname) {
      return currentscan.getVal(fldname);
   }

   /**
    * Gets the integer value of the specified field
    * of the current scan.
    * @see simpledb.query.Scan#getInt(java.lang.String)
    */
   public int getInt(String fldname) {
      return currentscan.getInt(fldname);
   }

   /**
    * Gets the string value of the specified field
    * of the current scan.
    * @see simpledb.query.Scan#getString(java.lang.String)
    */
   public String getString(String fldname) {
      return currentscan.getString(fldname);
   }

   /**
    * Returns true if the specified field is in the current scan.
    * @see simpledb.query.Scan#hasField(java.lang.String)
    */
   public boolean hasField(String fldname) {
      return currentscan.hasField(fldname);
   }

   /**
    * Saves the position of the current record,
    * so that it can be restored at a later time.
    */
   public void savePosition() {
      RID rid1 = s1.getRid();
      RID rid2 = (s2 == null) ? null : s2.getRid();
      RID rid3 = (s3 == null) ? null : s3.getRid();
      RID rid4 = (s4 == null) ? null : s4.getRid();
      savedposition = Arrays.asList(rid1,rid2,rid3,rid4);
   }

   /**
    * Moves the scan to its previously-saved position.
    */
   public void restorePosition() {
      RID rid1 = savedposition.get(0);
      RID rid2 = savedposition.get(1);
      RID rid3 = savedposition.get(2);
      RID rid4 = savedposition.get(3);
      s1.moveToRid(rid1);
      if (rid2 != null) {
          s2.moveToRid(rid2);
      }
      if (rid3 != null) {
          s3.moveToRid(rid3);
      }
      if (rid4 != null) {
          s4.moveToRid(rid4);
      }
   }
}
