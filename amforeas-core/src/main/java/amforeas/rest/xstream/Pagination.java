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

import java.util.List;
import java.util.Objects;
import amforeas.jdbc.LimitParam;

public class Pagination {

    private Integer page;
    private Integer size;
    private Integer pages;
    private Integer total;

    public Pagination() {
        super();
    }

    public Pagination(Integer page, Integer size, Integer pages, Integer total) {
        super();
        this.page = page;
        this.size = size;
        this.pages = pages;
        this.total = total;
    }

    public static Pagination of (LimitParam limitParam) {
        Pagination p = new Pagination();
        Integer diff = limitParam.getLimit() - limitParam.getStart();
        return p.setPage(limitParam.getLimit() / diff).setSize(diff);
    }

    public static Pagination of (LimitParam limitParam, List<?> results, Integer total) {
        return results != null ? Pagination.of(limitParam, results.size(), total) : Pagination.of(limitParam, 0, total);
    }

    public static Pagination of (LimitParam limitParam, Integer results, Integer total) {
        final Pagination p = new Pagination();

        if (total == null || total == 0) {
            return p.setPage(0).setSize(results).setTotal(0).setPages(0);
        }

        Integer pageSize = limitParam.getLimit();
        int currentPage = (limitParam.getStart() + pageSize) / pageSize;

        if (total < 0) {
            // We failed to obtain total
            return p.setPage(currentPage).setSize(results).setTotal(null).setPages(null);
        }


        Double pages = Double.valueOf(Math.ceil(total.doubleValue() / pageSize.doubleValue()));
        return p.setPage(currentPage).setSize(results).setTotal(total).setPages(pages.intValue());
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

    @Override
    public String toString () {
        return "Pagination [page=" + page + ", size=" + size + ", pages=" + pages + ", total=" + total + "]";
    }

    @Override
    public int hashCode () {
        return Objects.hash(page, pages, size, total);
    }

    @Override
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        return this.hashCode() == obj.hashCode();
    }



}
