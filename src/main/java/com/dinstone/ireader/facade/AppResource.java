
package com.dinstone.ireader.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Service;

@Service
@Path("/app")
@Produces(MediaType.APPLICATION_JSON)
public class AppResource {

    @GET
    @Path("/patch/{appVersion}/{patchVersion}")
    public List<Map<String, String>> patch(@PathParam("appVersion") String appVersion,
            @PathParam("patchVersion") String patchVersion) {
        List<Map<String, String>> patchList = new ArrayList<Map<String, String>>();

        int pv = 0;
        if (patchVersion != null) {
            try {
                pv = Integer.parseInt(patchVersion);
            } catch (NumberFormatException e) {
                // ignore;
            }
        }

        for (int i = 1; i < 5; i++) {
            if (pv < i) {
                HashMap<String, String> patchInfoMap = new HashMap<String, String>();
                patchInfoMap.put("appVersionName", appVersion);
                patchInfoMap.put("patchVersionName", "" + i);
                patchInfoMap.put("patchFileUrl", "" + (pv + i));
                patchInfoMap.put("patchFileMd5", "" + (pv + i));

                patchList.add(patchInfoMap);
            }
        }

        return patchList;
    }
}
