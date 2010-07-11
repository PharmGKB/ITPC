/*
 ----- BEGIN LICENSE BLOCK -----
 Version: MPL 1.1/GPL 2.0/LGPL 2.1

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is PharmGen.

 The Initial Developer of the Original Code is
 PharmGKB (The Pharmacogenetics and Pharmacogenetics Knowledge Base,
 supported by NIH U01GM61374).
 Portions created by the Initial Developer are Copyright (C) 2009
 the Initial Developer. All Rights Reserved.

 Contributor(s):

 Alternatively, the contents of this file may be used under the terms of
 either the GNU General Public License Version 2 or later (the "GPL"), or the
 GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in
 which case the provisions of the GPL or the LGPL are applicable instead of
 those above. If you wish to allow use of your version of this file only
 under the terms of either the GPL or the LGPL, and not to allow others to
 use your version of this file under the terms of the MPL, indicate your
 decision by deleting the provisions above and replace them with the notice
 and other provisions required by the GPL or the LGPL. If you do not delete
 the provisions above, a recipient may use your version of this file under
 the terms of any one of the MPL, the GPL or the LGPL.

 ----- END LICENSE BLOCK -----
 */
package util;

import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;


/**
 * This is an iterator for POI worksheets.  It determines whether there are more lines by looking
 * for a specific number of empty rows (the default is {@link #MAX_EMPTY_ROWS}.  If will still read
 * intermediary empty rows if the number of empty rows is less than the maximum number of allowed
 * empty rows.
 *
 *
 * @author Mark Woon
 */
public class PoiWorksheetIterator implements Iterator<List<String>> {
  public static final int MAX_EMPTY_ROWS = 3;
  private Sheet m_sheet;
  private int m_currentRow;
  private int m_maxEmptyRows = MAX_EMPTY_ROWS;
  private int m_maxColumns;


  /**
   * Standard constructor.
   *
   * @param sheet the worksheet to iterate through
   */
  public PoiWorksheetIterator(Sheet sheet) {

    this(sheet, 0);
  }

  /**
   * Standard constructor.
   *
   * @param sheet the worksheet to iterate through
   * @param startRow the row number to start iterating from, the first row being 1
   */
  public PoiWorksheetIterator(Sheet sheet, int startRow) {

    m_sheet = sheet;
    m_currentRow = startRow;
    m_maxColumns = -1;
  }

  /**
   * Standard constructor.
   *
   * @param sheet the worksheet to iterate through
   * @param startRow the row number to start iterating from, the first row being 1
   * @param maxColumns the number of columns to read per row (from the first column)
   */
  public PoiWorksheetIterator(Sheet sheet, int startRow, int maxColumns) {

    m_sheet = sheet;
    m_currentRow = startRow - 1;
    if (m_currentRow < 0) {
      throw new IllegalArgumentException("Start row cannot be less than 0");
    }
    m_maxColumns = maxColumns - 1;
    if (m_maxColumns < 0) {
      throw new IllegalArgumentException("Max columns cannot be less than 0");
    }
  }


  /**
   * Gets the maximum number of empty rows before considering that there are no more rows.  The
   * default is {@link #MAX_EMPTY_ROWS}.
   *
   * @return the maximum number of empty rows before considering that there are no more rows
   */
  public int getMaxEmptyRows() {

    return m_maxEmptyRows;
  }

  /**
   * Sets the maximum number of empty rows before considering that there are no more rows.
   *
   * @param maxEmptyRows the maximum number of empty rows before considering that there are no more
   * rows.
   */
  public void setMaxEmptyRows(int maxEmptyRows) {

    m_maxEmptyRows = maxEmptyRows;
  }


  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other words, returns
   * <tt>true</tt> if <tt>next</tt> would return an element rather than throwing an exception.)
   *
   * @return <tt>true</tt> if the iterator has more elements.
   */
  public boolean hasNext() {

    int currentRow = m_currentRow;
    for (int emptyRowCount = 0; emptyRowCount < m_maxEmptyRows; emptyRowCount++) {
      // advance row count and grab it from sheet
      Row row = m_sheet.getRow(currentRow++);
      if (row != null) {
        return true;
      }
    }
    return false;
  }


  /**
   * Returns the next element in the iteration.  Calling this method repeatedly until the {@link
   * #hasNext()} method returns false will return each element in the underlying collection exactly
   * once.
   *
   * @return the next element in the iteration.
   * @throws java.util.NoSuchElementException iteration has no more elements.
   */
  public List<String> next() {

    if (m_maxColumns == -1) {
      return POIUtils.getStringCellValues(m_sheet, m_currentRow++);
    }
    return POIUtils.getStringCellValues(m_sheet, m_currentRow++, 0, m_maxColumns);
  }


  /**
   * Gets the current row number of the worksheet that this iterator is on.  This number begins at
   * 0.
   *
   * @return the current row number of the worksheet that this iterator is on
   */
  public int getRowNumber() {

    return m_currentRow;
  }


  /**
   * Removes from the underlying collection the last element returned by the iterator (optional
   * operation).  This method can be called only once per call to <tt>next</tt>.  The behavior of an
   * iterator is unspecified if the underlying collection is modified while the iteration is in
   * progress in any way other than by calling this method.
   *
   * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this
   * Iterator.
   * @throws IllegalStateException if the <tt>next</tt> method has not yet been called, or the
   * <tt>remove</tt> method has already been called after the last call to the <tt>next</tt>
   * method.
   */
  public void remove() {

    throw new UnsupportedOperationException("remove() is not supported");
  }
}
