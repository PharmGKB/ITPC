package org.pharmgkb;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan Whaley
 * Date: Jul 13, 2010
 * Time: 6:09:19 PM
 */
public class Metabolizer {
  public enum Status {Unknown, PM, IM, EM, UM}

  private static final Map<String, Status> metabMap = new HashMap<String, Status>();
  static {
    metabMap.put("*3",Status.PM);
    metabMap.put("*4",Status.PM);
    metabMap.put("*5",Status.PM);
    metabMap.put("*6",Status.PM);
    metabMap.put("*7",Status.PM);
    metabMap.put("*8",Status.PM);
    metabMap.put("*11",Status.PM);
    metabMap.put("*12",Status.PM);
    metabMap.put("*13",Status.PM);
    metabMap.put("*14",Status.PM);
    metabMap.put("*15",Status.PM);
    metabMap.put("*16",Status.PM);
    metabMap.put("*18",Status.PM);
    metabMap.put("*19",Status.PM);
    metabMap.put("*20",Status.PM);
    metabMap.put("*40",Status.PM);
    metabMap.put("*42",Status.PM);
    metabMap.put("*44",Status.PM);
    metabMap.put("*56",Status.PM);
    metabMap.put("*36",Status.PM);
    metabMap.put("*38",Status.PM);
    metabMap.put("*4XN",Status.PM);

    metabMap.put("*9",Status.IM);
    metabMap.put("*10",Status.IM);
    metabMap.put("*17",Status.IM);
    metabMap.put("*29",Status.IM);
    metabMap.put("*37",Status.IM);
    metabMap.put("*41",Status.IM);
    metabMap.put("*45",Status.IM);
    metabMap.put("*46",Status.IM);

    metabMap.put("*1",Status.EM);
    metabMap.put("*2",Status.EM);
    metabMap.put("*33",Status.EM);
    metabMap.put("*35",Status.EM);
    metabMap.put("*39",Status.EM);
    metabMap.put("*43",Status.EM);

    metabMap.put("*1XN", Status.UM);
    metabMap.put("*2XN", Status.UM);
    metabMap.put("*9XN", Status.UM);
    metabMap.put("*10XN", Status.UM);
    metabMap.put("*35XN", Status.UM);
    metabMap.put("*39XN", Status.UM);
    metabMap.put("*41XN", Status.UM);
    metabMap.put("*45XN", Status.UM);
  }

  private List<Status> m_statuses = new ArrayList<Status>();

  public List<Status> getStatuses() {
    return m_statuses;
  }

  public void addStatus(Status status) throws Exception {
    if (m_statuses.size()<2) {
      m_statuses.add(status);
    }
    else {
      throw new Exception("A subject can only have a maximum of two metabolizer statuses");
    }
  }

  public boolean isUnknown() {
    return m_statuses.contains(Status.Unknown) || m_statuses.size()!=2;
  }

  public boolean is(Status status1, Status status2) {
    if (status1 != null && status2 != null) {
      if (status1==status2) {
        return statusCount(status1)==2;
      }
      else {
        return (statusCount(status1) == 1 && statusCount(status2)==1);
      }
    }
    return false;
  }

  private int statusCount(Status pStatus) {
    int count = 0;
    for (Status status : this.getStatuses()) {
      if (status == pStatus) count++;
    }
    return count;
  }

  protected static String getText(Status value) {
    switch (value) {
      case IM: return "IM";
      case PM: return "PM";
      case EM: return "EM";
      case UM: return "UM";
      default: return "Unknown";
    }
  }

  public String toString() {
    if (m_statuses.isEmpty()) {
      return getText(Status.Unknown);
    }

    List<String> genotypes = new ArrayList<String>();
    for (Status status : m_statuses) {
      genotypes.add(getText(status));
    }

    StringBuilder genoBuilder = new StringBuilder();
    Collections.sort(genotypes, String.CASE_INSENSITIVE_ORDER);
    for (int x = 0; x < genotypes.size(); x++) {
      genoBuilder.append(genotypes.get(x));
      if (x != genotypes.size() - 1) {
        genoBuilder.append("/");
      }
    }
    return genoBuilder.toString();
  }
}
