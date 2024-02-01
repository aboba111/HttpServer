package org.example;

import org.json.simple.JSONObject;

import java.io.InputStream;

public class JsonRequest {
    private JSONObject object;
    public JsonRequest(JSONObject object){
        this.object=object;

    }

    public JSONObject authSuccess(int rating, int building_flag1,
                                  int building_flag2, int building_flag3, int
                                  copper, int iron, int gold, int money,
                                  String token){
        object.put("request",1);
        object.put("token", token);
        object.put("rating", rating);
        object.put("building_flag1",building_flag1);
        object.put("building_flag2", building_flag2);
        object.put("building_flag3", building_flag3);
        object.put("copper",copper);
        object.put("iron", iron);
        object.put("gold", gold);
        object.put("money", money);
        return object;
    }


    public JSONObject authError(){
        object.put("request",-1);
        object.put("token", "zero");
        return object;
    }
    public JSONObject updateError(){
        object.put("request",-2);
        object.put("token", "zero");
        return object;
    }

    public JSONObject updateOk(){
        object.put("request",-1);
        object.put("token", "zero");
        return object;
    }



}
