package com.to_do_list_app.model;



import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

@Setter
@Getter
public class Image {

    private int id;
    private String fileName;
    private String path;


    public Image() {
    }

    public JSONObject toJson() {
        JSONObject res = new JSONObject();
        res.put("id", getId());
        res.put("fileName", getFileName());
        res.put("path", getPath());

        return res;
    }

    public String toJsonString() {
        return toJson().toString();
    }
}


