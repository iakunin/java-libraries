package dev.iakunin.library.tests.dbunit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

@Component
public class PostgresqlDataTypeFactoryWithJsonb extends PostgresqlDataTypeFactory {
    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if ("jsonb".equals(sqlTypeName)) {
            return new JsonbDataType();
        } else {
            return super.createDataType(sqlType, sqlTypeName);
        }
    }

    public static class JsonbDataType extends AbstractDataType {
        private final ObjectMapper objectMapper = new ObjectMapper();

        public JsonbDataType() {
            super("jsonb", Types.OTHER, String.class, false);
        }

        @Override
        @SneakyThrows
        public Object typeCast(Object obj) {
            if (obj == null) {
                return null;
            }

            if (obj instanceof String str) {
                return ComparableJsonNode.of(objectMapper.readTree(str));
            }

            if (obj instanceof Map || obj instanceof List<?>) {
                return ComparableJsonNode.of(objectMapper.valueToTree(obj));
            }

            return obj.toString();
        }

        @Override
        public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException {
            return resultSet.getString(column);
        }

        @Override
        public void setSqlValue(
            Object value,
            int column,
            PreparedStatement statement
        )
            throws SQLException {
            final PGobject jsonObj = new PGobject();
            jsonObj.setType("json");
            jsonObj.setValue(value == null ? null : value.toString());

            statement.setObject(column, jsonObj);
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    public static class ComparableJsonNode implements Comparable<ComparableJsonNode> {
        private final JsonNode jsonNode;

        @Override
        public int compareTo(
            PostgresqlDataTypeFactoryWithJsonb.ComparableJsonNode comparableJsonNode
        ) {
            return this.jsonNode.equals(comparableJsonNode.jsonNode) ? 0 : 1;
        }
    }
}
