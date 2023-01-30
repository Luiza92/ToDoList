package com.to_do_list_app.repository;

import com.to_do_list_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepo implements UserRepository{
@Autowired
RoleRepo roleRepo ;

    private final JdbcTemplate jdbcTemplate;

    public UserRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insert(User user) throws SQLException {

        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement(Query.userAdd, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getFirstName());
                statement.setString(3, user.getLastName());
                statement.setString(4, user.getEmail());
                statement.setString(5, user.getPassword());
                statement.setTimestamp(6, user.getCreatedAt());
                statement.setTimestamp(7, user.getUpdatedAt());
                statement.setInt(8, user.getImageId());
                statement.setInt(9, user.getRoleId());
                statement.setInt(10, user.getStatus());
                return statement;
            }
        }, holder);

        return ((Long) holder.getKey().longValue()).intValue();
    }
    @Override
    public User get(int id) throws SQLException {
        System.err.println("user id " + id);

        String sql = (Query.userGet);
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        User user = new User();

        user.setUsername((String) result.get("username"));
        user.setFirstName((String) result.get("first_name"));
        user.setLastName((String) result.get("last_name"));
        user.setEmail((String) result.get("email"));
        user.setPassword((String) result.get("password"));
        user.setCreatedAt((Timestamp) result.get("created_at"));
        user.setUpdatedAt((Timestamp) result.get("updated_at"));
        user.setImageId((int) result.get("image_id"));
        user.setRoleId((int) result.get("role_id"));
        user.setStatus((int) result.get("status"));


            Integer user_id = result.get("id") != null ? ((Integer) result.get("id")).intValue() : null;
            user.setId(user_id);

        return user;
    }

    @Override
    public User getByUsername(String username) throws SQLException {
        System.err.println("username  " + username);

        return jdbcTemplate.query(
                Query.userGetByUsername,
                new ResultSetExtractor<User>() {
                    @Override
                    public User extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {
                            User u = new User();
                            u.setId(rs.getInt("id"));
                            u.setUsername(rs.getString("username"));
                            u.setFirstName(rs.getString("first_name"));
                            u.setLastName(rs.getString("last_name"));
                            u.setEmail(rs.getString("email"));
                            u.setPassword(rs.getString("password"));
                            u.setCreatedAt(rs.getTimestamp("created_at"));
                            u.setUpdatedAt(rs.getTimestamp("updated_at"));
                            u.setImageId(rs.getInt("image_Id"));
                            u.setRoleId(rs.getInt("role_id"));
                            u.setRole(roleRepo.get(rs.getInt("role_id")));
                            u.setStatus(rs.getInt("status"));
                            return u;
                        }

                        return null;
                    }
                },
                username);
    }

    @Override
    public User getByUsernameAndId(String username, int id) throws SQLException {
        System.err.println("username  " + username);

        return jdbcTemplate.query(
                Query.userGetByUsernameAndId,
                new ResultSetExtractor<User>() {
                    @Override
                    public User extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {
                            User u = new User();
                            u.setId(rs.getInt("id"));
                            u.setUsername(rs.getString("username"));
                            u.setFirstName(rs.getString("first_name"));
                            u.setLastName(rs.getString("last_name"));
                            u.setEmail(rs.getString("email"));
                            u.setPassword(rs.getString("password"));
                            u.setCreatedAt(rs.getTimestamp("created_at"));
                            u.setUpdatedAt(rs.getTimestamp("updated_at"));
                            u.setImageId(rs.getInt("image_id"));
                            u.setRoleId(rs.getInt("role_id"));
                            u.setStatus(rs.getInt("status"));
                            return u;
                        }

                        return null;
                    }
                },
                username, id );
    }

    @Override
    public User getByEmail(String email) throws SQLException {
      System.err.println("email  " + email);

        return jdbcTemplate.query(
                Query.userGetByEmail,

                new ResultSetExtractor<User>() {
                    @Override
                    public User extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {
                            User u = new User();
                            u.setId(rs.getInt("id"));
                            u.setUsername(rs.getString("username"));
                            u.setFirstName(rs.getString("first_name"));
                            u.setLastName(rs.getString("last_name"));
                            u.setEmail(rs.getString("email"));
                            u.setPassword(rs.getString("password"));
                            u.setCreatedAt(rs.getTimestamp("created_at"));
                            u.setUpdatedAt(rs.getTimestamp("updated_at"));
                            u.setImageId(rs.getInt("image_id"));
                            u.setRoleId(rs.getInt("role_id"));
                            u.setStatus(rs.getInt("status"));
                            return u;
                        }

                        return null;
                    }
                },
                email);
    }

    @Override
    public User getByEmailAndId(String email, int id) throws SQLException {
        return null;
    }

    @Override
    public User getByUserId(int userId) throws SQLException {
        return jdbcTemplate.query(
                Query.userGet,
                new ResultSetExtractor<User>() {
                    @Override
                    public User extractData(ResultSet rs) throws SQLException,
                            DataAccessException {
                        if (rs.next()) {
                            User u = new User();
                            u.setId(rs.getInt("id"));
                            u.setUsername(rs.getString("username"));
                            u.setFirstName(rs.getString("first_name"));
                            u.setLastName(rs.getString("last_name"));
                            u.setEmail(rs.getString("email"));
                            u.setPassword(rs.getString("password"));
                            u.setCreatedAt(rs.getTimestamp("created_at"));
                            u.setUpdatedAt(rs.getTimestamp("updated_at"));
                            u.setImageId(rs.getInt("image_id"));
                            u.setRoleId(rs.getInt("role_id"));
                            u.setStatus(rs.getInt("status"));
                            return u;
                        }
                        return null;
                    }
                },
                userId);
    }


    @Override
    public boolean delete(int id) throws SQLException {
        String sql = Query.userDelete;
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            System.out.println("user has been  delete.");
            return false;
        }
        return true;
    }


    @Override
    public int update(User user) throws SQLException {
        String sql = Query.userUpdate;
        System.err.println(user.getUsername() + ", " + user.getFirstName() + ", " + user.getLastName() + "," + user.getEmail() + ", " + user.getPassword() + ", " + user.getUpdatedAt() + ", " + user.getImageId() + ", " + user.getRoleId() + ", " + user.getStatus() + ", " + user.getId());
        int result = jdbcTemplate.update(sql, user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getUpdatedAt(), user.getImageId(), user.getRoleId() , user.getStatus(), user.getId());
        if (result > 0) {
            System.out.println("user has been update.");
            return user.getId();
        }
        return -1;
    }

    @Override
    public List<User> getAllUsers(String limit, String skip) {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(
                Query.getAllUsers, Integer.parseInt(limit), Integer.parseInt(skip));
        List<User> users = new ArrayList<>();
        result.forEach(user1 -> {

            User user = new User();

            user.setId((int) user1.get("id"));
            user.setUsername((String) user1.get("username"));
            user.setFirstName((String) user1.get("first_name"));
            user.setLastName((String) user1.get("last_name"));
            user.setEmail((String) user1.get("email"));
            user.setCreatedAt((Timestamp) user1.get("created_at"));
            user.setUpdatedAt((Timestamp) user1.get("updated_at"));
            user.setPassword((String) user1.get("password"));
            user.setImageId((int) user1.get("image_id"));
            user.setRoleId((int) user1.get("role_id"));
            user.setStatus((int) user1.get("status"));


            users.add(user);
        });


        System.out.println("users " + users);
        return users;
    }
}
