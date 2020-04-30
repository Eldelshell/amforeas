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

package org.amforeas.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import amforeas.exceptions.AmforeasBadRequestException;
import amforeas.jdbc.StoredProcedureParam;

@Tag("offline-tests")
public class StoredProcedureParamTest {

    @Test
    public void test_getSqlType () throws AmforeasBadRequestException {
        assertEquals(java.sql.Types.INTEGER, generateStoredProcedureParamByType("INTEGER").getSqlType());
        assertEquals(java.sql.Types.VARCHAR, generateStoredProcedureParamByType("varcHar").getSqlType());

        assertThrows(AmforeasBadRequestException.class, () -> {
            generateStoredProcedureParamByType("INTEGER2").getSqlType();
        });
    }

    @Test
    public void test_equals () {
        assertEquals(generateStoredProcedureParamByType("INTEGER"), generateStoredProcedureParamByType("INTEGER"));
        assertEquals(generateStoredProcedureParamByType("INTEGER"), generateStoredProcedureParamByType("integer"));
        assertNotEquals(generateStoredProcedureParamByType("varchar"), generateStoredProcedureParamByType("INTEGER"));
    }

    private StoredProcedureParam generateStoredProcedureParamByType (String type) {
        return new StoredProcedureParam("car_id", "1", false, 1, type);
    }
}
