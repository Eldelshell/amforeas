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
package amforeas.jdbc;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang3.StringUtils;

/**
 * An object to represent the two limit parameters (limit & offset) which are then translated to
 * their correct form in SQL. If no limit parameter is given, the default is 25
 * the configuration. The default value for the offset or start is 0.
 * The limit value has a maximum of 1000.
 */
public class LimitParam {

    private final Integer limit;
    private final Integer start;

    private static final Integer default_limit = Integer.valueOf(25);
    private static final Integer max_limit = Integer.valueOf(1000);

    public LimitParam() {
        this.limit = default_limit;
        this.start = 0;
    }

    public LimitParam(Integer limit) {
        this.limit = getMaxLimit(limit);
        this.start = 0;
    }

    public LimitParam(Integer limit, Integer start) {
        this.limit = getMaxLimit(limit);
        this.start = start;
    }

    public Integer getLimit () {
        return limit;
    }

    public Integer getStart () {
        return start;
    }

    private Integer getMaxLimit (Integer limit) {
        if (limit >= max_limit) {
            limit = max_limit;
        }
        return limit;
    }

    public static LimitParam valueOf (final MultivaluedMap<String, String> pathParams, final Integer pageSize) {
        if (StringUtils.isNumeric(pathParams.getFirst("page"))) {
            Integer page = Integer.valueOf(pathParams.getFirst("page"));
            return new LimitParam(pageSize, (page - 1) * pageSize);
        }

        return LimitParam.valueOf(pathParams);
    }

    /**
     * From the received parameters, try to obtain a LimitParam object. By default, the LimitParam
     * always has a limit of 25 and an offset (start) in 0.
     * @param pathParams
     * @return 
     */
    public static LimitParam valueOf (final MultivaluedMap<String, String> pathParams) {
        Integer l = null;
        if (StringUtils.isNumeric(pathParams.getFirst("limit"))) {
            l = Integer.valueOf(pathParams.getFirst("limit"));
        }

        Integer o = null;
        if (StringUtils.isNumeric(pathParams.getFirst("offset"))) {
            o = Integer.valueOf(pathParams.getFirst("offset"));
        }

        LimitParam instance;
        if (l == null) {
            instance = new LimitParam();
        } else {
            if (o == null) {
                instance = new LimitParam(l);
            } else {
                instance = new LimitParam(l, o);
            }
        }
        return instance;
    }

    @Override
    public String toString () {
        StringBuilder b = new StringBuilder("{LimitParam:{limit:");
        b.append(limit);
        b.append(", start: ");
        b.append(start);
        b.append("}}");
        return b.toString();
    }
}
