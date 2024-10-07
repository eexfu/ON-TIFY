package be.kuleuven.gt.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class Group {

    //instantiations

    private String name;
    private int accountid;
    private String selectedGroup;

    //Constructors

    public Group(String name, int accountid)
    {
        this.name = name;
        this.accountid = accountid;
        this.selectedGroup = "";
    }

    public Group(String name, int accountid, String selectedGroup) {
        this.name = name;
        this.accountid = accountid;
        this.selectedGroup = selectedGroup;
    }

    public Group(int accountid, String selectedGroup) {
        this.name = selectedGroup;
        this.accountid = accountid;
        this.selectedGroup = selectedGroup;
    }

    //JSON setup

    public Map<String, String> getPostParameters() {
        Map<String,String> params = new HashMap<>();
        params.put("name",name);
        params.put("accountid",accountid+"");
        return params;
    }

    public Map<String, String> getPostParametersEdit() {
        Map<String,String> params = new HashMap<>();
        if (name.contains(" ")){
            name = name.replace(" ","-_-_-");
        }
        if(selectedGroup.contains(" ")){
            selectedGroup = selectedGroup.replace(" ","-_-_-");
        }
        params.put("newname",name);
        params.put("accountid",accountid+"");
        params.put("name",selectedGroup);
        return params;
    }

    public Map<String, String> getPostParametersDelete() {
        Map<String,String> params = new HashMap<>();
        params.put("name",selectedGroup);
        params.put("accountid",accountid+"");
        return params;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", accountid=" + accountid +
                ", selectedGroup='" + selectedGroup + '\'' +
                '}';
    }

    //Setters and Getters

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
