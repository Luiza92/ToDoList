package com.to_do_list_app.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.security.core.GrantedAuthority;

@Setter
@Getter
public class Role  implements GrantedAuthority  {


    private int id;
    private String name;

    public Role() {
    }

    public Role(JSONObject data)
    {
        id = data.getInt("id");
        name = data.getString("name");

    }

    @Override
    public String getAuthority() {
        return Integer.toString(id);
    }
}
