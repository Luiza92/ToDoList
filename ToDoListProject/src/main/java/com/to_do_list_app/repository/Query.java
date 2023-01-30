package com.to_do_list_app.repository;

public class Query {


    public static String imageAdd = ("insert into images (file_name, path) values (?,?)");
    public static String imageGet = ("select * from images where id = ? ");
    public static String imageDelete = ("delete from images where id = ?");
    public static String imageUpdate = ("update images set file_name =?, path =? where id = ?");


    public static String toDoListAdd = ("insert into to_do_list (id, title, description, created_at, expired_at, is_complete, user_id) values (?,?,?,?,?,?,?)");
    public static String toDoListGet = ("select * from to_do_list where id = ? ");

    String query = "select count(*) from users";
    public static String GetAllToDoList = ("select * from to_do_list where user_id = ?");
    public static String toDoListDelete = ("delete from to_do_list where id = ?");
    public static String toDoListDeleteByUserId = ("delete from to_do_list where user_id = ?");
    public static String toDoListUpdate = ("update to_do_list set title =?, description =?, created_at =?,expired_at =?, is_complete =? , user_id =?  where id = ?");



    public static String userAdd = ("insert into users (username, first_name, last_name, email, password, created_at ,updated_at, image_id, role_id,  status ) values (?,?,?,?,?,?,?,?,?,?)");
    public static String userGet = ("select * from users where id = ?");
    public static String getAllUsers = ("select * from users LIMIT ? OFFSET ?");
    public static String userGetByUsername = ("select * from users where username = ?");
    public static String userGetByUsernameAndId = ("select * from users where username = ? and id = ?");
    public static String GetByRefreshToken = ("select * from refresh_token where token = ?");
    public static String GetByAccessToken = ("select * from access_token where token = ?");
    public static String userGetByEmail = ("select * from users where email = ?");
    public static String userGetByEmailAndId = ("select * from users where email = ? and id = ? ");
    public static String userDelete = ("delete from users where id = ?");
    public static String userUpdate = ("update users set username = ?, first_name=?, last_name=?, email = ?, password = ?, updated_at =?, image_id =? , role_id =?, status =? where id = ?");



    public static String accessTokenGet = (" SELECT access_token.*, refresh_token.`token` AS refreshToken\n" +
            "                    FROM access_token\n" +
            "                    LEFT JOIN refresh_token ON access_token.`refresh_token_id` = refresh_token.`id`\n" +
            "                   WHERE access_token.`id` = ?");

    public static String getByAccessToken = (" SELECT access_token.*, refresh_token.`token` AS refreshToken\n" +
            "                                FROM access_token\n" +
            "                               LEFT JOIN refresh_token ON access_token.`refresh_token_id` = refresh_token.`id`\n" +
            "                              WHERE access_token.`token` = ?");

    public static String accessTokenInsert = ("insert into access_token (user_id, token, `created_at`, expired_at, refresh_token_id) values (?,?,?,?,?)");
    public static String accessTokenDelete = ("delete from access_token where id = ?");
    public static String deleteByAccessToken = ("delete from access_token where token = ?");
    public static String deleteByRefreshToken = ("delete from refresh_token where token = ?");
    public static String accessTokenUpdate = ("update access_token set user_id = ?, token = ?, created_at = ?, expired_at = ?, refresh_token_id = ? ,   where id = ?");
    public static String accessTokenDeleteByUserId = ("delete  from access_token where user_id = ?");

    public static String refreshTokenGet = ("select * from refresh_token where id = ? ");
    public static String refreshTokenInsert = (" insert into refresh_token (user_id, token, `created_at`,expired_at) values (?,?,?,?)");
    public static String refreshTokenDelete = ("delete from refresh_token where id = ?");
    public static String refreshTokenUpdate = ("update refresh_token set user_id = ?, token = ?, created_at = ?, expired_at = ?, where id = ?");
    public static String refreshTokenDeleteByUserId = ("delete  from refresh_token where user_id = ?");

    public static String approveDeleteByUserId = ("delete  from approve where user_id = ?");
    public static String approveGet = ("select * from approve where id = ?");
    public static String getByRandomId = ("select * from approve where random_id = ?");
    public static String approveAdd = ("insert into approve ( user_id, random_id, time_expires) values (?,?,?)");
    public static String approveUpdate = ("update approve set user_id = ?, random_id = ?, time_expires = ?, where id = ?");

    public static String permissionGet = ("SELECT roles.`name` AS role_name, roles.`id`, permissions.`name`,permissions.`enable`\n" +
            "                                     FROM permissions\n" +
            "                                     LEFT JOIN roles ON permissions.`role_id` = roles.`id`\n" +
            "                                WHERE roles.`id` = ? \n");

    public static String getUserPermission = ("SELECT ENABLE\n" +
            "FROM users \n" +
            "LEFT JOIN permissions ON users.`role_id` = permissions.`role_id`\n" +
            "WHERE users.`id` = ? AND permissions.`name` = ?");

    public static String roleGet = ( " Select * from roles where id = ?");

}
