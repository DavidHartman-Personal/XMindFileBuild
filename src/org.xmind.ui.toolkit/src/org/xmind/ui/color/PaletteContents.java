/* ******************************************************************************
 * Copyright (c) 2006-2012 XMind Ltd. and others.
 * 
 * This file is a part of XMind 3. XMind releases 3 and
 * above are dual-licensed under the Eclipse Public License (EPL),
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 * and the GNU Lesser General Public License (LGPL), 
 * which is available at http://www.gnu.org/licenses/lgpl.html
 * See http://www.xmind.net/license.html for details.
 * 
 * Contributors:
 *     XMind Ltd. - initial API and implementation
 *******************************************************************************/
package org.xmind.ui.color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.RGB;

public class PaletteContents {

    private static PaletteContents instance = null;

    public static PaletteContents getDefault() {
        if (instance == null) {
            PaletteContents palette = new PaletteContents(48, 6, 8);
            // row 1
            palette.addItem(PaletteItem.Black);
            palette.addItem(PaletteItem.Red);
            palette.addItem(PaletteItem.Orange);
            palette.addItem(PaletteItem.Yellow);
            palette.addItem(PaletteItem.Green);
            palette.addItem(PaletteItem.Blue);
            palette.addItem(PaletteItem.Indigo);
            palette.addItem(PaletteItem.Purple);

            // row 2
            palette.addItem(PaletteItem.White);
            palette.addItem(PaletteItem.Red80);
            palette.addItem(PaletteItem.Orange80);
            palette.addItem(PaletteItem.Yellow80);
            palette.addItem(PaletteItem.Green80);
            palette.addItem(PaletteItem.Blue80);
            palette.addItem(PaletteItem.Indigo80);
            palette.addItem(PaletteItem.Purple80);

            // row 3
            palette.addItem(PaletteItem.Black80);
            palette.addItem(PaletteItem.Red60);
            palette.addItem(PaletteItem.Orange60);
            palette.addItem(PaletteItem.Yellow60);
            palette.addItem(PaletteItem.Green60);
            palette.addItem(PaletteItem.Blue60);
            palette.addItem(PaletteItem.Indigo60);
            palette.addItem(PaletteItem.Purple60);

            // row 4
            palette.addItem(PaletteItem.Black60);
            palette.addItem(PaletteItem.Red40);
            palette.addItem(PaletteItem.Orange40);
            palette.addItem(PaletteItem.Yellow40);
            palette.addItem(PaletteItem.Green40);
            palette.addItem(PaletteItem.Blue40);
            palette.addItem(PaletteItem.Indigo40);
            palette.addItem(PaletteItem.Purple40);

            // row 5
            palette.addItem(PaletteItem.Black40);
            palette.addItem(PaletteItem.Red25);
            palette.addItem(PaletteItem.Orange25);
            palette.addItem(PaletteItem.Yellow25);
            palette.addItem(PaletteItem.Green25);
            palette.addItem(PaletteItem.Blue25);
            palette.addItem(PaletteItem.Indigo25);
            palette.addItem(PaletteItem.Purple25);

            // row 6
            palette.addItem(PaletteItem.Black20);
            palette.addItem(PaletteItem.Red50);
            palette.addItem(PaletteItem.Orange50);
            palette.addItem(PaletteItem.Yellow50);
            palette.addItem(PaletteItem.Green50);
            palette.addItem(PaletteItem.Blue50);
            palette.addItem(PaletteItem.Indigo50);
            palette.addItem(PaletteItem.Purple50);
            instance = palette;
        }
        return instance;
    }

    private List<PaletteItem> items;

    private int prefRows;

    private int prefColumns;

    public PaletteContents() {
        this(10);
    }

    public PaletteContents(int num) {
        this(num, calcRows(num));
    }

    public PaletteContents(int num, int preferredRows) {
        this(num, preferredRows, calcColumns(num, preferredRows));
    }

    public PaletteContents(int num, int preferredRows, int preferredColumns) {
        if (num <= 0)
            throw new IllegalArgumentException();
        this.items = new ArrayList<PaletteItem>(num);
        this.prefRows = Math.min(Math.max(preferredRows, 1), num);
        this.prefColumns = Math.min(Math.max(preferredColumns, 1), num);
    }

    public PaletteContents(PaletteItem[] items) {
        this.items = new ArrayList<PaletteItem>(Arrays.asList(items));
        this.prefRows = calcRows(items.length);
        this.prefColumns = calcColumns(items.length, prefRows);
    }

    public void addItem(RGB color, String description) {
        addItem(new PaletteItem(color, description));
    }

    public void addItem(int r, int g, int b, String description) {
        addItem(new RGB(r, g, b), description);
    }

    public void addItem(int color, String description) {
        addItem((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff,
                description);
    }

    public void addItem(PaletteItem item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public RGB getColor(int index) {
        return items.get(index).color;
    }

    public String getDescription(int index) {
        return items.get(index).description;
    }

    public int getPreferredRows() {
        return prefRows;
    }

    public int getPreferredColumns() {
        return prefColumns;
    }

    public int size() {
        return items.size();
    }

    public PaletteItem[] toArray() {
        return items.toArray(new PaletteItem[items.size()]);
    }

    public List<PaletteItem> toList() {
        return Collections.unmodifiableList(items);
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof PaletteContents))
            return false;
        PaletteContents that = (PaletteContents) obj;
        return this.items.equals(that.items);
    }

    public int hashCode() {
        return items.hashCode();
    }

    private static int calcRows(int num) {
        return (int) Math.sqrt(num);
    }

    private static int calcColumns(int num, int rows) {
        return rows == 0 ? 0 : (num + rows - 1) / rows;
    }

}