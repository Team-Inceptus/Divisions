package us.teaminceptus.divisions.api;

import us.teaminceptus.divisions.api.division.Division;

class TestDivConfig implements DivConfig {
    @Override
    public String get(String key) {
        return "";
    }

    @Override
    public String getMessage(String key) {
        return "";
    }

    @Override
    public String getLanguage() {
        return "en";
    }

    @Override
    public int getMaxDivisionSize() {
        return Division.MAX_PLAYERS;
    }
}
