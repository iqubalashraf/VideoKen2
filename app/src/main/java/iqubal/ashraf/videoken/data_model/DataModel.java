package iqubal.ashraf.videoken.data_model;

/**
 * Created by ashrafiqubal on 29/04/17.
 */

public class DataModel {
    int NOTE_TYPE;
    String YOUTUBE_ID,VOICE_CLIP_NAME,START_TIME,DURATION,VOICE_IN_STRING;
    public int getNOTE_TYPE(){
        return NOTE_TYPE;
    }
    public void setNOTE_TYPE(int NOTE_TYPE){
        this.NOTE_TYPE = NOTE_TYPE;
    }
    public String getYOUTUBE_ID(){
        return YOUTUBE_ID;
    }
    public void setYOUTUBE_ID(String YOUTUBE_ID){
        this.YOUTUBE_ID = YOUTUBE_ID;
    }
    public String getVOICE_CLIP_NAME(){
        return VOICE_CLIP_NAME;
    }
    public void setVOICE_CLIP_NAME(String VOICE_CLIP_NAME){
        this.VOICE_CLIP_NAME = VOICE_CLIP_NAME;
    }
    public String getSTART_TIME(){
        return START_TIME;
    }
    public void setSTART_TIME(String START_TIME){
        this.START_TIME = START_TIME;
    }
    public String getDURATION(){
        return DURATION;
    }
    public void setDURATION(String DURATION){
        this.DURATION = DURATION;
    }
    public String getVOICE_IN_STRING(){
        return VOICE_IN_STRING;
    }
    public void setVOICE_IN_STRING(String VOICE_IN_STRING){
        this.VOICE_IN_STRING = VOICE_IN_STRING;
    }
}
