
package com.dinstone.ireader.domain;

import java.util.ArrayList;
import java.util.List;

public class AppVersion {

    private String version;

    private String description;

    private String url;

    private String sign;

    private List<AppPatch> patches = new ArrayList<AppPatch>();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<AppPatch> getPatches() {
        return patches;
    }

    public void setPatches(List<AppPatch> patches) {
        if (patches != null) {
            this.patches.addAll(patches);
        }
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
