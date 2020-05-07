package amforeas.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import com.fasterxml.jackson.core.JsonProcessingException;

public class RequestParams {

    private final String resource;
    private final String primaryKey;
    private final String primaryKeyValue;

    private final String column;
    private final String columnValue;

    private final String query;
    private final List<NameValuePair> queryArgs;

    private final Integer page;
    private final Integer limit;
    private final Integer offset;

    private final String sort;
    private final String direction;

    private final List<NameValuePair> insert;
    private final List<NameValuePair> update;

    public static Builder builder (String resource) {
        return new Builder(resource);
    }

    public static class Builder {

        private final String resource;
        private String primaryKey;
        private String primaryKeyValue;

        private String column;
        private String columnValue;

        private String query;
        private List<NameValuePair> queryArgs;

        private List<NameValuePair> insert;
        private List<NameValuePair> update;

        private Integer page;
        private Integer limit;
        private Integer offset;

        private String sort;
        private String direction;

        Builder(String resource) {
            this.resource = resource;
        }

        Builder key (String key) {
            this.primaryKey = key;
            return this;
        }

        Builder id (String id) {
            if (this.primaryKey == null) {
                this.primaryKey = "id";
            }

            this.primaryKeyValue = id;
            return this;
        }

        /**
         * Starts a query for a given column name: SELECT * FROM x WHERE column EQUALS value.
         * Remember to set set the value with .value()
         * @param column - the column name
         * @return
         */
        Builder column (String column) {
            this.column = column;
            return this;
        }

        Builder value (String value) {
            if (StringUtils.isEmpty(this.column)) {
                throw new IllegalStateException("Cannot set value to undefined column. Use .column() first");
            }

            this.columnValue = value;
            return this;
        }

        Builder dynamicQuery (String query) {
            if (!query.startsWith("findBy") && !query.startsWith("findAllBy")) {
                throw new IllegalArgumentException("Invalid Dynamic Query. It must start with findBy or findAllBy");
            }

            this.queryArgs = new ArrayList<>(2);
            this.query = query;
            return this;
        }

        Builder addQueryParam (String value) {
            if (this.query == null || this.queryArgs == null) {
                throw new IllegalStateException("Can add parameters to inexisten query. Use .dynamicQuery before .addQueryParam");
            }

            if (this.queryArgs.size() == 2) {
                throw new IndexOutOfBoundsException("Can't add more than two arguments to dynamic query");
            }

            this.queryArgs.add(new BasicNameValuePair("args", value));
            return this;
        }

        Builder insert (String name, String value) {
            if (this.insert == null) {
                this.insert = new ArrayList<>();
            }
            this.insert.add(new BasicNameValuePair(name, value));
            return this;
        }

        Builder insert (List<NameValuePair> insert) {
            this.insert = insert;
            return this;
        }

        Builder update (String name, String value) {
            if (this.update == null) {
                this.update = new ArrayList<>();
            }
            this.update.add(new BasicNameValuePair(name, value));
            return this;
        }

        Builder update (List<NameValuePair> update) {
            this.update = update;
            return this;
        }

        Builder page (Integer page) {
            if (this.limit != null || this.offset != null) {
                throw new IllegalStateException("You can only use either page or from/to params");
            }

            this.page = page;
            return this;
        }

        Builder from (Integer from) {
            this.offset = from;
            return this;
        }

        Builder to (Integer to) {
            this.limit = to;
            return this;
        }

        Builder sortBy (String col, String direction) {
            if (!"ASC".equalsIgnoreCase(direction) && !"DESC".equalsIgnoreCase(direction)) {
                throw new IllegalArgumentException("Invalid direction value " + direction);
            }

            this.sort = col;
            this.direction = direction.toUpperCase();

            return this;
        }

        RequestParams build () {
            if ((this.column != null || this.columnValue != null) && this.primaryKeyValue != null) {
                throw new IllegalStateException("For primary key and column values are incompatible");
            }

            if (this.limit != null && this.offset == null) {
                throw new IllegalStateException("For a given limit an offset is required");
            }

            if (this.limit == null && this.offset != null) {
                throw new IllegalStateException("For a given offset a limit is required");
            }

            if (this.update != null && this.primaryKey == null) {
                throw new IllegalStateException("For an update, an id is required. Use .id() to set it.");
            }

            return new RequestParams(
                resource,
                primaryKey,
                primaryKeyValue,
                column,
                columnValue,
                query,
                queryArgs,
                insert,
                update,
                page,
                limit,
                offset,
                sort,
                direction);
        }
    }

