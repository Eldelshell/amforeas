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

package amforeas.client.model;

import java.util.Objects;

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
