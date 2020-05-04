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

import amforeas.jdbc.LimitParam;

public class Pagination {

    private Integer page;
    private Integer size;
    private Integer pages;
    private Integer total;

    public Pagination() {
        super();
    }

    public static Pagination of (LimitParam limitParam) {
        Pagination p = new Pagination();
        Integer diff = limitParam.getLimit() - limitParam.getStart();
        return p.setPage(limitParam.getLimit() / diff).setSize(diff);
    }

    public static Pagination of (LimitParam limitParam, Integer total) {
        if (total == null || total <= 0) {
            return of(limitParam);
        }

        Pagination p = new Pagination();
        Integer diff = limitParam.getLimit() - limitParam.getStart();
        Double k = Double.valueOf(Math.ceil(total.doubleValue() / diff.doubleValue()));
        Integer pages = total >= diff ? k.intValue() : total;
        return p.setPage(limitParam.getLimit() / diff).setSize(diff).setTotal(total).setPages(pages);
    }

    public Integer getPage () {
        return page;
    }

    public Pagination setPage (Integer page) {
        this.page = page;
        return this;
    }

    public Integer getSize () {
        return size;
    }

    public Pagination setSize (Integer size) {
        this.size = size;
        return this;
    }

    public Integer getPages () {
        return pages;
    }

    public Pagination setPages (Integer pages) {
        this.pages = pages;
        return this;
    }

    public Integer getTotal () {
        return total;
    }

    public Pagination setTotal (Integer total) {
        this.total = total;
        return this;
    }



}
