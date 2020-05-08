/**
 * Copyright (C) Alejandro Ayuso
 *
 * This file is part of Amforeas. Amforeas is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas.rest.xstream;

import java.util.HashMap;
import java.util.Map;

/**
 * Represent a database result row with a Map of cells and a row counter.
 */
public class Row {

    private int roi;
    private Map<String, Object> cells;// = new HashMap<String, String>();

    public Row() {}

    /**
     * Instantiates a new row with empty cells.
     * @param roi the row number.
     */
    public Row(int roi) {
        this.roi = roi;
        this.cells = new HashMap<String, Object>();
    }

    /**
     * Instantiates a new row with the given cells.
     * @param roi the row number.
     * @param cells a map with the cells
     */
    public Row(int roi, Map<String, Object> cells) {
        this.roi = roi;
        this.cells = cells;
    }

    public int getRoi () {
        return roi;
    }

    public Map<String, Object> getCells () {
        return cells;
    }

    public void setCells (Map<String, Object> cells) {
        this.cells = cells;
    }

    public void setRoi (int roi) {
        this.roi = roi;
    }
}
