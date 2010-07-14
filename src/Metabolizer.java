import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan Whaley
 * Date: Jul 13, 2010
 * Time: 6:09:19 PM
 */
public class Metabolizer {
  public enum Status {Unknown, PM, IM, EM, UM}

  private List<Status> m_statuses = new ArrayList<Status>();
  private String m_text;

  public List<Status> getStatuses() {
    return m_statuses;
  }

  public void setStatuses(List<Status> m_statuses) {
    this.m_statuses = m_statuses;
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
