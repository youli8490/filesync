package youli.open.filesync.sync;

import java.util.LinkedList;
import java.util.List;

public class SyncFilter {
    public static final String Default_Filter = "Default(空)";
    public static final String Name_Filter = "=";
    public static final String Black_White = "|";
    public static final String Regex_Black_White = "\\|";
    public static final String Filter_Separator = ";";
    public static final String Verticle_Line = "&vtcl;";

    private String filterName;
    private List<String> syncFilterBlack;
    private List<String> syncFilterWhite;

    public static SyncFilter createDefaultSyncFilter(){
        SyncFilter filter = new SyncFilter();
        filter.setFilterName(Default_Filter);
        filter.setSyncFilterBlack(new LinkedList<String>());
        filter.setSyncFilterWhite(new LinkedList<String>());
        return filter;
    }
    
    public static SyncFilter instance(String str){
        SyncFilter filter = null;
        if(str == null)
            return filter;
        String[] nameFilter = str.split(Name_Filter);
        if(nameFilter.length != 2 || nameFilter[0].length() == 0)
            return filter;
        
        filter = new SyncFilter();
        filter.setFilterName(nameFilter[0]);
        
        String[] blackWhite = nameFilter[1].split(Regex_Black_White);
        String[] blacks = blackWhite[0].split(Filter_Separator);
        List<String> blackList = new LinkedList<String>();
        for(String black : blacks){
            if(black.length() != 0)
                blackList.add(black.replaceAll(Verticle_Line, Black_White));
        }
        filter.setSyncFilterBlack(blackList);
        
        String[] whites = blackWhite[1].split(Filter_Separator);
        List<String> whiteList = new LinkedList<String>();
        for(String white : whites){
            if(white.length() != 0)
                whiteList.add(white.replaceAll(Verticle_Line, Black_White));
        }
        filter.setSyncFilterWhite(whiteList);
        return filter;
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(filterName).append(Name_Filter);
        if(syncFilterBlack.size() == 0)//黑名单
            buffer.append(Filter_Separator);
        else{
            for(int i = 0; i < syncFilterBlack.size(); i++){
                buffer.append(syncFilterBlack.get(i).replaceAll(Regex_Black_White, Verticle_Line));
                buffer.append(Filter_Separator);
            }
        }
        buffer.append(Black_White);
        if(syncFilterWhite.size() == 0)//白名单
            buffer.append(Filter_Separator);
        else{
            for(int i = 0; i < syncFilterWhite.size(); i++){
                buffer.append(syncFilterWhite.get(i).replaceAll(Regex_Black_White, Verticle_Line));
                buffer.append(Filter_Separator);
            }
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        System.out.println(";|;".split("\\|").length);
    }

    public List<String> getSyncFilterBlack() {
        return syncFilterBlack;
    }

    public void setSyncFilterBlack(List<String> syncFilterBlack) {
        this.syncFilterBlack = syncFilterBlack;
    }

    public List<String> getSyncFilterWhite() {
        return syncFilterWhite;
    }

    public void setSyncFilterWhite(List<String> syncFilterWhite) {
        this.syncFilterWhite = syncFilterWhite;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

}
