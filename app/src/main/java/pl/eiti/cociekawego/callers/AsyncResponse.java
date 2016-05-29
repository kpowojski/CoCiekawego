package pl.eiti.cociekawego.callers;

import org.json.JSONObject;

/**
 * Created by krystian on 2016-05-11.
 */
public interface AsyncResponse {
    public boolean processFinish(JSONObject result);
}