    public String getPath (final String root, final String alias) {
        if (this.insert != null) {
            return String.format("%s/%s/%s", root, alias, this.resource);
        }

        if (this.update != null) {
            return String.format("%s/%s/%s/%s", root, alias, this.resource, this.primaryKeyValue);
        }

        if (StringUtils.isNotEmpty(this.column) && StringUtils.isNotEmpty(this.columnValue)) {
            return String.format("%s/%s/%s/%s/%s", root, alias, this.resource, this.column, this.columnValue);
        }

        if (StringUtils.isNotEmpty(this.primaryKeyValue)) {
            return String.format("%s/%s/%s/%s", root, alias, this.resource, this.primaryKeyValue);
        }

        if (StringUtils.isNotEmpty(this.query)) {
            return String.format("%s/%s/%s/dynamic/%s", root, alias, this.resource, this.query);
        }

        return String.format("%s/%s/%s", root, alias, this.resource);
    }

    public NameValuePair[] getParameters () {
        final List<NameValuePair> pairs = new ArrayList<>();

        if (this.getPage() != null) {
            pairs.add(new BasicNameValuePair("page", this.getPage().toString()));
        } else if (this.getLimit() != null && this.getOffset() != null) {
            pairs.add(new BasicNameValuePair("limit", this.getLimit().toString()));
            pairs.add(new BasicNameValuePair("offset", this.getOffset().toString()));
        }

        if (this.getDirection() != null && this.getSort() != null) {
            pairs.add(new BasicNameValuePair("sort", this.getSort()));
            pairs.add(new BasicNameValuePair("dir", this.getDirection()));
        }

        if (this.query != null && this.queryArgs != null) {
            pairs.addAll(this.queryArgs);
        }

        return pairs.toArray(new NameValuePair[] {});
    }

    public String getParametersAsQueryParams () {
        return StringUtils.join(this.getParameters(), "&");
    }

    public UrlEncodedFormEntity getFormBody () throws UnsupportedEncodingException {
        if (this.update != null)
            return new UrlEncodedFormEntity(update);

        if (this.insert != null)
            return new UrlEncodedFormEntity(insert);

        throw new IllegalStateException("No form body provided");
    }

    public StringEntity getJSONBody () throws UnsupportedEncodingException, JsonProcessingException {
        if (this.update != null)
            return new StringEntity(toJSON(update));

        if (this.insert != null)
            return new StringEntity(toJSON(insert));

        throw new IllegalStateException("No form body provided");
    }

    public String toJSON (final List<NameValuePair> params) {
        final String format = "\"%s\":\"%s\"";
        final StringBuilder b = new StringBuilder("{");
        b.append(StringUtils.join(params.stream().map(p -> String.format(format, p.getName(), p.getValue())).collect(Collectors.toList()), ","));
        b.append("}");
        return b.toString();
    }

    public RequestParams(
        String resource,
        String primaryKey,
        String primaryKeyValue,
        String column,
        String columnValue,
        String query,
        List<NameValuePair> queryArgs,
        List<NameValuePair> insert,
        List<NameValuePair> update,
        Integer page,
        Integer limit,
        Integer offset,
        String sort,
        String direction) {

        this.resource = resource;
        this.primaryKey = primaryKey;
        this.primaryKeyValue = primaryKeyValue;
        this.column = column;
        this.columnValue = columnValue;
        this.query = query;
        this.queryArgs = queryArgs;
        this.insert = insert;
        this.update = update;
        this.page = page;
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
        this.direction = direction;

    }

    public String getColumn () {
        return column;
    }


    public String getColumnValue () {
        return columnValue;
    }

    public String getResource () {
        return resource;
    }

    public String getPrimaryKey () {
        return primaryKey;
    }

    public String getPrimaryKeyValue () {
        return primaryKeyValue;
    }

    public Integer getPage () {
        return page;
    }

    public Integer getLimit () {
        return limit;
    }

    public Integer getOffset () {
        return offset;
    }

    public String getSort () {
        return sort;
    }

    public String getDirection () {
        return direction;
    }

    public String getQuery () {
        return query;
    }

    public List<NameValuePair> getQueryArgs () {
        return queryArgs;
    }

    public List<NameValuePair> getInsert () {
        return insert;
    }

    public List<NameValuePair> getUpdate () {
        return update;
    }

    @Override
    public int hashCode () {
        return Objects.hash(column, columnValue, direction, limit, offset, page, primaryKey, primaryKeyValue, resource, sort, query);
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
        RequestParams other = (RequestParams) obj;
        return this.hashCode() == other.hashCode();
    }

    @Override
    public String toString () {
        return new StringBuilder().append("RequestParams [resource=").append(resource)
            .append(", primaryKey=").append(primaryKey)
            .append(", primaryKeyValue=").append(primaryKeyValue)
            .append(", column=").append(column)
            .append(", columnValue=").append(columnValue)
            .append(", query=").append(query)
            .append(", page=").append(page)
            .append(", limit=").append(limit)
            .append(", offset=").append(offset)
            .append(", sort=").append(sort)
            .append(", direction=").append(direction)
            .append("]")
            .toString();
    }



}
