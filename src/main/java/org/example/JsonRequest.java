package org.example;

import org.json.simple.JSONObject;

import java.io.InputStream;

public class JsonRequest {
    private JSONObject object;
    public JsonRequest(JSONObject object){
        this.object=object;

    }

    public JSONObject authSuccess(int countDiamonds, int countGold){
        object.put("request",1);
        object.put("diamond",countDiamonds);
        object.put("gold",countGold);
        return object;
    }
    public JSONObject createUser(int countDiamonds, int countGold){
        object.put("request",1);
        object.put("diamond",countDiamonds);
        object.put("gold",countGold);
        return object;
    }

    public JSONObject authError(){
        object.put("request",-1);
        object.put("diamond",0);
        object.put("gold",0);
        return object;
    }
}
