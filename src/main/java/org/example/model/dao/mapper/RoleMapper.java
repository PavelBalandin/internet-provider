package org.example.model.dao.mapper;

import org.example.model.entity.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class RoleMapper implements ObjectMapper<Role> {
    @Override
    public Role extractFromResultSet(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getInt("role_id"));
        role.setName(rs.getString("role_name"));
        return role;
    }

    @Override
    public Role makeUnique(Map<Integer, Role> cache, Role role) {
        cache.putIfAbsent(role.getId(), role);
        return cache.get(role.getId());
    }
}
