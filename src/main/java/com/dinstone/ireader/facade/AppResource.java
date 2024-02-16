
package com.dinstone.ireader.facade;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinstone.ireader.domain.AppPatch;
import com.dinstone.ireader.domain.AppVersion;

@RestController
@RequestMapping("/app")
public class AppResource {

    private AppVersion appVersion;

    @GetMapping("/patch/{version}/{patch}")
    public List<AppPatch> patch(@PathVariable String version, @PathVariable String patch) {
        int pv = 0;
        if (patch != null) {
            try {
                pv = Integer.parseInt(patch);
            } catch (NumberFormatException e) {
                // ignore;
            }
        }

        List<AppPatch> patchList = new ArrayList<AppPatch>();
        if (appVersion.getVersion().equals(version)) {
            for (AppPatch appPatch : appVersion.getPatches()) {
                if (pv < appPatch.getVersion()) {
                    patchList.add(appPatch);
                }
            }
        }

        return patchList;
    }

    @GetMapping("/check/{appVersion}")
    public AppVersion check(@PathVariable("appVersion") String version) {
        if (version == null || version.isEmpty()) {
            return null;
        }

        if (needUpdate(version, appVersion.getVersion())) {
            return appVersion;
        }

        return null;
    }

    public static boolean needUpdate(String currentVersion, String expectedVersion) {
        if (currentVersion.equals(expectedVersion)) {
            return false;
        }

        String[] currentParts = currentVersion.split("\\.");
        String[] updateParts = expectedVersion.split("\\.");

        int length = currentParts.length < updateParts.length ? currentParts.length : updateParts.length;
        for (int i = 0; i < length; i++) {
            if (Integer.parseInt(updateParts[i]) > Integer.parseInt(currentParts[i])) {
                return true;
            } else if (Integer.parseInt(updateParts[i]) < Integer.parseInt(currentParts[i])) {
                return false;
            }
            // 相等 比较下一组值
        }

        return true;
    }
}
