package com.benlefevre.go4lunch.models;

public class AutoCompleteItem {

    private String mName;
    private String mAddress;

    public AutoCompleteItem(String name, String address) {
        mName = name;
        mAddress = address;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    @Override
    public String toString() {
        return mName;
    }
}
